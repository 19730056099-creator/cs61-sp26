# 手写 CS 61B `SLList` 封装实战教程

本教程按照 Josh Hug 课堂的重构脉络组织，从最底层的节点封装，到外壳类、状态缓存与哨兵节点（Sentinel Node），指导逐步完成一份工业级的单链表实现。

---

## 阶段一：定义内部支撑结构（Nested Class）

链表的核心是节点，但外部调用者不应该直接面对指针和节点，因此将其作为**内部私有静态类**嵌入。

### 设计思路

* **`private`**：对外隐藏细节，其他外部类无法直接操作或破坏节点。
* **`static`**：节点不需要访问外部链表对象的实例成员，避免隐式持有外部类引用以节约内存。

### 手写模板

```java
public class SLList {

    /* 内部私有节点类 */
    private static class IntNode {
        public int item;
        public IntNode next;

        public IntNode(int i, IntNode n) {
            item = i;
            next = n;
        }
    }

    // 后续成员变量与方法在此处展开...
}

```

---

## 阶段二：封装外部状态与基础操作

为了让外部用户不需要像操作递归链表一样写出嵌套的构造，`SLList` 提供面向对象的接口。

### 1. 成员变量与构造函数

```java
    /* 私有成员变量：隐藏链表头指针，缓存长度以实现 O(1) 查询 */
    private IntNode first;
    private int size;

    /** 构造单元素链表 */
    public SLList(int x) {
        first = new IntNode(x, null);
        size = 1;
    }

    /** 构造空链表 */
    public SLList() {
        first = null;
        size = 0;
    }

```

### 2. 头部插入与首元素读取（时间复杂度均为 $O(1)$）

```java
    /** 头部插入：让新节点指向原 first，再让 first 指向新节点 */
    public void addFirst(int x) {
        first = new IntNode(x, first);
        size += 1;
    }

    /** 获取链表首个元素的值 */
    public int getFirst() {
        return first.item;
    }

```

### 3. 尾部追加与长度查询

```java
    /** 尾部追加：遍历至链表末尾后挂载新节点 */
    public void addLast(int x) {
        size += 1;

        /* 空链表特判 */
        if (first == null) {
            first = new IntNode(x, null);
            return;
        }

        IntNode p = first;
        while (p.next != null) {
            p = p.next;
        }
        p.next = new IntNode(x, null);
    }

    /** 返回链表长度（读取缓存变量，时间复杂度 O(1)） */
    public int size() {
        return size;
    }

```

---

## 阶段三：进阶优化——引入哨兵节点（Sentinel Node）

在上述 `addLast` 中，如果链表为空（`first == null`），必须进行 `if` 特判。Josh Hug 在课堂上提出了核心优化：**使用一个永远存在的虚拟头节点（Sentinel Node）来消除边界情况**。

### 优化后的结构与完整手写代码

```java
public class SLList {

    /* 内部私有节点类 */
    private static class IntNode {
        public int item;
        public IntNode next;

        public IntNode(int i, IntNode n) {
            item = i;
            next = n;
        }
    }

    /* 哨兵节点：sentinel 自身永远不为 null，真正的第一个数据节点是 sentinel.next */
    private IntNode sentinel;
    private int size;

    /** 构造单元素链表 */
    public SLList(int x) {
        sentinel = new IntNode(63, null); // 哨兵节点的 item 可以填任意占位数字
        sentinel.next = new IntNode(x, null);
        size = 1;
    }

    /** 构造空链表 */
    public SLList() {
        sentinel = new IntNode(63, null);
        size = 0;
    }

    /** 头部插入 */
    public void addFirst(int x) {
        sentinel.next = new IntNode(x, sentinel.next);
        size += 1;
    }

    /** 获取首个元素 */
    public int getFirst() {
        return sentinel.next.item;
    }

    /** 尾部追加：有了哨兵节点后，永远不需要特判空链表 */
    public void addLast(int x) {
        size += 1;

        IntNode p = sentinel;
        // 顺着哨兵一直往后走，直到找到最后一个节点
        while (p.next != null) {
            p = p.next;
        }
        p.next = new IntNode(x, null);
    }

    /** 获取链表大小 */
    public int size() {
        return size;
    }
}

```

---

## 手写自查要点

1. **嵌套类声明**：是否标注为 `private static class IntNode`？
2. **指针重定向**：`addFirst` 中是否写成 `sentinel.next = new IntNode(x, sentinel.next);`？新节点的 `next` 必须先接上旧的首节点，不能颠倒赋值顺序。
3. **循环终止条件**：`addLast` 遍历时判断的是 `p.next != null`，退出循环后 `p` 恰好停在最后一个非空节点上。
4. **计数器维护**：每次 `addFirst` 或 `addLast` 时，是否执行了 `size += 1`？
