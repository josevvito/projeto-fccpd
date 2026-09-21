# Projeto FCCPD

**Disciplina:** Fundamentos de computação Paralela, Concorrente e Distribuída.

## Objetivo

Estudar e aplicar conceitos de programação paralela, concorrente e distribuída em um projeto Java simples, documentando os experimentos e seus resultados.

## Integrantes

- Flávio Barbosa
- José Vitor
- Nestor Melquezedeque

## Estrutura inicial

- `src/`: código-fonte Java a ser desenvolvido.
- `docs/`: documentação do trabalho.
- `resultados/`: resultados dos experimentos.

O projeto não utiliza Maven, Gradle ou frameworks. A versão inicial em `src/Main.java` realiza o processamento sequencial de matrizes determinísticas, medindo apenas o tempo de processamento.

## Como executar

Com o JDK 25 instalado e selecionado no terminal, execute na raiz do projeto (a V3 usa uma API preview):

```sh
javac --enable-preview --release 25 -d out src/Main.java
java --enable-preview -cp out Main
```

O menu oferece matrizes de 500x500, 1000x1000, 1500x1500 e 2000x2000. Ao final de cada processamento, exibe o resultado e o tempo em milissegundos e segundos. Use a opção `0` para sair.

## V2: ExecutorService

As opções `1` a `4` preservam a V1 sequencial. A opção `5` executa a V2: informe o tamanho da matriz e escolha 5, 10 ou 100 tarefas.

Cada tarefa recebe um bloco contíguo de linhas, calcula uma soma local e retorna um `Future<Double>`. Os resultados parciais são somados na ordem de envio. Os limites dos blocos cobrem todas as linhas, inclusive quando a divisão não é exata. O pool usa no máximo o menor valor entre o número de tarefas e os processadores disponíveis e chama `shutdown()` em `finally`.

A matriz e o cálculo são os mesmos da V1. A mudança na ordem das somas pode causar pequenas diferenças de ponto flutuante; nos testes de validação, foi usada tolerância relativa de `1e-9` (com tolerância absoluta mínima de `1e-9`), que não é aplicada automaticamente pela aplicação. Nesta etapa, a validação foi executada até a matriz `500x500`. Os experimentos completos com `1000x1000`, `1500x1500` e `2000x2000` serão executados posteriormente. A medição exclui a criação da matriz e inclui a criação do pool, o envio das tarefas, a obtenção e soma dos resultados e a chamada de encerramento.

## V3: StructuredTaskScope

A opção `6` executa a versão estruturada com 5, 10 ou 100 subtarefas. As opções da V1 e da V2 continuam disponíveis. O cálculo e a geração da matriz permanecem iguais.

Cada subtarefa processa um bloco contíguo de linhas, com os mesmos limites da V2, e mantém sua própria soma local. A matriz é apenas lida. `fork()` cria as subtarefas dentro de `StructuredTaskScope.<Double>open()`, que usa threads virtuais por padrão no Java 25. Após `join()` terminar com sucesso, a tarefa principal soma os resultados de `Subtask.get()` na ordem de criação.

O `try-with-resources` mantém o ciclo de vida das subtarefas vinculado ao escopo: seu fechamento aguarda o término de todas elas. Em caso de falha, o escopo cancela as demais subtarefas e propaga `FailedException`; as tarefas verificam interrupção a cada linha. Diferentemente da V2, não há gerenciamento manual de pool ou `shutdown()` na V3. Não há acumulador compartilhado entre subtarefas.

A medição continua excluindo a geração da matriz e inclui abertura do escopo, criação e execução das subtarefas, espera, combinação e fechamento do escopo.

Nos testes de validação da V3, foram comparadas V1, V2 e V3 nas matrizes `0x0`, `3x7`, `103x11` e `500x500`, com 5, 10 e 100 tarefas. Todos os resultados ficaram dentro da tolerância relativa de `1e-9` (mínimo absoluto de `1e-9`), usada apenas nos testes. V2 e V3 produziram resultados idênticos para a mesma divisão; a maior diferença absoluta entre V3 e V1 em `500x500` foi aproximadamente `3,04e-7`. Também foram testadas entradas inválidas, falha de subtarefa e interrupção. Os experimentos completos com dimensões maiores continuam pendentes.

Referências: [exemplo do professor](https://github.com/rnl-school/Paralelismo/tree/Aula04_Concorrencia_Estruturada) e [API StructuredTaskScope do Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/StructuredTaskScope.html).

## V4: Estado compartilhado com ConcurrentLinkedQueue

A opção `7` executa a V4 com 5, 10 ou 100 subtarefas, preservando as opções das versões anteriores. A divisão em blocos contíguos de linhas, o cálculo e a matriz determinística são os mesmos da V3.

O método `processarEstadoCompartilhado` cria uma `ConcurrentLinkedQueue<Double>` por execução, compartilhada pelas subtarefas do `StructuredTaskScope`. Cada subtarefa criada com `fork()` calcula sua soma local e adiciona exatamente um resultado parcial à fila. Após `join()` terminar com sucesso, apenas a tarefa principal percorre a fila e soma seus valores.

A fila permite inserções concorrentes seguras, sem `ArrayList` compartilhado ou `synchronized`. A matriz é somente lida, as somas locais pertencem a cada subtarefa e a soma final acontece após todas terminarem. Assim, não há disputa por um acumulador nem perda de inserções por acesso concorrente. A fila não é reutilizada entre execuções.

Na V3, os resultados são lidos de `Subtask.get()` na ordem de criação; na V4, a ordem da fila depende das inserções concorrentes. Essa diferença pode produzir pequenas variações de ponto flutuante, sem representar uma race condition no armazenamento. Em caso de falha ou interrupção, a V4 segue o tratamento da V3: o escopo cancela as subtarefas, aguarda seu término no fechamento e não retorna a soma de uma fila incompleta.

A criação da matriz permanece fora da medição. O tempo da V4 inclui criação da fila e do escopo, execução das subtarefas, inserções, espera, soma final e fechamento. A V4 também requer Java 25 com preview:

```sh
javac --enable-preview --release 25 -d out src/Main.java
java --enable-preview -cp out Main
```

Nos testes, a V4 foi comparada com V1, V2 e V3 usando 5, 10 e 100 tarefas nas matrizes `0x0`, `3x7`, `103x11` e `500x500`. Foram feitas dez repetições por configuração pequena e três por configuração de `500x500`. Todos os resultados ficaram dentro da tolerância relativa de `1e-9`, com mínimo absoluto de `1e-9`, aplicada apenas nos testes. Em `500x500`, a maior diferença absoluta foi aproximadamente `3,04e-7` frente à V1 e `2,33e-10` frente à V2 e à V3. A matriz permaneceu intacta. Também passaram os testes de menu, entradas inválidas, falha de subtarefa e interrupção. Matrizes maiores e experimentos completos de desempenho ainda não foram executados nesta etapa.

Referências: [aula de estado compartilhado e variáveis atômicas](https://github.com/rnl-school/Paralelismo/tree/Aula05_Estado_Compartilhado_Atomico) e [ConcurrentLinkedQueue no Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ConcurrentLinkedQueue.html).
