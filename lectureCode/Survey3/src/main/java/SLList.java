public class SLList {
    //定义内部支撑结构(Nested Class)
//    `private`：对外隐藏细节，其他外部类无法直接操作或破坏节点。
//    链表的核心是节点，但外部调用者不应该直接面对指针和节点，因此将其作为**内部私有静态类**嵌入。
//    `static`：节点不需要访问外部链表对象的实例成员，避免隐式持有外部类引用以节约内存。

    private static class IntNode {
        public int item;
        public IntNode next;
        //私有成员变量：隐藏链表头指针，缓存长度以实现0(1)查询

        public IntNode(int i, IntNode n) {
            item = i;
            next = n;
        }
    }
//    private IntNode first;
//    private int size;
/* 哨兵节点：sentinel 自身永远不为 null，真正的第一个数据节点是 sentinel.next */
    private IntNode sentinel;
    private int size;
//    构造单元素链表
    public SLList(int x) {
        sentinel = new IntNode(63, null);//63 is used to place the space,
        // if you can use the ?? to replace the 63,but in java ,the method is wrong
        sentinel.next = new IntNode(x,null);
        size = 1;
    }
//    构造空链表
    public SLList(){
        sentinel = new IntNode(63,null);
        size = 0;
    }
//    头部插入
    public void addFirst(int x){
        sentinel.next = new IntNode(x,sentinel.next);//理解哨兵节点
        size += 1;
    }
//    获取链表首个元素的值
    public int getFirst(){
//        return sentinel.item;
          return sentinel.next.item;//真正的元素是在sentinel.next上
    }
    /** 尾部追加：遍历至链表末尾后挂载新节点 */
//    Add an item to the end of the list
    public void addLast(int x){
        size += 1;

//        空列表特判
//        该判空机制永远都不会触发
//        if (sentinel == null){
//            sentinel = new IntNode(x,null);
//            return;
//        }

        IntNode p = sentinel;
//        Move p until it reaches the end of the list
        while (p.next != null){
            p = p.next;
        }//为什么我们要使用这个whiel循环来查找末节点？
        //因为你也不知道什么sentinel.next多少个next后才会找到末节点，所以只能循环遍历查找末位节点
        p.next = new IntNode(x,null);
    }
    public int size(){
        return size(sentinel.next);//需要跳过哨兵，从真正的首个数据节点开始
    }//空链表时，sentinel.next是null!
    private static int size(IntNode p){
//        if (p.next == null){//如果p是null,直接抛出NUllPointerException
//            return 1;
//        }
        if (p == null){
            return 0;//递归终点：没有节点时，长度为0
        }

        return 1 + size(p.next);
    }

    public static void main(String[] args) {
        //创建链表节点，观察Objects区域的对象生成与指针连接
//        IntNode L = new IntNode(15,null);
//        L = new IntNode(10,L);
//        L = new IntNode(5,L);
        SLList L = new SLList(15);
        L.addFirst(10);
        L.addFirst(5);
        L.addLast(20);
        System.out.println("First:"+ L.getFirst());//5
        System.out.println(L.size());//4
    }
}
