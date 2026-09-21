# Resultados dos experimentos finais

- Início da bateria: 2026-09-21T13:45:38.173363800-03:00
- Estado: Concluída somente a bateria 500x500; demais dimensões pendentes
- Java: 25.0.4+1-b508.27
- JVM: OpenJDK 64-Bit Server VM
- Sistema: Windows 11 10.0 / amd64
- Processadores disponíveis para a JVM: 8
- Heap máximo da JVM (bytes; não representa a RAM física): 4232052736
- Modelo de CPU, RAM física, commit e outras cargas: não coletados automaticamente.

Uma única JVM, ordem fixa V1, V2, V3, V4, sem aquecimento extra. Cada configuração completa tem exatamente dez medições. A primeira execução também entra na média; JIT e aquecimento podem influenciar os tempos. Uma matriz por dimensão é reutilizada apenas para leitura.

Tempos em ms, medidos somente dentro do processamento. Geração da matriz, validação, impressão e gravação ficam fora do cronômetro. Não são reutilizados tempos de baterias anteriores.

Tempo médio = soma dos 10 tempos / 10. Speedup = tempo médio sequencial / tempo médio paralelo da mesma dimensão.

Validação de cada repetição: resultado finito e `|resultado - V1| <= 1e-9 * max(1, |V1|)`, usando o primeiro resultado da V1 da mesma matriz, sem arredondar. As demais repetições de V1 também são conferidas. Médias e speedup só aparecem após dez medições concluídas.

## Matriz 500x500

| Implementação | Quantidade de tarefas | T1 (ms) | T2 (ms) | T3 (ms) | T4 (ms) | T5 (ms) | T6 (ms) | T7 (ms) | T8 (ms) | T9 (ms) | T10 (ms) | Tempo médio (ms) | Speedup | Resultado correto |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V1 — Sequencial | Não se aplica | 8847.597900 | 8079.093500 | 7033.784100 | 7495.027900 | 6837.152800 | 7070.145000 | 9273.370000 | 7822.271300 | 8093.530300 | 6986.106200 | 7753.807900 | Não se aplica | Sim (10/10) |
| V2 — ExecutorService | 5 | 3095.958800 | 3263.823600 | 3021.330900 | 3274.487600 | 3585.042900 | 4285.989000 | 5021.229500 | 4234.808400 | 3693.204600 | 3563.820400 | 3703.969570 | 2.093378 | Sim (10/10) |
| V2 — ExecutorService | 10 | 3882.105500 | 3641.762800 | 4406.770500 | 3148.185900 | 3227.244900 | 3119.509700 | 3273.269700 | 3323.346200 | 3041.705700 | 2984.524700 | 3404.842560 | 2.277288 | Sim (10/10) |
| V2 — ExecutorService | 100 | 2904.394400 | 3279.612200 | 2754.343400 | 3020.335600 | 2545.627200 | 2514.417000 | 2712.295000 | 2508.296200 | 2558.445100 | 2902.709800 | 2770.047590 | 2.799161 | Sim (10/10) |
| V3 — StructuredTaskScope | 5 | 3974.649500 | 3279.169200 | 3301.592700 | 2985.568600 | 3004.916400 | 2946.870100 | 2854.062700 | 2874.694200 | 2790.669900 | 2777.396600 | 3078.958990 | 2.518321 | Sim (10/10) |
| V3 — StructuredTaskScope | 10 | 3132.877000 | 2910.862700 | 2880.439300 | 3567.271400 | 3007.295700 | 3022.361900 | 2856.358000 | 2954.079100 | 2900.548400 | 2863.276900 | 3009.537040 | 2.576412 | Sim (10/10) |
| V3 — StructuredTaskScope | 100 | 2447.875500 | 2528.919300 | 2501.485500 | 2648.817800 | 2457.267200 | 2736.353200 | 2493.734800 | 2393.320000 | 2534.398200 | 2483.888900 | 2522.606040 | 3.073729 | Sim (10/10) |
| V4 — StructuredTaskScope + fila | 5 | 3675.484200 | 3419.664600 | 2969.228300 | 2809.578600 | 3006.631900 | 3229.322200 | 2857.492600 | 2800.153400 | 2848.381300 | 2906.013800 | 3052.195090 | 2.540404 | Sim (10/10) |
| V4 — StructuredTaskScope + fila | 10 | 3137.234900 | 3158.203700 | 2859.452600 | 2791.418000 | 2793.147900 | 2909.391100 | 3031.117700 | 2788.315700 | 2941.072300 | 2881.778800 | 2929.113270 | 2.647152 | Sim (10/10) |
| V4 — StructuredTaskScope + fila | 100 | 2589.621600 | 2524.196300 | 2585.847700 | 2733.428400 | 2586.646200 | 2405.165800 | 2570.979200 | 2857.462600 | 2481.960800 | 2579.627600 | 2591.493620 | 2.992023 | Sim (10/10) |

### Menor tempo médio por versão

- V2: 100 tarefas (2770.047590 ms);
- V3: 100 tarefas (2522.606040 ms);
- V4: 100 tarefas (2591.493620 ms);

Menor tempo não substitui a conferência de correção na tabela.

### Resultados numéricos por repetição

- V1 — Sequencial / tarefas=0: R1=1264682.8309984687 (correto); R2=1264682.8309984687 (correto); R3=1264682.8309984687 (correto); R4=1264682.8309984687 (correto); R5=1264682.8309984687 (correto); R6=1264682.8309984687 (correto); R7=1264682.8309984687 (correto); R8=1264682.8309984687 (correto); R9=1264682.8309984687 (correto); R10=1264682.8309984687 (correto);
- V2 — ExecutorService / tarefas=5: R1=1264682.8309987728 (correto); R2=1264682.8309987728 (correto); R3=1264682.8309987728 (correto); R4=1264682.8309987728 (correto); R5=1264682.8309987728 (correto); R6=1264682.8309987728 (correto); R7=1264682.8309987728 (correto); R8=1264682.8309987728 (correto); R9=1264682.8309987728 (correto); R10=1264682.8309987728 (correto);
- V2 — ExecutorService / tarefas=10: R1=1264682.830998622 (correto); R2=1264682.830998622 (correto); R3=1264682.830998622 (correto); R4=1264682.830998622 (correto); R5=1264682.830998622 (correto); R6=1264682.830998622 (correto); R7=1264682.830998622 (correto); R8=1264682.830998622 (correto); R9=1264682.830998622 (correto); R10=1264682.830998622 (correto);
- V2 — ExecutorService / tarefas=100: R1=1264682.8309986508 (correto); R2=1264682.8309986508 (correto); R3=1264682.8309986508 (correto); R4=1264682.8309986508 (correto); R5=1264682.8309986508 (correto); R6=1264682.8309986508 (correto); R7=1264682.8309986508 (correto); R8=1264682.8309986508 (correto); R9=1264682.8309986508 (correto); R10=1264682.8309986508 (correto);
- V3 — StructuredTaskScope / tarefas=5: R1=1264682.8309987728 (correto); R2=1264682.8309987728 (correto); R3=1264682.8309987728 (correto); R4=1264682.8309987728 (correto); R5=1264682.8309987728 (correto); R6=1264682.8309987728 (correto); R7=1264682.8309987728 (correto); R8=1264682.8309987728 (correto); R9=1264682.8309987728 (correto); R10=1264682.8309987728 (correto);
- V3 — StructuredTaskScope / tarefas=10: R1=1264682.830998622 (correto); R2=1264682.830998622 (correto); R3=1264682.830998622 (correto); R4=1264682.830998622 (correto); R5=1264682.830998622 (correto); R6=1264682.830998622 (correto); R7=1264682.830998622 (correto); R8=1264682.830998622 (correto); R9=1264682.830998622 (correto); R10=1264682.830998622 (correto);
- V3 — StructuredTaskScope / tarefas=100: R1=1264682.8309986508 (correto); R2=1264682.8309986508 (correto); R3=1264682.8309986508 (correto); R4=1264682.8309986508 (correto); R5=1264682.8309986508 (correto); R6=1264682.8309986508 (correto); R7=1264682.8309986508 (correto); R8=1264682.8309986508 (correto); R9=1264682.8309986508 (correto); R10=1264682.8309986508 (correto);
- V4 — StructuredTaskScope + fila / tarefas=5: R1=1264682.8309987728 (correto); R2=1264682.8309987728 (correto); R3=1264682.8309987728 (correto); R4=1264682.8309987728 (correto); R5=1264682.8309987728 (correto); R6=1264682.8309987728 (correto); R7=1264682.8309987728 (correto); R8=1264682.8309987728 (correto); R9=1264682.8309987728 (correto); R10=1264682.8309987728 (correto);
- V4 — StructuredTaskScope + fila / tarefas=10: R1=1264682.830998622 (correto); R2=1264682.830998622 (correto); R3=1264682.830998622 (correto); R4=1264682.830998622 (correto); R5=1264682.830998622 (correto); R6=1264682.830998622 (correto); R7=1264682.830998622 (correto); R8=1264682.830998622 (correto); R9=1264682.830998622 (correto); R10=1264682.830998622 (correto);
- V4 — StructuredTaskScope + fila / tarefas=100: R1=1264682.8309986508 (correto); R2=1264682.8309986508 (correto); R3=1264682.8309986508 (correto); R4=1264682.8309986508 (correto); R5=1264682.8309986508 (correto); R6=1264682.8309986508 (correto); R7=1264682.8309986508 (correto); R8=1264682.8309986508 (correto); R9=1264682.8309986508 (correto); R10=1264682.8309986508 (correto);

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

### Menor tempo médio por versão

- V2: Pendente
- V3: Pendente
- V4: Pendente

Menor tempo não substitui a conferência de correção na tabela.

### Resultados numéricos por repetição

Pendente.

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

### Menor tempo médio por versão

- V2: Pendente
- V3: Pendente
- V4: Pendente

Menor tempo não substitui a conferência de correção na tabela.

### Resultados numéricos por repetição

Pendente.

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

### Menor tempo médio por versão

- V2: Pendente
- V3: Pendente
- V4: Pendente

Menor tempo não substitui a conferência de correção na tabela.

### Resultados numéricos por repetição

Pendente.
