import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    // ---------- entrada rápida ----------
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

    // ---------- árvore (listas de adjacência em arrays) ----------
    private static int[] readTree(int n, int[] head, int[] nxt, int[] to, int[] deg) throws IOException {
        Arrays.fill(head, 0, n, -1);
        Arrays.fill(deg, 0, n, 0);
        int cnt = 0;
        for (int i = 0; i < n - 1; i++) {
            int u = readInt() - 1;
            int v = readInt() - 1;
            to[cnt] = v; nxt[cnt] = head[u]; head[u] = cnt++;
            to[cnt] = u; nxt[cnt] = head[v]; head[v] = cnt++;
            deg[u]++; deg[v]++;
        }
        return null;
    }

    // Calcula o id canônico da raiz (vértice 0).
    private static int canon(int n, int[] head, int[] nxt, int[] to, int[] deg,
                             Map<List<Integer>, Integer> mapa) {
        int[] pai = new int[n];
        int[] ordem = new int[n];
        int[] pilha = new int[n];
        int sp = 0, k = 0;

        pai[0] = -1;
        pilha[sp++] = 0;
        while (sp > 0) {
            int v = pilha[--sp];
            ordem[k++] = v;
            for (int e = head[v]; e != -1; e = nxt[e]) {
                int w = to[e];
                if (w != pai[v]) {
                    pai[w] = v;
                    pilha[sp++] = w;
                }
            }
        }

        int[] id = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            int v = ordem[i];
            int numFilhos = deg[v] - (v == 0 ? 0 : 1);
            Integer[] chave = new Integer[numFilhos];
            int j = 0;
            for (int e = head[v]; e != -1; e = nxt[e]) {
                int w = to[e];
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

            int[] head = new int[n], nxt = new int[2 * n], to = new int[2 * n], deg = new int[n];
            readTree(n, head, nxt, to, deg);
            int[] head2 = new int[n], nxt2 = new int[2 * n], to2 = new int[2 * n], deg2 = new int[n];
            readTree(n, head2, nxt2, to2, deg2);

            Map<List<Integer>, Integer> mapa = new HashMap<List<Integer>, Integer>();
            int ra = canon(n, head, nxt, to, deg, mapa);
            int rb = canon(n, head2, nxt2, to2, deg2, mapa);
            out.append(ra == rb ? "YES" : "NO").append('\n');
        }
        System.out.print(out);
    }
}
