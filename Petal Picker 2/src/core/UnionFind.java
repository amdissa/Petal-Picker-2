package core;

public class UnionFind {
    private int[] set;
    private int size;
    /* Creates a UnionFind data structure holding N items. Initially, all
       items are in disjoint sets. */
    public UnionFind(int N) {
        size = N;
        set = new int[N];
        for (int i = 0; i < N; i++) {
            set[i] = -1;
        }
    }

    /* Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        int p = find(v);
        return -1 * set[p];
    }

    public int largestSetSize() {
        int x = 0;
        for (int i : set) {
            if (i < x) {
                x = i;
            }
        }
        return -1 * x;
    }

    /* Returns the parent of V. If V is the root of a tree, returns the
       negative size of the tree for which V is the root. */
    public int parent(int v) {
        return set[v];
    }

    /* Returns true if nodes/vertices V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        if (find(v1) == find(v2)) {
            return true;
        }
        return false;
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid items are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        int x = v;
        if (v < 0 || v >= size) {
            throw new IllegalArgumentException("Invalid index.");
        }
        while (set[v] > -1) {
            v = set[v];
        }
        compressor(v, x);
        return v;
    }

    private void compressor(int root, int start) {
        int s = start;
        int temp;
        while (s != root) {
            temp = set[s];
            set[s] = root;
            s = temp;
        }
    }

    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing an item with itself or items that are
       already connected should not change the structure. */
    public void union(int v1, int v2) {
        int a = find(v1);
        int b = find(v2);
        if (a != b) {
            if (set[a] < set[b]) {
                int x = set[b];
                set[b] = a;
                set[a] = set[a] + x;
            } else {
                int x = set[a];
                set[a] = b;
                set[b] = set[b] + x;
            }
        }
    }
}
