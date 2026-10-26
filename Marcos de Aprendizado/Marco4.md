# Marco 4 — Implementação final e conclusão

## 1. Solução final

- **Linguagem:** Java.
- **Arquivo:** [`src/Main.java`](../src/Main.java), em um único arquivo, com `Bag`, `Stack` e `Graph` como classes internas.
- **Problema:** Tree Isomorphism I (CSES), isomorfismo de árvores enraizadas no vértice 1.
- **Estratégia:** forma canônica de cada subárvore, como descrito no [Marco 3](marco-3.md).

### Como executar

```bash
# Java 11 ou superior: compila e executa em um passo
java src/Main.java < dados/casos-de-teste.txt

# ou, de forma tradicional
cd src
javac Main.java
java Main < ../dados/casos-de-teste.txt
```

Saída esperada para `dados/casos-de-teste.txt`:

```
NO
YES
YES
YES
YES
NO
YES
NO
```

### Visão geral do código

| Trecho | Função |
|---|---|
| `readInt()` / `readTree(n)` | leitura rápida da entrada e construção de uma árvore como `Graph`, com vértices decrementados de 1 (raiz = 0) |
| `canon(g, mapa)` | DFS iterativa com `Stack`, guardando `pai[]` e a pré-ordem. Depois percorre a pré-ordem ao contrário e calcula o `id` de cada nó |
| `main` | para cada teste, lê as duas árvores, cria **um** `HashMap` compartilhado, chama `canon` nas duas e compara os ids das raízes |

---

## 2. Classes reutilizadas e modificadas

### Implementações de referência (`algs4`)

| Componente | Situação | O que foi feito e por quê |
|---|---|---|
| `Bag` | **reutilizada** | Mantida com a lógica original (`add`, `size`, `isEmpty` e iterador). Serve como lista de adjacência de cada vértice. A iteração em ordem inversa à inserção não afeta o resultado, porque as listas de ids são ordenadas |
| `Stack` | **reutilizada, simplificada** | Mantidos `push`, `pop`, `isEmpty` e `size`. Foram removidos `peek`, o iterador e o `toString`, que a DFS não usa |
| `Graph` | **reutilizada, simplificada** | Mantidos `Graph(int V)`, `V()`, `E()`, `addEdge`, `adj` e `degree`. Foram removidos o construtor `Graph(In)` (o formato de entrada é outro), o construtor de cópia e o `toString`. A validação de vértices foi retirada porque as entradas respeitam as restrições, e isso evita custo extra |
| `DepthFirstPaths` | **modificada (reescrita)** | Usada apenas como modelo. A recursão foi trocada por uma DFS **iterativa** com `Stack`, pois `n` chega a `10⁵` e um caminho estouraria a pilha de recursão. O vetor `edgeTo[]` virou `pai[]`. O teste `!marked[w]` virou `w != pai[v]`, suficiente em árvores. A DFS também registra a pré-ordem |
| `StdIn` / `StdOut` | **substituídas** | `StdIn` usa `Scanner`, lento para até `4·10⁵` inteiros em 1 s. Entrou um leitor com `DataInputStream` e buffer de 64 KB. A saída é acumulada em um `StringBuilder` e impressa uma vez |

### Código novo

| Componente | Descrição |
|---|---|
| `canon` | cálculo da forma canônica: ordena os ids dos filhos e consulta o dicionário |
| `readTree` | leitura de uma árvore com `n − 1` arestas |
| `main` | laço dos testes, um `HashMap` novo por teste e impressão de `YES`/`NO` |

### Classes do JDK usadas

`HashMap`, `Arrays.sort` e `Arrays.asList` são estruturas e funções genéricas de propósito geral. Nenhuma delas resolve o isomorfismo: a propriedade estrutural, a DFS e o critério de comparação foram implementados e explicados pelo grupo.

---

## 3. Justificativa das escolhas

- **`HashMap<List<Integer>, Integer>` em vez de `ST`/`RedBlackBST`:** a chave é a lista ordenada de ids dos filhos. O `HashMap` dispensa um comparador de listas e é mais rápido.
- **Dicionário compartilhado por teste:** garante que o mesmo id signifique a mesma forma nas duas árvores. Ele é criado de novo a cada teste, para a memória não crescer entre os testes.
- **Ordenar a chave:** é o que torna o resultado independente da ordem dos filhos. Os ids `[1, 0]` e `[0, 1]` geram a mesma chave `[0, 1]`.
- **Pré-ordem lida ao contrário:** todo filho aparece depois do pai na pré-ordem, então lida de trás para frente garante que os filhos sejam processados antes do pai, sem precisar de `DepthFirstOrder`.
- **Número de filhos:** `degree(v) − (v == 0 ? 0 : 1)`. Todo vértice, exceto a raiz, tem o pai na lista de adjacência.
- **Ids em vez de strings de parênteses:** a soma dos tamanhos das chaves é `n − 1`, enquanto strings poderiam chegar a `O(n²)` em um caminho.

---

## 4. Complexidade

| Recurso | Custo |
|---|---|
| Tempo | `O(n log n)` por teste. A busca é `O(n)`, a ordenação das listas de filhos soma `O(n log n)` e o `HashMap` soma `O(n)` esperado |
| Espaço | `O(n)`: dois `Graph`, vetores `pai`, `ordem` e `id`, pilha e dicionário |

Com `Σ n ≤ 10⁵`, o custo cabe com folga nos limites de 1 s e 512 MB. A demonstração completa está no [Marco 3](marco-3.md).

---

## 5. Casos de teste e casos especiais

O arquivo [`dados/casos-de-teste.txt`](../dados/casos-de-teste.txt) tem 8 testes. A execução do `Main.java` produziu exatamente as saídas esperadas abaixo, conferidas à mão.

| # | `n` | Situação | Esperado |
|---|---|---|---|
| 1 | 3 | exemplo do enunciado: caminho contra raiz com dois filhos | `NO` |
| 2 | 3 | exemplo do enunciado: caminho `1→2→3` contra caminho `1→3→2` | `YES` |
| 3 | 2 | menor `n` possível, com a aresta dada como `2 1` (ordem invertida) | `YES` |
| 4 | 5 | estrela com a raiz de grau 4 (chaves com ids repetidos) | `YES` |
| 5 | 5 | mesma forma, mas o nó interno é o 2 em uma árvore e o 3 na outra | `YES` |
| 6 | 5 | mesma raiz com 2 filhos, mas em uma o nó 2 tem dois filhos folha e na outra tem um filho que tem outro filho | `NO` |
| 7 | 6 | mesma forma com rótulos e ordem das arestas diferentes | `YES` |
| 8 | 5 | árvore com raiz de grau 2 contra caminho a partir da raiz | `NO` |

**Casos especiais tratados:**
- **`n = 2`:** a raiz tem um filho e a folha tem lista vazia.
- **Aresta em qualquer sentido** (`2 1` ou `3 2`): a orientação vem da DFS a partir da raiz, nunca da ordem do par lido.
- **Raiz com grau 1 ou grau alto:** o número de filhos da raiz não desconta o pai.
- **Caminho com `n = 10⁵`:** a DFS iterativa evita `StackOverflowError`. A solução foi executada em um caminho de `10⁵` nós sem erro.
- **Vários testes na mesma entrada:** o dicionário é reiniciado a cada teste.

---

## 6. Evidência de `Accepted`

Submissão no CSES, problema **Tree Isomorphism I**, em Java, em 23/10/2026 às 17:24:07 (+0300). O resultado final é **ACCEPTED**.

![Submissão aceita no CSES](../evidencias/accepted.png)

Na captura, os testes #1 a #12 aparecem como `ACCEPTED`. Entre eles, o maior tempo é **0,61 s** (teste #10), abaixo do limite de 1,00 s.

Arquivo: [`evidencias/accepted.png`](../evidencias/accepted.png).

---

## 7. Conclusão

- A propriedade que resolve o problema é que duas árvores enraizadas são isomorfas se e somente se, em cada nó, os multiconjuntos de subárvores dos filhos se correspondem. A forma canônica com ids transforma essa propriedade em uma comparação de inteiros.
- A DFS é necessária para **orientar** a árvore a partir da raiz. As arestas da entrada são não dirigidas, e sem a busca não há como distinguir pai de filho.
- As implementações de referência do `algs4` foram aproveitadas onde serviam (`Bag`, `Stack`, `Graph`) e adaptadas onde os limites do problema exigiam: DFS iterativa por causa de `n` até `10⁵` e leitura rápida por causa do volume de entrada.
- O resultado é uma solução em `O(n log n)` no tempo e `O(n)` no espaço, aceita em todos os testes mostrados na evidência.

---

## 8. Apresentação

Arquivo: [`apresentacao/apresentacao.pdf`](../apresentacao/apresentacao.pdf), com 6 slides focados na função `canon`.

| Slide | Conteúdo |
|---|---|
| 1 | Capa: "Canon: ids de árvores enraizadas", com os integrantes |
| 2 | Contexto e saída: duas árvores entram e o `canon` devolve um id por raiz. Exemplo com `n = 6` e saída de uma versão de depuração (`ra = 3, rb = 3`) |
| 3 | Como o id é encontrado: tabela da árvore A com lista ordenada e id de cada vértice. Explica lista já vista, lista nova, ordenação e mapa compartilhado |
| 4 | Código do `canon`, parte 1: DFS iterativa que preenche `pai[]` e `ordem[]` (pré-ordem), e o teste `w != pai[v]` no lugar de `marked[]` |
| 5 | Código do `canon`, parte 2: cálculo do id dos filhos para o pai, com `Arrays.sort`, `get`/`put` no mapa e `id[0]` como retorno |
| 6 | Justificativa da complexidade, trecho a trecho, com um único fator log (a ordenação dos filhos) |
