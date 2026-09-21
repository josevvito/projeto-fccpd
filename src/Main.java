import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.time.OffsetDateTime;
import java.util.Locale;

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

    private static double processarEstruturado(double[][] matriz, int quantidadeTarefas)
            throws InterruptedException {
        if (quantidadeTarefas != 5 && quantidadeTarefas != 10 && quantidadeTarefas != 100) {
            throw new IllegalArgumentException("Use 5, 10 ou 100 tarefas.");
        }

        // O fechamento do escopo aguarda o termino de todas as subtarefas.
        try (var scope = StructuredTaskScope.<Double>open()) {
            List<StructuredTaskScope.Subtask<Double>> resultados = new ArrayList<>();

            for (int tarefa = 0; tarefa < quantidadeTarefas; tarefa++) {
                final int inicio = (int) ((long) tarefa * matriz.length / quantidadeTarefas);
                final int fim = (int) ((long) (tarefa + 1) * matriz.length / quantidadeTarefas);

                resultados.add(scope.fork(() -> {
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

            // Se uma subtarefa falhar, o escopo cancela as demais e propaga a falha.
            scope.join();

            double resultado = 0.0;
            for (StructuredTaskScope.Subtask<Double> parcial : resultados) {
                resultado += parcial.get();
            }
            return resultado;
        }
    }

    private static double processarEstadoCompartilhado(double[][] matriz, int quantidadeTarefas)
            throws InterruptedException {
        if (quantidadeTarefas != 5 && quantidadeTarefas != 10 && quantidadeTarefas != 100) {
            throw new IllegalArgumentException("Use 5, 10 ou 100 tarefas.");
        }

        // Uma fila por execucao, compartilhada apenas pelas subtarefas deste escopo.
        ConcurrentLinkedQueue<Double> resultados = new ConcurrentLinkedQueue<>();
        try (var scope = StructuredTaskScope.<Void>open()) {
            for (int tarefa = 0; tarefa < quantidadeTarefas; tarefa++) {
                final int inicio = (int) ((long) tarefa * matriz.length / quantidadeTarefas);
                final int fim = (int) ((long) (tarefa + 1) * matriz.length / quantidadeTarefas);

                scope.fork(() -> {
                    double parcial = 0.0;
                    for (int i = inicio; i < fim; i++) {
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException("Processamento interrompido.");
                        }
                        for (int j = 0; j < matriz[i].length; j++) {
                            parcial += calcular(matriz[i][j]);
                        }
                    }
                    resultados.add(parcial);
                    return null;
                });
            }

            // Somente combina a fila se todas as subtarefas terminarem com sucesso.
            scope.join();

            double resultado = 0.0;
            for (double parcial : resultados) {
                resultado += parcial;
            }
            return resultado;
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
        executarProcessamento(linhas, colunas, quantidadeTarefas, false);
    }

    private static void executarProcessamento(int linhas, int colunas,
            int quantidadeTarefas, boolean estruturado) {
        executarProcessamento(linhas, colunas, quantidadeTarefas, estruturado, false);
    }

    private static void executarProcessamento(int linhas, int colunas,
            int quantidadeTarefas, boolean estruturado, boolean estadoCompartilhado) {
        System.out.println();
        System.out.println("==========================================");
        System.out.println(quantidadeTarefas == 0
                ? "       PROCESSAMENTO SEQUENCIAL"
                : estadoCompartilhado ? "       V4: ESTADO COMPARTILHADO COM FILA"
                : estruturado ? "       PROCESSAMENTO COM STRUCTUREDTASKSCOPE"
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
                    : estadoCompartilhado ? processarEstadoCompartilhado(matriz, quantidadeTarefas)
                    : estruturado ? processarEstruturado(matriz, quantidadeTarefas)
                    : processarParalelo(matriz, quantidadeTarefas);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Processamento interrompido.");
            return;
        } catch (ExecutionException | StructuredTaskScope.FailedException e) {
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

    private static void escolherBenchmark(Scanner scanner) {
        System.out.println("1 - Validar somente 500x500 (100 medicoes)");
        System.out.println("2 - Todas as matrizes (400 medicoes; pode demorar bastante)");
        System.out.println("0 - Voltar");
        System.out.println("Uma nova bateria substitui o relatorio anterior, sem reutilizar tempos.");
        System.out.print("Escolha: ");
        if (!scanner.hasNextInt()) {
            if (scanner.hasNext()) scanner.next();
            System.out.println("Opcao invalida!");
            return;
        }
        int opcao = scanner.nextInt();
        if (opcao == 0) return;
        if (opcao != 1 && opcao != 2) {
            System.out.println("Opcao invalida!");
            return;
        }
        executarBenchmarks(opcao == 1 ? 1 : 4);
    }

    private static int tarefasBenchmark(int configuracao) {
        return configuracao == 0 ? 0 : new int[] {5, 10, 100}[(configuracao - 1) % 3];
    }

    private static String nomeBenchmark(int configuracao) {
        if (configuracao == 0) return "V1 — Sequencial";
        return new String[] {"V2 — ExecutorService", "V3 — StructuredTaskScope",
                "V4 — StructuredTaskScope + fila"}[(configuracao - 1) / 3];
    }

    private static double mediaBenchmark(long[] tempos) {
        double soma = 0.0;
        for (long tempo : tempos) soma += tempo;
        return soma / tempos.length / 1_000_000.0;
    }

    private static void executarBenchmarks(int quantidadeTamanhos) {
        int[] tamanhos = {500, 1000, 1500, 2000};
        long[][][] tempos = new long[4][10][10];
        double[][][] valores = new double[4][10][10];
        boolean[][][] corretos = new boolean[4][10][10];
        int[][] concluidas = new int[4][10];
        String inicioBateria = OffsetDateTime.now().toString();
        String estado = "Em andamento";

        try {
            salvarBenchmarks(tempos, valores, corretos, concluidas, inicioBateria, estado);
            for (int tamanho = 0; tamanho < quantidadeTamanhos; tamanho++) {
                // A matriz e criada uma vez por tamanho, fora de todas as medicoes.
                double[][] matriz = gerarMatriz(tamanhos[tamanho], tamanhos[tamanho]);
                for (int configuracao = 0; configuracao < 10; configuracao++) {
                    int tarefas = tarefasBenchmark(configuracao);
                    for (int repeticao = 0; repeticao < 10; repeticao++) {
                        if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                        double resultado;
                        long inicio = System.nanoTime();
                        if (configuracao == 0) resultado = processar(matriz);
                        else if (configuracao <= 3) resultado = processarParalelo(matriz, tarefas);
                        else if (configuracao <= 6) resultado = processarEstruturado(matriz, tarefas);
                        else resultado = processarEstadoCompartilhado(matriz, tarefas);
                        long duracao = System.nanoTime() - inicio;

                        double referencia = configuracao == 0 && repeticao == 0
                                ? resultado : valores[tamanho][0][0];
                        boolean correto = Double.isFinite(resultado) && Double.isFinite(referencia)
                                && Math.abs(resultado - referencia) <= 1e-9 * Math.max(1.0, Math.abs(referencia));
                        tempos[tamanho][configuracao][repeticao] = duracao;
                        valores[tamanho][configuracao][repeticao] = resultado;
                        corretos[tamanho][configuracao][repeticao] = correto;
                        concluidas[tamanho][configuracao]++;
                        System.out.printf(Locale.ROOT, "%dx%d | %s | tarefas=%d | %d/10 | %.6f ms | correto=%s%n",
                                tamanhos[tamanho], tamanhos[tamanho], nomeBenchmark(configuracao),
                                tarefas, repeticao + 1, duracao / 1_000_000.0, correto);
                    }
                    double media = mediaBenchmark(tempos[tamanho][configuracao]);
                    System.out.printf(Locale.ROOT, "Media: %.6f ms | Speedup: %.6f%n", media,
                            mediaBenchmark(tempos[tamanho][0]) / media);
                    salvarBenchmarks(tempos, valores, corretos, concluidas, inicioBateria, estado);
                }
                for (int versao = 0; versao < 3; versao++) {
                    int melhor = 1 + versao * 3;
                    for (int c = melhor + 1; c <= 3 + versao * 3; c++) {
                        if (mediaBenchmark(tempos[tamanho][c]) < mediaBenchmark(tempos[tamanho][melhor])) melhor = c;
                    }
                    System.out.printf("Menor media em %dx%d: %s, %d tarefas (confira a validacao no relatorio).%n",
                            tamanhos[tamanho], tamanhos[tamanho], nomeBenchmark(melhor), tarefasBenchmark(melhor));
                }
            }
            estado = quantidadeTamanhos == 1 ? "Concluída somente a bateria 500x500; demais dimensões pendentes"
                    : "Bateria completa concluída";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            estado = "Interrompida; medições não concluídas permanecem pendentes";
        } catch (ExecutionException | StructuredTaskScope.FailedException | IOException e) {
            estado = "Falha: " + e;
        } finally {
            try {
                salvarBenchmarks(tempos, valores, corretos, concluidas, inicioBateria, estado);
                System.out.println(estado);
                System.out.println("Relatorio: resultados/resultados-experimentos.md");
            } catch (IOException e) {
                System.out.println("Nao foi possivel salvar o relatorio: " + e.getMessage());
            }
        }
    }

    private static void salvarBenchmarks(long[][][] tempos, double[][][] valores,
            boolean[][][] corretos, int[][] concluidas, String inicio, String estado) throws IOException {
        StringBuilder texto = new StringBuilder("# Resultados dos experimentos finais\n\n");
        texto.append("- Início da bateria: ").append(inicio).append("\n- Estado: ").append(estado.replace('\n', ' '))
                .append("\n- Java: ").append(System.getProperty("java.runtime.version"))
                .append("\n- JVM: ").append(System.getProperty("java.vm.name"))
                .append("\n- Sistema: ").append(System.getProperty("os.name")).append(' ')
                .append(System.getProperty("os.version")).append(" / ").append(System.getProperty("os.arch"))
                .append("\n- Processadores disponíveis para a JVM: ").append(Runtime.getRuntime().availableProcessors())
                .append("\n- Heap máximo da JVM (bytes; não representa a RAM física): ").append(Runtime.getRuntime().maxMemory())
                .append("\n- Modelo de CPU, RAM física, commit e outras cargas: não coletados automaticamente.\n\n")
                .append("Uma única JVM, ordem fixa V1, V2, V3, V4, sem aquecimento extra. Cada configuração completa tem exatamente dez medições. A primeira execução também entra na média; JIT e aquecimento podem influenciar os tempos. Uma matriz por dimensão é reutilizada apenas para leitura.\n\n")
                .append("Tempos em ms, medidos somente dentro do processamento. Geração da matriz, validação, impressão e gravação ficam fora do cronômetro. Não são reutilizados tempos de baterias anteriores.\n\n")
                .append("Tempo médio = soma dos 10 tempos / 10. Speedup = tempo médio sequencial / tempo médio paralelo da mesma dimensão.\n\n")
                .append("Validação de cada repetição: resultado finito e `|resultado - V1| <= 1e-9 * max(1, |V1|)`, usando o primeiro resultado da V1 da mesma matriz, sem arredondar. As demais repetições de V1 também são conferidas. Médias e speedup só aparecem após dez medições concluídas.\n\n");
        int[] tamanhos = {500, 1000, 1500, 2000};
        for (int t = 0; t < 4; t++) {
            texto.append("## Matriz ").append(tamanhos[t]).append('x').append(tamanhos[t]).append("\n\n")
                    .append("| Implementação | Quantidade de tarefas |");
            for (int r = 1; r <= 10; r++) texto.append(" T").append(r).append(" (ms) |");
            texto.append(" Tempo médio (ms) | Speedup | Resultado correto |\n|");
            for (int c = 0; c < 15; c++) texto.append(" --- |");
            texto.append('\n');
            for (int c = 0; c < 10; c++) {
                texto.append("| ").append(nomeBenchmark(c)).append(" | ")
                        .append(c == 0 ? "Não se aplica" : tarefasBenchmark(c)).append(" |");
                boolean correto = true;
                for (int r = 0; r < 10; r++) {
                    texto.append(r < concluidas[t][c] ? String.format(Locale.ROOT, " %.6f |", tempos[t][c][r] / 1_000_000.0) : " Pendente |");
                    if (r < concluidas[t][c]) correto &= corretos[t][c][r];
                }
                if (concluidas[t][c] == 10) {
                    texto.append(String.format(Locale.ROOT, " %.6f | ", mediaBenchmark(tempos[t][c])));
                    texto.append(c == 0 ? "Não se aplica" : String.format(Locale.ROOT, "%.6f", mediaBenchmark(tempos[t][0]) / mediaBenchmark(tempos[t][c])));
                    texto.append(correto ? " | Sim (10/10) |\n" : " | Não |\n");
                } else texto.append(" Pendente | ").append(c == 0 ? "Não se aplica" : "Pendente")
                        .append(correto ? " | Pendente |\n" : " | Não (bateria incompleta) |\n");
            }
            texto.append("\n### Menor tempo médio por versão\n\n");
            for (int v = 0; v < 3; v++) {
                int primeiro = 1 + v * 3;
                boolean completo = true;
                double menor = Double.POSITIVE_INFINITY;
                for (int c = primeiro; c < primeiro + 3; c++) {
                    completo &= concluidas[t][c] == 10;
                    menor = Math.min(menor, mediaBenchmark(tempos[t][c]));
                }
                texto.append("- V").append(v + 2).append(": ");
                if (!completo) texto.append("Pendente");
                else for (int c = primeiro; c < primeiro + 3; c++) {
                    if (Double.compare(mediaBenchmark(tempos[t][c]), menor) == 0) {
                        texto.append(tarefasBenchmark(c)).append(" tarefas ")
                                .append(String.format(Locale.ROOT, "(%.6f ms); ", menor));
                    }
                }
                texto.append('\n');
            }
            texto.append("\nMenor tempo não substitui a conferência de correção na tabela.\n\n")
                    .append("### Resultados numéricos por repetição\n\n");
            for (int c = 0; c < 10; c++) {
                if (concluidas[t][c] == 0) continue;
                texto.append("- ").append(nomeBenchmark(c)).append(" / tarefas=").append(tarefasBenchmark(c)).append(": ");
                for (int r = 0; r < concluidas[t][c]; r++) {
                    texto.append("R").append(r + 1).append('=').append(valores[t][c][r])
                            .append(corretos[t][c][r] ? " (correto)" : " (divergente)").append("; ");
                }
                texto.append('\n');
            }
            if (concluidas[t][0] == 0) texto.append("Pendente.\n");
            texto.append('\n');
        }
        Path destino = Path.of("resultados", "resultados-experimentos.md");
        Files.createDirectories(destino.getParent());
        Path temporario = Files.createTempFile(destino.getParent(), "benchmark-", ".tmp");
        try {
            Files.writeString(temporario, texto.toString().replaceAll("(?m)[ \\t]+$", "").stripTrailing() + "\n");
            try {
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
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
        System.out.println("6 - V3: StructuredTaskScope (5, 10 ou 100 tarefas)");
        System.out.println("7 - V4: Estado compartilhado (5, 10 ou 100 tarefas)");
        System.out.println("8 - Benchmarks finais");
        System.out.println("0 - Sair");
        System.out.println("==========================================");
        System.out.print("Escolha uma opcao: ");
    }

    private static void escolherProcessamentoParalelo(Scanner scanner) {
        escolherProcessamentoParalelo(scanner, false);
    }

    private static void escolherProcessamentoParalelo(Scanner scanner, boolean estruturado) {
        escolherProcessamentoParalelo(scanner, estruturado, false);
    }

    private static void escolherProcessamentoParalelo(Scanner scanner,
            boolean estruturado, boolean estadoCompartilhado) {
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
        executarProcessamento(tamanho, tamanho, quantidadeTarefas, estruturado, estadoCompartilhado);
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
                case 6:
                    escolherProcessamentoParalelo(scanner, true);
                    break;
                case 7:
                    escolherProcessamentoParalelo(scanner, true, true);
                    break;
                case 8:
                    escolherBenchmark(scanner);
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
