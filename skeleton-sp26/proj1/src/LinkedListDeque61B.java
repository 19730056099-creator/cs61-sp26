import java.util.ArrayList;
import java.util.List;

public class LinkedListDeque61B<T> implements Deque61B<T>{
    private class Node{
        Node prev;
        T item;
        Node next;
        public Node(Node p,T i,Node n){
            prev = p;
            item = i;
            next = n;
        }
    }

    private Node sentinel;

    private int size;

    public LinkedListDeque61B(){
        sentinel = new Node(null,null,null);
        sentinel.prev = sentinel;
//        sentinel.next = sentinel.prev;
        sentinel.next = sentinel;//为什么sentinel.next指向的也是sentinel本身，这个sentinel.next图像上不是指向sentinel.prev位置了么?
        //答案是：引用永远指向整个对象，不会指向某一格。
        size = 0;
    }



    /**
     * Add {@code x} to the front of the deque. Assumes {@code x} is never null.
     *
     * @param x item to add1
     */

    //即便没有这个@override,方法依旧能够重写，程序运行起来完全一样，这个注解的作用是
    //让编译器帮你检查:我写的这个方法，真的重写了父类或接口的某个方法
//    举个例子，如果你不小心把方法名写错了：
//    public void addFrist(T x) { ... }   // 拼错了
//    没有 @Override：编译器不会报错，只会当成你新写了一个叫 addFrist 的方法，真正的 addFirst 其实没实现，bug 很难发现。
//    有 @Override：编译器直接报错，告诉你"这个方法没有重写任何东西"。
    @Override
    public void addFirst(T x) {
        size += 1;
        sentinel.next = new Node(sentinel,x,sentinel.next);
        sentinel.next.next.prev =sentinel.next;//如果没有这一串代码的话sentinel → N → A，但 A 的 prev 还指着 sentinel。
        //而正确的逻辑应该是A的prev要指向N
        //这也就是我们说的要注意释放改变指针的指向
    }

    /**
     * Add {@code x} to the back of the deque. Assumes {@code x} is never null.
     *
     * @param x item to add
     */
    @Override
    public void addLast(T x) {
        size +=1;
        sentinel.prev = new Node(sentinel.prev,x,sentinel);
        sentinel.prev.prev.next = sentinel.prev;
    }

    /**
     * Returns a List copy of the deque. Does not alter the deque.
     *
     * @return a new list copy of the deque.
     */
    @Override
    public List toList() {
        List<T> returnList = new ArrayList<>();
        Node p = sentinel.next;              // 从第一个元素开始
        while (p != sentinel) {         // 没走回哨兵就继续
            returnList.add(p.item);
            p = p.next;               // 走到下一个
        }
        return returnList;
    }

    /**
     * Returns if the deque is empty. Does not alter the deque.
     *
     * @return {@code true} if the deque has no elements, {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
//        if (size == 0){
//            return true;
//        }
//        return false;
        //这样写，逻辑完全没有问题
        //但是可以更加简洁一些，因为size==0这个表达式表达式本身就会算出true和false
        return size==0;
    }

    /**
     * Returns the size of the deque. Does not alter the deque.
     *
     * @return the number of items in the deque.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Return the element at the front of the deque, if it exists.
     *
     * @return element, otherwise {@code null}.
     */
    @Override
    public T getFirst() {
//        if (size == 0){
//            return null;
//        }
        return sentinel.next.item;
    }

    /**
     * Return the element at the back of the deque, if it exists.
     *
     * @return element, otherwise {@code null}.
     */
    @Override
    public T getLast() {
//        if (size == 0){
//            return null;
//        }
        //经过claude的指点，发现这个if 其实可以不用写,当size == 0的时候，setinel.prev其实指向的是自己
        //所以sentinel.prev.item其实就是自己的item，为null
        //同理,getFirst也是一个道理
        return sentinel.prev.item;
    }

    /**
     * Remove and return the element at the front of the deque, if it exists.
     *
     * @return removed element, otherwise {@code null}.
     */
    @Override
    public T removeFirst() {
        return null;
    }

    /**
     * Remove and return the element at the back of the deque, if it exists.
     *
     * @return removed element, otherwise {@code null}.
     */
    @Override
    public T removeLast() {
        return null;
    }

    /**
     * The Deque61B abstract data type does not typically have a get method,
     * but we've included this extra operation to provide you with some
     * extra programming practice. Gets the element, iteratively. Returns
     * null if index is out of bounds. Does not alter the deque.
     *
     * @param index index to get
     * @return element at {@code index} in the deque
     */
    @Override
    public T get(int index) {
        return null;
    }

    /**
     * This method technically shouldn't be in the interface, but it's here
     * to make testing nice. Gets an element, recursively. Returns null if
     * index is out of bounds. Does not alter the deque.
     *
     * @param index index to get
     * @return element at {@code index} in the deque
     */
    @Override
    public T getRecursive(int index) {
        return null;
    }

    public static void main(String[] args) {
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
        lld.addFirst(0);
        lld.addFirst(-1);
        lld.addLast(1);
//        lld.addLast(0);//[0]
//        lld.addLast(1);//[0,1]
//        lld.addFirst(-1);//[-1,0,1]
    }
}
