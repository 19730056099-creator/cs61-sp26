import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    // TODO: Add any necessary instance variables.

    // 上、下、左、右四个方向
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    private final int size;               // 网格边长 N
    private final boolean[][] openSites;  // 每个格子是否打开
    private int openCount;                // 打开的格子数
    private final WeightedQuickUnionUF uf;     // 含 top 和 bottom，判断 percolates()
    private final WeightedQuickUnionUF ufTop;  // 只含 top，判断 isFull()，防止 backwash
    private final int top;                // 虚拟顶部节点编号
    private final int bottom;             // 虚拟底部节点编号

    public Percolation(int N) {
        // TODO: Fill in this constructor.
        if (N <= 0) {
            throw new IllegalArgumentException("N must be positive");
        }
        size = N;
        openSites = new boolean[N][N];
        openCount = 0;
        top = N * N;
        bottom = N * N + 1;
        uf = new WeightedQuickUnionUF(N * N + 2);     // N² 个格子 + top + bottom
        ufTop = new WeightedQuickUnionUF(N * N + 1);  // N² 个格子 + top
    }

    public void open(int row, int col) {
        // TODO: Fill in this method.
        validate(row, col);
        if (openSites[row][col]) {
            return; // 已打开，避免重复计数
        }
        openSites[row][col] = true;
        openCount++;

        int id = xyTo1D(row, col);
        if (row == 0) {
            uf.union(id, top);
            ufTop.union(id, top);
        }
        if (row == size - 1) {
            uf.union(id, bottom); // ufTop 不连 bottom，这样就不会倒灌
        }
        for (int[] d : DIRECTIONS) {
            int r = row + d[0];
            int c = col + d[1];
            if (inBounds(r, c) && openSites[r][c]) {
                int neighbor = xyTo1D(r, c);
                uf.union(id, neighbor);
                ufTop.union(id, neighbor);
            }
        }
    }

    public boolean isOpen(int row, int col) {
        // TODO: Fill in this method.
        validate(row, col);
        return openSites[row][col];
    }

    public boolean isFull(int row, int col) {
        // TODO: Fill in this method.
        validate(row, col);
        return ufTop.connected(xyTo1D(row, col), top);
    }

    public int numberOfOpenSites() {
        // TODO: Fill in this method.
        return openCount;
    }

    public boolean percolates() {
        // TODO: Fill in this method.
        return uf.connected(top, bottom);
    }

    // TODO: Add any useful helper methods (we highly recommend this!).
    // TODO: Remove all TODO comments before submitting.
    // 二维坐标 → 一维编号
    private int xyTo1D(int row, int col) {
        return row * size + col;
    }

    // 判断坐标是否在网格内（不抛异常，用于检查邻居）
    private boolean inBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    // 检查参数，越界就抛异常（用于公开方法）
    private void validate(int row, int col) {
        if (!inBounds(row, col)) {
            throw new IndexOutOfBoundsException("(" + row + ", " + col + ") is out of bounds");
        }
    }

}
