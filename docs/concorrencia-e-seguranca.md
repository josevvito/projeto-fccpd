# Concorrência e segurança

Esta análise considera os métodos atuais de `src/Main.java`. Ela descreve a organização do código, sem garantir tempo máximo de execução ou justiça do escalonador. Os laços de cálculo e os blocos de linhas são finitos para as matrizes do menu. A matriz é criada antes das tarefas e apenas lida durante o processamento.

## Deadlock

Não foi identificado um ciclo de espera entre tarefas. Na V2, a tarefa principal espera pelos `Future`, mas as tarefas do pool não esperam por ela nem por outras tarefas. Na V3 e na V4, a tarefa principal espera pelo escopo e as subtarefas apenas calculam seus blocos. Não há aquisição encadeada de locks nem `synchronized` no código da aplicação.

A espera em `get()`, `join()` ou no fechamento do escopo não é, por si só, deadlock. Nessas chamadas, o trabalho aguardado pode continuar executando. Essa conclusão vale para o código atual; não é uma garantia sobre qualquer alteração futura ou problema externo da JVM e do sistema operacional.

## Livelock

Não foi identificado um mecanismo em que tarefas fiquem reagindo umas às outras e repetindo ações sem concluir o trabalho. Não há laços de tentativa de bloqueio ou de negociação entre tarefas no código. Cada subtarefa percorre um bloco finito e produz um parcial. A fila concorrente pode resolver disputas internas entre inserções, mas a aplicação não implementa um ciclo de tentativas entre produtores.

## Starvation e espera normal

Não foi identificado no código um mecanismo que exclua continuamente uma tarefa do processamento. A V2 envia uma quantidade finita de tarefas para um pool, e cada tarefa termina seu bloco sem aguardar outra tarefa do mesmo pool. V3 e V4 também criam uma quantidade finita de subtarefas, sem prioridades diferentes definidas pela aplicação.

Esperar porque todas as threads do pool estão ocupadas é espera normal: quando uma tarefa termina, uma thread pode executar outra. Starvation é a falta prolongada ou indefinida de oportunidade de avançar enquanto outras tarefas continuam sendo atendidas. Uma execução demorada ou uma fila de espera, isoladamente, não demonstram starvation.

O código não impõe uma política de justiça e não configura timeout. Assim, não permite garantir que toda tarefa receberá CPU em um prazo definido. A carga da máquina e o escalonamento da JVM e do sistema operacional influenciam a execução.

## V2: gerenciamento manual

`processarParalelo()` cria um `ExecutorService` com pool fixo, limitado pelo menor valor entre tarefas e processadores disponíveis. Envia cada bloco com `submit()` e guarda os `Future` em uma lista manipulada somente pela tarefa principal. As tarefas usam somas locais, sem escrever na lista ou em um acumulador compartilhado.

A tarefa principal soma os valores de `Future.get()` na ordem de envio. Isso pode fazê-la esperar por uma tarefa anterior enquanto outra já terminou, mas não impede as tarefas do pool de continuar. Uma falha em uma tarefa posterior também pode ser percebida só quando seu `Future` for consultado.

O `finally` sempre chama `shutdown()`. Se a coleta não terminou com sucesso, solicita `cancel(true)` em cada `Future`. `shutdown()` não equivale a aguardar o término de todas as threads; a V2 não usa `awaitTermination()`. O cancelamento depende da cooperação das tarefas, que verificam interrupção a cada linha. Dentro de `calcular()`, não há essa verificação, mas o laço tem 1000 iterações.

## V3: vínculo com o escopo

`processarEstruturado()` usa `StructuredTaskScope` em `try-with-resources`. A tarefa principal cria as subtarefas com `fork()` e chama `join()` antes de consultar os resultados. A lista de `Subtask` é acessada somente pela tarefa principal; não é um estado escrito pelas subtarefas.

O escopo padrão espera pelo sucesso de todas ou reage à falha de uma delas, cancelando as demais e propagando `FailedException`. O fechamento aguarda o término das subtarefas, inclusive quando há cancelamento. Por isso, elas não continuam além do fechamento do escopo. A resposta ao cancelamento ainda depende das verificações de interrupção feitas a cada linha.

## V4: escrita na fila compartilhada

`processarEstadoCompartilhado()` cria uma nova `ConcurrentLinkedQueue<Double>` para cada execução. Cada subtarefa mantém seu `parcial` local e faz uma inserção ao terminar seu bloco. A fila suporta essas inserções concorrentes sem necessidade de `synchronized` na aplicação.

A tarefa principal percorre a fila somente depois de `join()` bem-sucedido. Nesse ponto, todas as inserções terminaram. Não há disputa sobre a soma final, porque apenas a tarefa principal a atualiza. Se uma tarefa falhar ou houver interrupção, o fluxo não retorna uma soma feita com uma fila incompleta. O tratamento de escopo e de exceções acompanha a V3.

Nas versões V2, V3 e V4, `executarProcessamento()` informa falhas e restaura a indicação de interrupção ao capturar `InterruptedException`.

## Ponto flutuante e race condition

Somar números `double` em ordens diferentes pode mudar os últimos dígitos devido ao arredondamento. V1 soma elemento por elemento; V2 e V3 somam parciais por bloco. Na V4, a ordem dos parciais depende das inserções na fila e pode variar entre execuções.

Isso é diferente de uma race condition, como duas tarefas atualizarem um mesmo `double` com `+=` e perderem uma atualização. Esse padrão não aparece no código atual: os acumuladores das subtarefas são locais, a fila protege suas inserções e a soma final tem um único responsável.

Não foi identificado risco de perda de parciais por escrita concorrente nessa organização. Uma pequena diferença numérica, porém, não prova sozinha que tudo está correto: a validação deve comparar com V1 usando uma tolerância definida e conferir se todos os blocos foram processados. A aplicação não faz essa validação automaticamente.
