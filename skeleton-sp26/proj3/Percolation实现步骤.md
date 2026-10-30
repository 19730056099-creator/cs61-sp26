# CS61B Project 3: Percolation 操作步骤

> 只需要改两个文件：`Percolation.java` 和 `tests/PercolationTest.java`。
> 官方页面：https://sp26.datastructur.es/projects/proj3/

---

## 第 1 步：打开项目

1. 按 61B 的 Assignment Workflow 拉取 starter code
2. 用 IntelliJ 打开 `proj3` 文件夹
3. 打开 `Percolation.java`，准备在里面写代码

---

## 第 2 步：写 import 和成员变量

把 `Percolation.java` 开头改成这样（保留 starter 里已有的类声明和方法签名）：

```java
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    // 上、下、左、右四个方向
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    private final int size;               // 网格边长 N
    private final boolean[][] openSites;  // 每个格子是否打开
    private int openCount;                // 打开的格子数
    private final WeightedQuickUnionUF uf;     // 含 top 和 bottom，判断 percolates()
    private final WeightedQuickUnionUF ufTop;  // 只含 top，判断 isFull()，防止 backwash
    private final int top;                // 虚拟顶部节点编号
    private final int bottom;             // 虚拟底部节点编号
```

---

## 第 3 步：写三个辅助方法

放在类的最下面：

```java
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
```

---

## 第 4 步：写构造函数

```java
    public Percolation(int N) {
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
```

---

## 第 5 步：写 `open`

```java
    public void open(int row, int col) {
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
```

---

## 第 6 步：写其余四个方法

```java
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return openSites[row][col];
    }

    public boolean isFull(int row, int col) {
        validate(row, col);
        return ufTop.connected(xyTo1D(row, col), top);
    }

    public int numberOfOpenSites() {
        return openCount;
    }

    public boolean percolates() {
        return uf.connected(top, bottom);
    }
}
```

到这里 `Percolation.java` 就写完了，完整文件见文末。

---

## 第 7 步：用可视化工具检查

1. IntelliJ 菜单：`Run → Edit Configurations → + → Application`
2. Main class 填 `PercolationPicture`
3. Program arguments 填 `inputFiles/input20.txt`
4. 点 Run，把结果和官网 Outputs 里的 `input20` 图片对比
5. 再换成这些文件各跑一次：`input10-no.txt`、`input1-no.txt`、`input8-dups.txt`
6. 把 Main class 换成 `InteractivePercolationVisualizer`（不用填参数），用鼠标点格子测试

---

## 第 8 步：写测试

把下面的测试方法加到 `tests/PercolationTest.java`。保留 starter 原有的 import 和测试，缺的 import 再补上。

```java
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PercolationTest {

    @Test
    public void testSingleSite() {
        Percolation p = new Percolation(1);
        assertThat(p.percolates()).isFalse();
        p.open(0, 0);
        assertThat(p.isOpen(0, 0)).isTrue();
        assertThat(p.isFull(0, 0)).isTrue();
        assertThat(p.percolates()).isTrue();
    }

    @Test
    public void testOpenTwiceCountsOnce() {
        Percolation p = new Percolation(3);
        p.open(1, 1);
        p.open(1, 1);
        assertThat(p.numberOfOpenSites()).isEqualTo(1);
    }

    @Test
    public void testOutOfBounds() {
        Percolation p = new Percolation(3);
        assertThrows(IndexOutOfBoundsException.class, () -> p.open(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> p.open(0, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> p.isOpen(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> p.isFull(0, -1));
    }

    @Test
    public void testIllegalConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Percolation(0));
        assertThrows(IllegalArgumentException.class, () -> new Percolation(-5));
    }

    @Test
    public void testNotPercolating() {
        Percolation p = new Percolation(3);
        p.open(1, 0);
        p.open(1, 1);
        p.open(1, 2);
        assertThat(p.percolates()).isFalse();
        assertThat(p.isFull(1, 1)).isFalse();
    }

    @Test
    public void testPercolatesVertical() {
        Percolation p = new Percolation(3);
        p.open(0, 0);
        p.open(1, 0);
        assertThat(p.percolates()).isFalse();
        p.open(2, 0);
        assertThat(p.percolates()).isTrue();
        assertThat(p.isFull(2, 0)).isTrue();
    }

    @Test
    public void testDiagonalNotConnected() {
        Percolation p = new Percolation(2);
        p.open(0, 0);
        p.open(1, 1);
        assertThat(p.isFull(1, 1)).isFalse();
        assertThat(p.percolates()).isFalse();
    }

    @Test
    public void testNoBackwash() {
        // ■ □ □
        // ■ □ ■
        // ■ □ ■
        Percolation p = new Percolation(3);
        p.open(0, 0);
        p.open(1, 0);
        p.open(2, 0);
        p.open(1, 2);
        p.open(2, 2);
        assertThat(p.percolates()).isTrue();
        assertThat(p.isFull(2, 2)).isFalse();
        assertThat(p.isFull(1, 2)).isFalse();
    }
}
```

右键 `PercolationTest.java` → Run，确认全部通过。

---

## 第 9 步：风格检查

1. 在 IntelliJ 里对 `Percolation.java` 运行 61B 的风格检查（style checker）
2. 修完所有风格问题

> 风格不过，自动评分不会运行；每 24 小时只有 4 次提交机会。

---

## 第 10 步：提交

```bash
git add Percolation.java tests/PercolationTest.java
git commit -m "Finish proj3 Percolation"
git push
```

然后到 Gradescope 提交。

---

## 第 11 步（可选，不计分）：跑阈值实验

1. Main class 换成 `PercolationStats`，运行
2. 平均值应该接近 **0.593**

---

## 附：`Percolation.java` 完整文件

```java
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    // 上、下、左、右四个方向
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    private final int size;
    private final boolean[][] openSites;
    private int openCount;
    private final WeightedQuickUnionUF uf;
    private final WeightedQuickUnionUF ufTop;
    private final int top;
    private final int bottom;

    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be positive");
        }
        size = N;
        openSites = new boolean[N][N];
        openCount = 0;
        top = N * N;
        bottom = N * N + 1;
        uf = new WeightedQuickUnionUF(N * N + 2);
        ufTop = new WeightedQuickUnionUF(N * N + 1);
    }

    public void open(int row, int col) {
        validate(row, col);
        if (openSites[row][col]) {
            return;
        }
        openSites[row][col] = true;
        openCount++;

        int id = xyTo1D(row, col);
        if (row == 0) {
            uf.union(id, top);
            ufTop.union(id, top);
        }
        if (row == size - 1) {
            uf.union(id, bottom);
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
        validate(row, col);
        return openSites[row][col];
    }

    public boolean isFull(int row, int col) {
        validate(row, col);
        return ufTop.connected(xyTo1D(row, col), top);
    }

    public int numberOfOpenSites() {
        return openCount;
    }

    public boolean percolates() {
        return uf.connected(top, bottom);
    }

    private int xyTo1D(int row, int col) {
        return row * size + col;
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    private void validate(int row, int col) {
        if (!inBounds(row, col)) {
            throw new IndexOutOfBoundsException("(" + row + ", " + col + ") is out of bounds");
        }
    }
}
```
