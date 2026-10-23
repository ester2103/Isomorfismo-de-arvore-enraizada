# Estratégia algorítmica: isomorfismo de árvores enraizadas

## 1. Propriedade estrutural central e critério de reconhecimento

**Propriedade:** duas árvores enraizadas são isomórficas se e somente se, em cada nó, o *multiconjunto* das subárvores dos filhos de uma árvore corresponde (par a par, recursivamente) ao multiconjunto das subárvores dos filhos da outra. A ordem dos filhos é irrelevante. É isso que significa "desenhá-las de forma que fiquem iguais".

**Critério:** atribuir a cada subárvore uma **forma canônica**, um identificador que depende só da estrutura:

> `id(v)` = identificador do multiconjunto ordenado `{ id(f₁), id(f₂), …, id(f_k) }` dos filhos de `v`.

Uma folha tem lista vazia, logo recebe sempre o mesmo identificador (digamos 0). Duas subárvores são isomórficas ⇔ têm o mesmo `id`. As árvores completas são isomórficas ⇔ `id(raiz₁) == id(raiz₂)`.

**Como obter a resposta:** a saída é só "YES"/"NO", então basta comparar os dois identificadores das raízes. Para que os ids sejam comparáveis entre as duas árvores, o dicionário `lista ordenada de ids dos filhos → inteiro` deve ser **compartilhado** pelas duas árvores dentro de um mesmo teste.

**Por que não usar a string canônica (parênteses balanceados)?** Ela também funciona (`"(" + filhos ordenados + ")"`), mas as strings podem chegar a O(n) por nó, dando O(n²) em caminhos. A numeração por ids mantém cada lista com tamanho igual ao número de filhos; a soma dos tamanhos é n−1.

## 2. Implementações de referência do `algs4`

O CSES aceita um único arquivo, com a classe pública `Main` e sem `package`. Por isso `Graph`, `Stack` e `Bag` do `algs4` entram como **classes `static` aninhadas** em `Main`, sem alterar a lógica.

| Componente | Papel | Adaptação feita |
|---|---|---|
| `Graph` | Guarda cada árvore como lista de adjacência (não direcionada, pois as arestas vêm sem orientação). | Aninhada em `Main`. Usa só `Graph(int V)`, `addEdge(v, w)`, `adj(v)`, `degree(v)` e `V()`. Foram removidos `Graph(In)` (o formato de entrada é outro), `Graph(Graph)`, `toString`, `toDot` e o `main` de teste. Cada aresta lida tem os vértices decrementados de 1, de modo que a raiz é o vértice 0. Um objeto por árvore. |
| `Bag` | Lista de adjacência interna do `Graph`. | Aninhada em `Main`, sem mudanças. A iteração sai em ordem inversa à inserção, o que não importa, pois as listas de ids são ordenadas. |
| `Stack` | Pilha explícita da DFS iterativa. | Aninhada em `Main`; mantidos só `push`, `pop`, `isEmpty` e `size`. |
| `DepthFirstPaths` | Modelo para o vetor de pais (`edgeTo[]`) a partir da raiz. | Reescrever a DFS de forma **iterativa**, pois a original é recursiva e n pode chegar a 10⁵ (um caminho estouraria a pilha de recursão em Java). Trocar o teste "não marcado" por "diferente do pai", suficiente em árvores. Registrar a ordem em que os vértices saem da pilha (pré-ordem). |
| `StdIn` / `StdOut` | Leitura da entrada e impressão de YES/NO. | **Não usados.** `StdIn` usa `Scanner`, lento para até 4·10⁵ inteiros em 1 s; foi substituído por um leitor próprio sobre `DataInputStream` com buffer de 64 KB. `StdOut` faz flush a cada `print`; foi substituído por um `StringBuilder` impresso uma única vez com `System.out.print`. |
| `java.util.HashMap` (fora do `algs4`; alternativa: `ST` / `RedBlackBST`) | Dicionário `lista de ids → id`. | Chave: a lista de ids dos filhos, **ordenada** (`Arrays.asList` sobre o vetor ordenado). Com `ST`/`RedBlackBST` seria necessário um comparador de listas; `HashMap` é mais simples e mais rápido. |
| `java.util.Arrays.sort` | Ordenar os ids dos filhos de cada nó. | Aplicar sobre o vetor de ids de cada nó. |

**Pós-ordem sem `DepthFirstOrder`:** na pré-ordem produzida pela pilha, todo filho aparece depois do pai. Percorrendo essa ordem **de trás para frente**, os filhos são processados antes do pai, que é exatamente o que o cálculo dos ids exige.

**Papel de cada etapa:**
1. `readTree` lê cada árvore para um `Graph`.
2. A DFS iterativa (com `Stack`) gera `pai[]` e a pré-ordem.
3. Percorrendo a pré-ordem ao contrário, calcula-se `id[v]` de cada nó com base nos ids dos filhos (já calculados).
4. Compara-se o id da raiz da primeira árvore com o da segunda.

## 3. Algoritmo (pseudocódigo)

```
main:
    t ← readInt()
    saída ← StringBuilder vazio
    para cada teste (t vezes):
        n ← readInt()
        G1 ← readTree(n)
        G2 ← readTree(n)
        mapa ← HashMap vazio            // compartilhado entre as duas árvores, novo a cada teste
        r1 ← canon(G1, mapa)
        r2 ← canon(G2, mapa)
        saída.append(r1 == r2 ? "YES" : "NO") e quebra de linha
    imprime saída de uma só vez          // System.out.print, sem flush por linha

readTree(n):
    G ← Graph(n)
    repete n-1 vezes:
        u ← readInt() - 1
        v ← readInt() - 1
        G.addEdge(u, v)
    retorna G

canon(G, mapa):
    n ← G.V()
    pai[0] ← -1
    pilha ← Stack<Integer> vazia; pilha.push(0); k ← 0
    enquanto pilha não vazia:
        v ← pilha.pop(); ordem[k++] ← v
        para w em G.adj(v), com w ≠ pai[v]:
            pai[w] ← v
            pilha.push(w)

    para i de n-1 até 0:                 // pré-ordem ao contrário
        v ← ordem[i]
        numFilhos ← G.degree(v) - (v == 0 ? 0 : 1)   // a raiz não tem pai
        chave ← vetor de tamanho numFilhos
        j ← 0
        para w em G.adj(v), com w ≠ pai[v]:
            chave[j++] ← id[w]
        ordena chave
        lista ← chave visto como lista (Arrays.asList)
        se lista ∉ mapa: mapa[lista] ← tamanho(mapa)
        id[v] ← mapa[lista]

    retorna id[0]
```

## 4. Rastreio manual (exemplo do enunciado, n = 3)

Nos rastreios, os nós aparecem com a numeração do enunciado (1 a 3); no código, cada um é decrementado de 1.

### Teste 1

Árvore A: arestas `1-2, 2-3` (caminho 1→2→3).
Árvore B: arestas `1-2, 1-3` (raiz com dois filhos).

**Árvore A.** DFS iterativa a partir de 1: pilha `[1]`; sai o 1 (`ordem = 1`), entra o 2 (`pai[2]=1`); sai o 2, entra o 3 (`pai[3]=2`); sai o 3. Pré-ordem: `1, 2, 3`. Processamento (ao contrário): `3, 2, 1`.

| Nó | Lista de ids dos filhos | `mapa` após a decisão | `id` |
|---|---|---|---|
| 3 | `[]` | `{[]→0}` (nova) | 0 |
| 2 | `[0]` | `{[]→0, [0]→1}` (nova) | 1 |
| 1 | `[1]` | `{[]→0, [0]→1, [1]→2}` (nova) | 2 |

`id_A[1] = 2`.

**Árvore B** (mesmo `mapa`). DFS: sai o 1 e entram 3 e 2 (`pai[2]=pai[3]=1`); como o `Bag` itera em ordem inversa, o 3 entra primeiro e o 2 fica no topo. Pré-ordem: `1, 2, 3`. Processamento: `3, 2, 1`.

| Nó | Lista | Decisão | `id` |
|---|---|---|---|
| 3 | `[]` | já existe | 0 |
| 2 | `[]` | já existe | 0 |
| 1 | `[0, 0]` (ordenada) | nova: `[0,0]→3` | 3 |

`id_B[1] = 3`. Como `2 ≠ 3`, a saída é **NO** ✔ (coincide com o exemplo).

### Teste 2

Árvore A: `1-2, 2-3` (caminho, `id = 2` como acima).
Árvore B: `1-3, 3-2`, em que `pai[3]=1`, `pai[2]=3`. Pré-ordem: `1, 3, 2`. Processamento: `2, 3, 1`.

- Nó 2: `[] → 0`
- Nó 3: `[0] → 1`
- Nó 1: `[1] → 2`

`id_B[1] = 2 = id_A[1]` ⇒ **YES** ✔. O caso mostra que a rotulagem dos nós é irrelevante: só a estrutura conta.

**Observação:** a ordenação de `L` é o que garante invariância à ordem dos filhos. Por exemplo, filhos com ids `[1,0]` e `[0,1]` produzem a mesma chave `[0,1]`.

## 5. Complexidade

Seja n o número de nós de cada árvore.

**Tempo:**
- Construção dos dois `Graph`s e DFS iterativa: O(n).
- Ordenação das listas de filhos: soma de `kᵥ log kᵥ` ≤ O(n log n), com `kᵥ` o número de filhos de `v` (Σ kᵥ = n−1).
- Operações no `HashMap`: cada chave tem tamanho `kᵥ`; hashing e comparação custam O(kᵥ), somando O(n) esperado.
- **Total por teste: O(n log n)**, dominado pela ordenação. Somando os testes (Σn ≤ 10⁵), cabe folgadamente em 1 s.

**Memória:**
- **Representação dos grafos:** dois `Graph`s com V = n e E = n−1, ou seja, Θ(n) (cada aresta aparece em duas listas do `Bag`).
- **Memória auxiliar:** vetores `pai[]`, `id[]` e `ordem[]`, a `Stack` da DFS, as listas-chave, o `HashMap`, o `StringBuilder` de saída e o buffer de leitura (64 KB, constante), todos O(n) (a soma dos tamanhos das chaves é n−1). **Auxiliar total: O(n)**, bem abaixo dos 512 MB.

**Cuidados práticos:** DFS iterativa (evita `StackOverflowError`); reinicializar o `mapa` a cada teste; decrementar 1 dos vértices lidos; nunca confundir `Graph` não direcionado com árvore enraizada, sempre excluindo o pai ao listar filhos; manter tudo em um único arquivo com a classe `Main`, sem `package`.
