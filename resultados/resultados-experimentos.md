# Resultados dos experimentos finais

Este arquivo é somente a estrutura de registro. Nenhum tempo, speedup ou resultado de correção dos experimentos finais foi preenchido. Os testes de validação anteriores não substituem estas dez medições por configuração.

## Condições de execução

- Data: pendente.
- Commit utilizado: pendente.
- Sistema operacional: pendente.
- Processador e quantidade de processadores disponíveis para a JVM: pendente.
- Memória RAM: pendente.
- Versão completa do JDK 25: pendente.
- Aquecimento da JVM e uso de processos novos ou do mesmo processo: pendente.
- Outras cargas na máquina durante as medições: pendente.

## Como preencher

Execute dez medições para cada linha das tabelas, mantendo as condições comparáveis. Registre todos os tempos em **milissegundos (ms)**. Use o tempo de processamento informado pelo programa; ele exclui a criação da matriz.

Calcule a média somente depois de registrar os dez tempos válidos:

`Tempo médio = (T1 + T2 + T3 + T4 + T5 + T6 + T7 + T8 + T9 + T10) / 10`

Use a fórmula solicitada:

`Speedup = tempo sequencial / tempo paralelo`

Para estas tabelas, use a média da V1 dividida pela média da versão paralela, sempre para a mesma dimensão e condições de execução. O speedup não tem unidade. Para V1, a coluna de speedup não se aplica; não há quantidade de tarefas configurável.

Em “Resultado correto”, registre a conclusão somente após comparar com a V1 da mesma dimensão. Registre também o resultado numérico e a diferença observada nas notas. O critério usado nos testes anteriores foi `|resultado - referência| <= 1e-9 * max(1, |referência|)`; essa verificação é externa à aplicação. A saída do menu arredonda o resultado para seis casas decimais, portanto não permite avaliar todos os dígitos internos do `double`.

Falhas e interrupções devem ser anotadas, sem tratar uma execução incompleta como tempo válido ou resultado correto. Nenhum campo pendente deve ser interpretado como zero.

## Matriz 500x500

| Implementação | Quantidade de tarefas | T1 (ms) | T2 (ms) | T3 (ms) | T4 (ms) | T5 (ms) | T6 (ms) | T7 (ms) | T8 (ms) | T9 (ms) | T10 (ms) | Tempo médio (ms) | Speedup | Resultado correto |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V1 — Sequencial | Não se aplica | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Não se aplica | Pendente |
| V2 — ExecutorService | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V2 — ExecutorService | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V2 — ExecutorService | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |

Notas sobre resultados numéricos, diferenças, falhas e condições desta dimensão: pendente.

## Matriz 1000x1000

| Implementação | Quantidade de tarefas | T1 (ms) | T2 (ms) | T3 (ms) | T4 (ms) | T5 (ms) | T6 (ms) | T7 (ms) | T8 (ms) | T9 (ms) | T10 (ms) | Tempo médio (ms) | Speedup | Resultado correto |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V1 — Sequencial | Não se aplica | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Não se aplica | Pendente |
| V2 — ExecutorService | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V2 — ExecutorService | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V2 — ExecutorService | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |

Notas sobre resultados numéricos, diferenças, falhas e condições desta dimensão: pendente.

## Matriz 1500x1500

| Implementação | Quantidade de tarefas | T1 (ms) | T2 (ms) | T3 (ms) | T4 (ms) | T5 (ms) | T6 (ms) | T7 (ms) | T8 (ms) | T9 (ms) | T10 (ms) | Tempo médio (ms) | Speedup | Resultado correto |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V1 — Sequencial | Não se aplica | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Não se aplica | Pendente |
| V2 — ExecutorService | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V2 — ExecutorService | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V2 — ExecutorService | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |

Notas sobre resultados numéricos, diferenças, falhas e condições desta dimensão: pendente.

## Matriz 2000x2000

| Implementação | Quantidade de tarefas | T1 (ms) | T2 (ms) | T3 (ms) | T4 (ms) | T5 (ms) | T6 (ms) | T7 (ms) | T8 (ms) | T9 (ms) | T10 (ms) | Tempo médio (ms) | Speedup | Resultado correto |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V1 — Sequencial | Não se aplica | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Não se aplica | Pendente |
| V2 — ExecutorService | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V2 — ExecutorService | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V2 — ExecutorService | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V3 — StructuredTaskScope | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 5 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 10 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |
| V4 — StructuredTaskScope + fila | 100 | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente | Pendente |

Notas sobre resultados numéricos, diferenças, falhas e condições desta dimensão: pendente.
