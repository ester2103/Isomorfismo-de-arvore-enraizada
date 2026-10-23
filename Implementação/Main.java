import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Main {

    
    //  Bag (algs4): lista de adjacência interna do Graph

    static class Bag<Item> implements Iterable<Item> {
        private Node<Item> first;
        private int n;

        private static class Node<Item> {
            private Item item;
            private Node<Item> next;
        }

        public Bag() { first = null; n = 0; }

        public boolean isEmpty() { return first == null; }

        public int size() { return n; }

        public void add(Item item) {
            Node<Item> oldfirst = first;
            first = new Node<Item>();
            first.item = item;
            first.next = oldfirst;
            n++;
        }

        public Iterator<Item> iterator() { return new LinkedIterator(first); }

        private class LinkedIterator implements Iterator<Item> {
            private Node<Item> current;

            public LinkedIterator(Node<Item> first) { current = first; }

            public boolean hasNext() { return current != null; }

            public Item next() {
                if (!hasNext()) throw new NoSuchElementException();
                Item item = current.item;
                current = current.next;
                return item;
            }
        }
    }
  
    //  Stack (algs4): pilha explícita da DFS iterativa
   
    static class Stack<Item> {
        private Node<Item> first;
        private int n;

        private static class Node<Item> {
            private Item item;
            private Node<Item> next;
        }

        public Stack() { first = null; n = 0; }

        public boolean isEmpty() { return first == null; }

        public int size() { return n; }

        public void push(Item item) {
            Node<Item> oldfirst = first;
            first = new Node<Item>();
            first.item = item;
            first.next = oldfirst;
            n++;
        }

        public Item pop() {
            if (isEmpty()) throw new NoSuchElementException("Stack underflow");
            Item item = first.item;
            first = first.next;
            n--;
            return item;
        }
    }

    //  Graph (algs4): árvore como lista de adjacência (não direcionada)
    static class Graph {
        private final int V;
        private int E;
        private Bag<Integer>[] adj;

        @SuppressWarnings("unchecked")
        public Graph(int V) {
            if (V < 0) throw new IllegalArgumentException("Number of vertices must be non-negative");
            this.V = V;
            this.E = 0;
            adj = (Bag<Integer>[]) new Bag[V];
            for (int v = 0; v < V; v++) adj[v] = new Bag<Integer>();
        }

        public int V() { return V; }

        public int E() { return E; }

        public void addEdge(int v, int w) {
            E++;
            adj[v].add(w);
            adj[w].add(v);
        }

        public Iterable<Integer> adj(int v) { return adj[v]; }

        public int degree(int v) { return adj[v].size(); }
    }

   
    //  Entrada rápida (substitui StdIn/Scanner, lento para 4·10^5 inteiros)
  
    private static final DataInputStream in = new DataInputStream(System.in);
    private static final byte[] buf = new byte[1 << 16];
    private static int bufLen = 0, bufPtr = 0;

    private static int readByte() throws IOException {
        if (bufPtr == bufLen) {
            bufLen = in.read(buf, 0, buf.length);
            bufPtr = 0;
            if (bufLen <= 0) return -1;
        }
        return buf[bufPtr++];
    }

    private static int readInt() throws IOException {
        int c = readByte();
        while (c != '-' && (c < '0' || c > '9')) c = readByte();
        boolean neg = false;
        if (c == '-') { neg = true; c = readByte(); }
        int r = 0;
        while (c >= '0' && c <= '9') {
            r = r * 10 + (c - '0');
            c = readByte();
        }
        return neg ? -r : r;
    }

    // Lê n-1 arestas (vértices decrementados de 1; a raiz é o vértice 0)
    private static Graph readTree(int n) throws IOException {
        Graph g = new Graph(n);
        for (int i = 0; i < n - 1; i++) {
            int u = readInt() - 1;
            int v = readInt() - 1;
            g.addEdge(u, v);
        }
        return g;
    }

    
    //  Forma canônica: DFS iterativa (Stack) + pré-ordem lida ao contrário
   
    private static int canon(Graph g, Map<List<Integer>, Integer> mapa) {
        int n = g.V();
        int[] pai = new int[n];
        int[] ordem = new int[n];
        int k = 0;

        Stack<Integer> pilha = new Stack<Integer>();
        pai[0] = -1;
        pilha.push(0);
        while (!pilha.isEmpty()) {
            int v = pilha.pop();
            ordem[k++] = v;
            for (int w : g.adj(v)) {
                if (w != pai[v]) {
                    pai[w] = v;
                    pilha.push(w);
                }
            }
        }

        int[] id = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            int v = ordem[i];
            int numFilhos = g.degree(v) - (v == 0 ? 0 : 1);
            Integer[] chave = new Integer[numFilhos];
            int j = 0;
            for (int w : g.adj(v)) {
                if (w != pai[v]) chave[j++] = id[w];
            }
            Arrays.sort(chave);
            List<Integer> lista = Arrays.asList(chave);

            Integer val = mapa.get(lista);
            if (val == null) {
                val = mapa.size();
                mapa.put(lista, val);
            }
            id[v] = val;
        }
        return id[0];
    }

    public static void main(String[] args) throws IOException {
        int t = readInt();
        StringBuilder out = new StringBuilder();
        while (t-- > 0) {
            int n = readInt();

            Graph g1 = readTree(n);
            Graph g2 = readTree(n);

            // dicionário compartilhado entre as duas árvores do teste
            Map<List<Integer>, Integer> mapa = new HashMap<List<Integer>, Integer>();
            int ra = canon(g1, mapa);
            int rb = canon(g2, mapa);
            out.append(ra == rb ? "YES" : "NO").append('\n');
        }
        System.out.print(out);
    }
}
