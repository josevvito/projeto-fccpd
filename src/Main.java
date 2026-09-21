import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    // Executa a mesma carga de calculo para cada elemento da matriz.
    private static double calcular(double valor) {
        double resultado = valor;

        for (int i = 0; i < 1000; i++) {
            resultado += Math.sin(valor + i)
                    * Math.cos(valor - i)
                    * Math.sqrt(Math.abs(valor) + 1);
        }

        return resultado;
    }

    // Percorre toda a matriz sequencialmente, linha por linha.
    private static double processar(double[][] matriz) {
        double resultado = 0.0;

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                resultado += calcular(matriz[i][j]);
            }
        }

        return resultado;
    }

    private static double processarParalelo(double[][] matriz, int quantidadeTarefas)
            throws InterruptedException, ExecutionException {
        if (quantidadeTarefas != 5 && quantidadeTarefas != 10 && quantidadeTarefas != 100) {
            throw new IllegalArgumentException("Use 5, 10 ou 100 tarefas.");
        }

        int quantidadeThreads = Math.min(quantidadeTarefas,
                Runtime.getRuntime().availableProcessors());
        ExecutorService executor = Executors.newFixedThreadPool(quantidadeThreads);
        List<Future<Double>> resultados = new ArrayList<>();
        boolean concluido = false;

        try {
            for (int tarefa = 0; tarefa < quantidadeTarefas; tarefa++) {
                final int inicio = (int) ((long) tarefa * matriz.length / quantidadeTarefas);
                final int fim = (int) ((long) (tarefa + 1) * matriz.length / quantidadeTarefas);

                resultados.add(executor.submit(() -> {
                    double parcial = 0.0;
                    for (int i = inicio; i < fim; i++) {
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException("Processamento interrompido.");
                        }
                        for (int j = 0; j < matriz[i].length; j++) {
                            parcial += calcular(matriz[i][j]);
                        }
                    }
                    return parcial;
                }));
            }

            // Combina as somas locais na ordem de envio das tarefas.
            double resultado = 0.0;
            for (Future<Double> parcial : resultados) {
                resultado += parcial.get();
            }
            concluido = true;
            return resultado;
        } finally {
            if (!concluido) {
                for (Future<Double> parcial : resultados) {
                    parcial.cancel(true);
                }
            }
            executor.shutdown();
        }
    }

    // Os valores dependem apenas da posicao e ficam entre 0,0001 e 0,0100.
    private static double[][] gerarMatriz(int linhas, int colunas) {
        double[][] matriz = new double[linhas][colunas];

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                int valorBase = ((i + 1) * 31 + (j + 1) * 17) % 100;
                matriz[i][j] = (valorBase + 1) / 10000.0;
            }
        }

        return matriz;
    }

    private static void executarProcessamento(int linhas, int colunas) {
        executarProcessamento(linhas, colunas, 0);
    }

    private static void executarProcessamento(int linhas, int colunas, int quantidadeTarefas) {
        System.out.println();
        System.out.println("==========================================");
        System.out.println(quantidadeTarefas == 0
                ? "       PROCESSAMENTO SEQUENCIAL"
                : "       PROCESSAMENTO COM EXECUTORSERVICE");
        System.out.println("==========================================");
        System.out.println("Matriz: " + linhas + " x " + colunas);
        if (quantidadeTarefas != 0) {
            System.out.println("Tarefas: " + quantidadeTarefas);
        }

        long quantidadeElementos = (long) linhas * colunas;
        System.out.println("Elementos: " + quantidadeElementos);
        System.out.println("Gerando matriz...");

        double[][] matriz = gerarMatriz(linhas, colunas);

        System.out.println("Matriz criada.");

        // Mede apenas o processamento, sem a criacao da matriz ou a saida.
        long inicio = System.nanoTime();
        double resultado;
        try {
            resultado = quantidadeTarefas == 0
                    ? processar(matriz)
                    : processarParalelo(matriz, quantidadeTarefas);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Processamento interrompido.");
            return;
        } catch (ExecutionException e) {
            System.out.println("Falha no processamento: " + e.getCause());
            return;
        }
        long fim = System.nanoTime();

        long tempoNano = fim - inicio;
        double tempoMs = tempoNano / 1_000_000.0;
        double tempoSegundos = tempoNano / 1_000_000_000.0;

        System.out.println();
        System.out.println("------------------------------------------");
        System.out.printf("Resultado: %.6f%n", resultado);
        System.out.printf("Tempo: %.3f ms%n", tempoMs);
        System.out.printf("Tempo: %.3f segundos%n", tempoSegundos);
        System.out.println("------------------------------------------");
        System.out.println();
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("      PROJETO DE COMPUTACAO PARALELA");
        System.out.println("==========================================");
        System.out.println("1 - Matriz 500 x 500");
        System.out.println("2 - Matriz 1000 x 1000");
        System.out.println("3 - Matriz 1500 x 1500");
        System.out.println("4 - Matriz 2000 x 2000");
        System.out.println("5 - V2: ExecutorService (5, 10 ou 100 tarefas)");
        System.out.println("0 - Sair");
        System.out.println("==========================================");
        System.out.print("Escolha uma opcao: ");
    }

    private static void escolherProcessamentoParalelo(Scanner scanner) {
        System.out.print("Tamanho da matriz (500, 1000, 1500 ou 2000): ");
        if (!scanner.hasNextInt()) {
            if (scanner.hasNext()) {
                scanner.next();
            }
            System.out.println("Tamanho invalido!");
            return;
        }
        int tamanho = scanner.nextInt();
        if (tamanho != 500 && tamanho != 1000 && tamanho != 1500 && tamanho != 2000) {
            System.out.println("Tamanho invalido!");
            return;
        }

        System.out.print("Quantidade de tarefas (5, 10 ou 100): ");
        if (!scanner.hasNextInt()) {
            if (scanner.hasNext()) {
                scanner.next();
            }
            System.out.println("Quantidade de tarefas invalida!");
            return;
        }
        int quantidadeTarefas = scanner.nextInt();
        if (quantidadeTarefas != 5 && quantidadeTarefas != 10 && quantidadeTarefas != 100) {
            System.out.println("Quantidade de tarefas invalida!");
            return;
        }
        executarProcessamento(tamanho, tamanho, quantidadeTarefas);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        do {
            exibirMenu();

            if (!scanner.hasNext()) {
                break;
            }
            if (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println("Opcao invalida!");
                continue;
            }

            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    executarProcessamento(500, 500);
                    break;
                case 2:
                    executarProcessamento(1000, 1000);
                    break;
                case 3:
                    executarProcessamento(1500, 1500);
                    break;
                case 4:
                    executarProcessamento(2000, 2000);
                    break;
                case 5:
                    escolherProcessamentoParalelo(scanner);
                    break;
                case 0:
                    System.out.println("Encerrando o programa...");
                    break;
                default:
                    System.out.println("Opcao invalida!");
                    break;
            }
        } while (opcao != 0 && !Thread.currentThread().isInterrupted());

        scanner.close();
        System.out.println("Programa encerrado.");
    }
}
