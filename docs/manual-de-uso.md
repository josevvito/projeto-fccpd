# Manual de uso

## Requisitos

É necessário ter o JDK 25 instalado e selecionado no terminal. Confira com `java -version` e `javac -version`: os dois devem indicar a versão 25. O projeto não usa Maven, Gradle ou frameworks.

`StructuredTaskScope` é uma API preview no Java 25. Por isso, `--enable-preview` deve ser usado tanto na compilação quanto na execução, mesmo quando a opção escolhida no menu for a V1 ou a V2.

## Compilar e executar

Abra o terminal na pasta raiz do repositório e compile:

```sh
javac --enable-preview --release 25 -d out src/Main.java
```

O compilador grava os arquivos compilados em `out/`. Em seguida, execute:

```sh
java --enable-preview -cp out Main
```

## Opções do menu

| Opção | Execução |
| --- | --- |
| 1 | V1 sequencial, matriz 500x500 |
| 2 | V1 sequencial, matriz 1000x1000 |
| 3 | V1 sequencial, matriz 1500x1500 |
| 4 | V1 sequencial, matriz 2000x2000 |
| 5 | V2 com ExecutorService |
| 6 | V3 com StructuredTaskScope |
| 7 | V4 com StructuredTaskScope e fila compartilhada |
| 8 | Benchmarks automáticos, com submenu |
| 0 | Sair |

Na V1, basta digitar uma opção de `1` a `4` e pressionar Enter. Não há escolha de quantidade de tarefas.

Para V2, V3 ou V4, escolha `5`, `6` ou `7`. Depois informe o tamanho como um único número: `500`, `1000`, `1500` ou `2000`. Por fim, informe a quantidade de tarefas: `5`, `10` ou `100`. Pressione Enter após cada valor. Não digite `500x500` no campo de tamanho.

Exemplo: para executar V4 com matriz 500x500 e 10 tarefas, informe `7`, depois `500` e depois `10`. A quantidade de tarefas representa blocos de trabalho; não significa que todas executarão ao mesmo tempo.

## Saída e novas execuções

O programa informa a dimensão, o número de elementos, o resultado da soma e o tempo em milissegundos e segundos. Nas versões concorrentes, também informa a quantidade de tarefas. A geração da matriz fica fora da medição.

Após cada execução, o menu aparece novamente. As opções `1` a `7` continuam sendo execuções individuais, sem gravar relatório nem validar automaticamente contra a V1. Para registro e comparação automáticos, use a opção `8`.

Entradas inválidas exibem uma mensagem e retornam ao menu. Falhas nas tarefas são informadas sem apresentar um resultado parcial como sucesso. Se a tarefa principal for interrompida durante o processamento, o programa preserva a indicação de interrupção e encerra o laço do menu.

## Benchmarks automáticos

Escolha `8` e, no submenu, `1` para medir somente 500x500, `2` para medir as quatro dimensões ou `0` para voltar. A bateria completa pode demorar bastante. Cada nova bateria substitui `resultados/resultados-experimentos.md` e deixa pendentes as configurações não medidas nessa bateria; copie o relatório anterior antes de iniciar se quiser preservá-lo.

Para cada dimensão, a V1 executa dez vezes. Em seguida, V2, V3 e V4 executam dez vezes para cada quantidade de tarefas: 5, 10 e 100. São 100 medições por dimensão e 400 na bateria completa. Não há aquecimento extra: todas as dez repetições entram na média, na mesma JVM e em ordem fixa. Isso permite reproduzir o procedimento, mas o aquecimento da JVM e a carga da máquina podem influenciar a comparação.

A matriz é gerada uma vez por dimensão, antes das medições, e reutilizada apenas para leitura. O cronômetro envolve somente a chamada ao processamento; impressão, validação e gravação ficam fora dele. A média é a soma dos dez tempos dividida por dez. O speedup usa a média da V1 da mesma dimensão dividida pela média paralela.

Cada resultado é conferido com o primeiro resultado da V1 da mesma dimensão, usando valores completos de `double`: ambos devem ser finitos e a diferença absoluta deve ser no máximo `1e-9 * max(1, |V1|)`. Todas as dez repetições precisam passar para a configuração ser marcada como correta. Resultados divergentes são registrados como tal, sem ocultar a medição.

O terminal mostra o progresso, médias, speedup e a quantidade de tarefas com menor média por versão. O relatório registra os dez tempos, médias, speedup, correção, resultados numéricos por repetição e as menores médias, incluindo empates. Um tempo menor não torna correto um resultado divergente.

O arquivo é salvo ao iniciar, após cada configuração completa e ao terminar. Uma falha ou interrupção tratada tenta salvar as medições já concluídas, mantendo as demais pendentes; uma configuração incompleta não recebe média ou speedup. Um encerramento forçado do processo pode perder as medições desde a última gravação. A gravação usa arquivo temporário e troca atômica quando o sistema de arquivos permite.
