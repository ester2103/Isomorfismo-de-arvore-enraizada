import algs4.Graph;
import algs4.Stack;
import algs4.StdIn;
import algs4.StdOut;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Isomorfismo de árvores enraizadas (raiz = nó 1).
 *
 * Ideia: cada subárvore recebe um id inteiro que depende só da estrutura.
 * id(v) = número associado à lista ORDENADA dos ids dos filhos de v.
 * O dicionário lista -> id é compartilhado pelas duas árvores do mesmo teste,
 * então as árvores são isomórficas se e somente se id(raiz1) == id(raiz2).
 */
public class Main {

    // Calcula o id canônico da raiz (vértice 0) de uma árvore.
    private static int canon(Graph g, Map<List<Integer>, Integer> mapa) {
        int n = g.V();
        int[] pai = new int[n];      // análogo ao edgeTo[] do DepthFirstPaths
        int[] ordem = new int[n];    // ordem em que os vértices saem da pilha (pré-ordem)
        int k = 0;

        // DFS iterativa (a do DepthFirstPaths é recursiva e estouraria a pilha com n = 10^5)
        pai[0] = -1;
        Stack<Integer> pilha = new Stack<Integer>();
        pilha.push(0);
        while (!pilha.isEmpty()) {
            int v = pilha.pop();
            ordem[k++] = v;
            for (int w : g.adj(v)) {
                if (w != pai[v]) {   // em árvore, "não é o pai" equivale a "não visitado"
                    pai[w] = v;
                    pilha.push(w);
                }
            }
        }

        // Em pré-ordem todo filho aparece depois do pai.
        // Percorrendo ao contrário, os filhos são processados antes do pai.
        int[] id = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            int v = ordem[i];
            int numFilhos = g.degree(v) - (v == 0 ? 0 : 1);
            Integer[] chave = new Integer[numFilhos];
            int j = 0;
            for (int w : g.adj(v)) {
                if (w != pai[v]) chave[j++] = id[w];
            }
            Arrays.sort(chave);                       // ordem dos filhos é irrelevante
            List<Integer> lista = Arrays.asList(chave);

            Integer val = mapa.get(lista);
            if (val == null) {
                val = mapa.size();                    // novo id
                mapa.put(lista, val);
            }
            id[v] = val;
        }
        return id[0];
    }

    public static void main(String[] args) {
        int t = StdIn.readInt();
        while (t-- > 0) {
            int n = StdIn.readInt();

            Graph a = new Graph(n);
            for (int i = 0; i < n - 1; i++) {
                int u = StdIn.readInt() - 1;
                int v = StdIn.readInt() - 1;
                a.addEdge(u, v);
            }
            Graph b = new Graph(n);
            for (int i = 0; i < n - 1; i++) {
                int u = StdIn.readInt() - 1;
                int v = StdIn.readInt() - 1;
                b.addEdge(u, v);
            }

            Map<List<Integer>, Integer> mapa = new HashMap<List<Integer>, Integer>();
            int ra = canon(a, mapa);
            int rb = canon(b, mapa);   // mesmo mapa: ids comparáveis
            StdOut.println(ra == rb ? "SIM" : "NÃO");
        }
    }
}
