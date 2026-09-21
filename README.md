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
