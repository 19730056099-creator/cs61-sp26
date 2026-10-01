public class IntList {
    int first;
    IntList rest;

    public IntList(int f, IntList r) {
        first = f;//first中存储的就是当前节点中的数据
        rest = r;//rest中存储的是对下一个节点的引用
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
        IntList p = this;
        int sum = 0;
        while(p != null){
            sum += p.first;
            p = p.rest;
        }
        return sum;
    }

    /**
     * Destructively adds x to the end of the list.
     */
    public void addLast(int x) {
        // Optional: Fill in this code
        IntList p = this;
//        while(p != null){
//            p = p.rest;
//        }这个代码有问题，当代码循环找到了p = null，此时,p已经不指向任何节点了。没法再修改最后一个节点的rest.
//        p = new IntList(x,null);这个是非破坏性代码，不符合题目要求
        //解决方案是在p.rest == 0 的时候就停下来
        while (p.rest != null){
            p = p.rest;
        }
        p.rest = new IntList(x,null);
    }

    /**
     * Destructively adds x to the front of this IntList.
     * This is a bit tricky to implement. The standard way to do this would be
     * to return a new IntList, but for practice, this implementation should
     * be destructive.
     */
    public void addFirst(int x) {
        // Optional: Fill in this code
//        p.rest = new IntList(x,p.rest);//这样的话实际上新节点是插在第二个位置上的
        //把原来头节点的内容"挪"到一个新节点里，然后把头节点改成要加的值：
        this.rest = new IntList(this.first,this.rest);
        this.first = x;
    }

    public static void main() {
//            IntList L = new IntList(2,null);
//            L.addFirst(3);
//            L.addLast(5);
//            System.out.println(L.sum());
        //上述代码是用于测试sum()方法的
        //在写完代码测试时，addFirst和addLast方法是没有被实现的，所以上述代码运行之后最后的结果是2
        //因为实际上addFirst和addLast都没有生效，是是一个占位声明
            IntList L = new IntList(2,null);
            L.addLast(5);
            L.addFirst(3);
            System.out.println(L.sum());
    }
}

/*
    破坏性与非破坏性
        破坏性：原来的链表对象被改动了。addLast 里 p.rest = new IntList(x, null);
               修改了原链表最后一个节点的 rest，调用之后，原来的 L 本身就变长了。
        非破坏性：原链表一个节点都不动，而是返回一条新的链表，原来的 L 保持不变。
    是否破坏性，与是否使用了new是无关的
 */
