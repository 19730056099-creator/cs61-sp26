package lecture7.src;
//Last time:Remaining Problem:
//    slow to get to the end of the list
//    Only stores ints
//    Slow to access items in the middle of the list

//Question: Suppose we want to support addLast, getLast, and removeLast. Will having a last pointer make these methods fast, even on long lists?
//A. Yes
//B. No, addLast would be slow for long lists.
//C. No, getLast would be slow for long lists.
//D. No, removeLast would be slow for long lists.
//Correct answer: D
//Explanation:
//addLast: O(1). We attach the new node after last, then update last.
//        getLast: O(1). We return last.item directly.
//removeLast: O(n). We need the second-to-last node so we can set its next to null and move last back to it.
//        In a singly linked list, a node only knows the next node, not the previous one.
//        So we must start from the front and walk through the whole list.
//        Fix: Make it a doubly linked list (DLList). Each node has a prev pointer,
//        so last.prev gives the second-to-last node in one step, and removeLast becomes O(1).
//Trade-off: Each node uses more memory (one extra pointer), and the code must maintain both next and prev, which makes bugs more likely.
//        This is why we use a sentinel node to simplify edge cases.
public class SLList<Pizza> {
    private  class Node {
//        Pizza 是 DLList 的类型参数,是属于每个 DLList 对象的。
//        而 static 嵌套类是属于类本身的,不依赖任何对象,所以它看不到 Pizza,编译器就报错了。
        //类型参数的作用范围，是“一个具体的实例化版本”
        //DLList<Integer> a = new DLList<>();
        //DLList<String> b = new DLList<>();
        //也就是，Pizza具体是什么，要等到创建DLList时才确定

        //实际上几年前java也是没有泛型这一说法的，是直接使用Object来装，
        //Object 是"万能类型"的来源,但用它存数据取出来要强转、没有类型检查,所以才需要泛型来代替它。
        //而其实Object作为类的祖先，是不能直接存放基本类型的，是因为java将基本类型打包为包装类，所以才能赋值给Object
        //question:怎么实现包装类，如Integer.例外类型检查、转换是怎么实现的?

    //为什么 static 嵌套类看不到它
        //static 的意思是:这个类不依赖任何具体的 DLList,它只有一份,被所有 DLList 共享。
        //但 Pizza 是什么,取决于具体是哪一个 DLList(Integer 版还是 String 版)。
        // static 的 Node 不属于任何一个具体的 DLList,自然就不知道该用哪个版本的 Pizza,所以编译器拒绝让它使用。
        Pizza item;
        Node next;

        public Node(Pizza i, Node n) {
            item = i;
            next = n;
        }
    }

    private Node sentinel;
    private int size;

    public SLList() {
        sentinel = new Node(null, null);
        size = 0;
    }

    //    头部插入
    public void addFirst(Pizza x){
        size += 1;
        sentinel.next = new Node(x,sentinel.next);
    }

    //    递归查找
    public Pizza get(Node node,int i){
        if (i == 0){
            return node.item;
        }
        return get(node.next,i-1);
    }
    // Return the int at index i in the list
    public  Pizza get(int i){
        return get(sentinel.next,i);
    }

    public int size(){
        return size;
    }

}
