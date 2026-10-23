# Isomorfismo de Árvores Enraizadas

Dadas duas árvores enraizadas no vértice 1, decidir se são **isomorfas**, isto é, se é possível desenhá-las de modo que fiquem iguais. Problema *Tree Isomorphism* do CSES.

## O problema

**Entrada**
- Um inteiro `t`, o número de testes.
- Para cada teste, um inteiro `n` (nós em ambas as árvores, numerados de `1` a `n`, com o nó 1 como raiz).
- Em seguida, `n − 1` arestas da primeira árvore e `n − 1` arestas da segunda.

**Saída**
- Para cada teste, `YES` se as árvores são isomorfas e `NO` caso contrário.

**Restrições**

| Item | Valor |
|---|---|
| Tempo | 1,00 s |
| Memória | 512 MB |
| `1 ≤ t ≤ 1000` | número de testes |
| `2 ≤ n ≤ 10⁵` | nós por árvore |
| `Σ n ≤ 10⁵` | soma de `n` em todos os testes |

**Exemplo**

```
Entrada          Saída
2                NO
3                YES
1 2
2 3
1 2
1 3
3
1 2
2 3
1 3
3 2
```

## Ideia da solução

Cada vértice recebe um **identificador canônico** da sua subárvore:

1. Uma **DFS (ou BFS)** a partir do vértice 1 orienta a árvore e define pai e filhos de cada vértice.
2. Os vértices são processados **de baixo para cima** (pós-ordem na DFS).
3. Para cada vértice, monta-se a lista dos ids dos filhos, **ordena-se** a lista e consulta-se um dicionário compartilhado pelas duas árvores do teste. Lista nova recebe um novo id e lista já vista reaproveita o id.
4. As árvores são isomorfas se, e somente se, as raízes têm o mesmo id.

Por isso a ordem em que os filhos aparecem na entrada não importa. A DFS deve ser **iterativa** (ou usar BFS por níveis), porque uma árvore em forma de caminho com `n = 10⁵` estouraria a pilha de recursão.

## Complexidade

| Recurso | Custo |
|---|---|
| Tempo | `O(n log n)` por teste (a busca é `Θ(n)` e a ordenação das listas de filhos domina) |
| Espaço | `Θ(n)` |

Com `Σ n ≤ 10⁵`, o custo total cabe com folga em 1 s e 512 MB.

## Acompanhamento

A documentação do desenvolvimento está na pasta [`acompanhamento/`](acompanhamento/):

| Marco | Conteúdo | Arquivo |
|---|---|---|
| 1 | Problema e conhecimento prévio: entrada, saída e restrições, modelagem, classificação do grafo, resultado de aprendizagem, papel da DFS/BFS e rastreamento de uma instância pequena | [`Marco1.md`](Marcos de Aprendizado/Marco1.md) |
| 2 | Componentes conexas: grafo aleatório com V = 6 e E = 6, listas de adjacência, excentricidades, raio, diâmetro e centro, rastreamento da DFS recursiva e análise de complexidade | [`Marco2.md`](MarcosdeAprendizado/Marco2.md) |
| 3 | Estratégia algorítmica: propriedade estrutural, forma canônica das subárvores, componentes do `algs4` e suas adaptações, pseudocódigo, rastreio manual do exemplo e complexidade | [`marco-3.md`](acompanhamento/marco-3.md) |

## Estrutura do repositório

```
.
├── README.md
└── acompanhamento/
    ├── marco-1.md
    ├── marco-2.md
    └── marco-3.md
```
