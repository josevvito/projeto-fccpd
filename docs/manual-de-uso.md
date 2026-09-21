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
| 0 | Sair |

Na V1, basta digitar uma opção de `1` a `4` e pressionar Enter. Não há escolha de quantidade de tarefas.

Para V2, V3 ou V4, escolha `5`, `6` ou `7`. Depois informe o tamanho como um único número: `500`, `1000`, `1500` ou `2000`. Por fim, informe a quantidade de tarefas: `5`, `10` ou `100`. Pressione Enter após cada valor. Não digite `500x500` no campo de tamanho.

Exemplo: para executar V4 com matriz 500x500 e 10 tarefas, informe `7`, depois `500` e depois `10`. A quantidade de tarefas representa blocos de trabalho; não significa que todas executarão ao mesmo tempo.

## Saída e novas execuções

O programa informa a dimensão, o número de elementos, o resultado da soma e o tempo em milissegundos e segundos. Nas versões concorrentes, também informa a quantidade de tarefas. A geração da matriz fica fora da medição.

Após cada execução, o menu aparece novamente. Repita a escolha para fazer novas medições. A aplicação não salva os tempos em arquivo, não calcula médias ou speedup e não verifica automaticamente se o resultado coincide com a V1. Registre e confira esses dados na etapa de experimentos.

Entradas inválidas exibem uma mensagem e retornam ao menu. Falhas nas tarefas são informadas sem apresentar um resultado parcial como sucesso. Se a tarefa principal for interrompida durante o processamento, o programa preserva a indicação de interrupção e encerra o laço do menu.
