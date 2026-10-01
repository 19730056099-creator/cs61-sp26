public class IntList {
    int first;
    IntList rest;

    public IntList(int f, IntList r) {
        first = f;
        rest = r;
    }

    /** Return the size of the list using... recursion! */
    public int size() {
        if (rest == null) {
            return 1;
        }
        return 1 + this.rest.size();
    }

    /** Return the size of the list using no recursion! */
    public int iterativeSize() {
        IntList p = this;
        int totalSize = 0;
        while (p != null) {
            totalSize += 1;
            p = p.rest;
        }
        return totalSize;
    }

    /** Returns the ith item of this IntList. */
    public int get(int i) {
        if (i == 0) {
            return first;
        }
        return rest.get(i - 1);
    }

    /**
     * Returns an IntList identical to L, but with
     * each element incremented by x. Modifies the original list.
     * You are not allowed to use "new" in this method.
     */
    public static IntList incrRecursiveDestructive(IntList L, int x) {
        // TODO: Fill in this code
        if (L == null) {
            return null;
        }
        L.first = L.first + x;
//        L = incrRecursiveDestructive(L.rest, x);直接调用方法就可以了，并不需要这些将值又赋给这个L,如果又赋值一遍的话，程序逻辑是有问题的   // 处理后面的部分
        incrRecursiveDestructive(L.rest,x);//这样子的话，运用递归的方式，每次只处理当前这一个节点，剩下的L.rest本身也是一条链表,
        //只是比原来短了一个节点，所以可以把它当作新的链表连同x值再次传进同一个方法。每次传进去的链表都比上一次短，最后短到null,递归停下来
        return L;
        //
        }
//    单独一个_在java里的含义，随版本变过
//      java22起：它有了新用途，叫未命名变量，意思是"这个值我不会用到，随便放着"
    // ____在java中是没有特殊含义的，这个是claude给留的位置（占位符），方便后续学生的填充代码
    /*
     * =================================================================
     * OPTIONAL METHODS
     * =================================================================
     */

    /**
     * Returns the sum of all elements in the IntList.
     */
    public int sum() {
        // Optional: Fill in this code
        return 0;
    }

    /**
     * Destructively adds x to the end of the list.
     */
    public void addLast(int x) {
        // Optional: Fill in this code
    }

    /**
     * Destructively adds x to the front of this IntList.
     * This is a bit tricky to implement. The standard way to do this would be
     * to return a new IntList, but for practice, this implementation should
     * be destructive.
     */
    public void addFirst(int x) {
        // Optional: Fill in this code
    }
}
