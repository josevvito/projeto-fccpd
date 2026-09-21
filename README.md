# Projeto FPCPD

**Disciplina:** Programação Paralela, Concorrente e Distribuída.

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

Com um JDK instalado, execute na raiz do projeto:

```sh
javac -d out src/Main.java
java -cp out Main
```

O menu oferece matrizes de 500x500, 1000x1000, 1500x1500 e 2000x2000. Ao final de cada processamento, exibe o resultado e o tempo em milissegundos e segundos. Use a opção `0` para sair.

## V2: ExecutorService

As opções `1` a `4` preservam a V1 sequencial. A opção `5` executa a V2: informe o tamanho da matriz e escolha 5, 10 ou 100 tarefas.

Cada tarefa recebe um bloco contíguo de linhas, calcula uma soma local e retorna um `Future<Double>`. Os resultados parciais são somados na ordem de envio. Os limites dos blocos cobrem todas as linhas, inclusive quando a divisão não é exata. O pool usa no máximo o menor valor entre o número de tarefas e os processadores disponíveis e chama `shutdown()` em `finally`.

A matriz e o cálculo são os mesmos da V1. A mudança na ordem das somas pode causar pequenas diferenças de ponto flutuante; nos testes de validação, foi usada tolerância relativa de `1e-9` (com tolerância absoluta mínima de `1e-9`), que não é aplicada automaticamente pela aplicação. Nesta etapa, a validação foi executada até a matriz `500x500`. Os experimentos completos com `1000x1000`, `1500x1500` e `2000x2000` serão executados posteriormente. A medição exclui a criação da matriz e inclui a criação do pool, o envio das tarefas, a obtenção e soma dos resultados e a chamada de encerramento.
