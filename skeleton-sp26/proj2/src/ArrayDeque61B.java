import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ArrayDeque61B<T> implements Deque61B<T>{
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque61B(){
        items = (T[]) new Object[8];
        //(T[]) new Object[8] 这种写法是题目给的：Java 不允许直接 new T[8]，
        // 只能这样绕一下。IntelliJ 可能会给黄色警告，可以忽略。
        size = 0;
        nextFirst = 3;//任意选，只要两者相邻即可
        nextLast = 4;
    }

    //helper to implement the addFirst
    // 往左走一步：0 的左边是最后一格
    private int minusOne(int index) {
//        if (index == 0) {
//            return items.length - 1;
//        }
//        return index - 1;
        return (index - 1 + items.length) % items.length;
    }
    // 往右走一步：最后一格的右边是 0
    private int plusOne(int index) {
        return (index + 1) % items.length;
    }

    /** a method for resizing up **/
    public void resizeup(){
        T[] temp = (T[]) new Object[items.length * 2];
        for(int i = 0;i < size;i++){
            temp[i] = items[(nextFirst + 1 + i) % items.length];
        }
        items = temp;
        nextFirst = items.length - 1;   // 注意：这里 items 已经是新数组了
        nextLast = size;
    }

    /** a method for resizing up **/
    public void resizedown(){
        T[] temp = (T[]) new Object[items.length / 2];
        for(int i = 0;i < size;i++){
            temp[i] = items[(nextFirst + 1 + i) % items.length];
        }
        items = temp;
        nextFirst = items.length - 1;   // 注意：这里 items 已经是新数组了
        nextLast = size;
    }

    /**
     * Add {@code x} to the front of the deque. Assumes {@code x} is never null.
     *
     * @param x item to add
     */
    @Override
    public void addFirst(T x) {
//        while(size > items.length){
//            resizeup();
//        }size就不可能出现 > items.length的情况,最多就是当size == items.length的时候，数组扩容
        if(size == items.length){
            resizeup();
        }
        items[nextFirst] = x;             // 1. 放进 nextFirst 指的空位
        nextFirst = minusOne(nextFirst);  // 2. nextFirst 往左移一格
        size += 1;                        // 3. 元素个数加一
    }

    /**
     * Add {@code x} to the back of the deque. Assumes {@code x} is never null.
     *
     * @param x item to add
     */
    @Override
    public void addLast(T x) {
        if(size == items.length){
            resizeup();
        }
        items[nextLast] = x;              // 1. 放进 nextLast 指的空位
        nextLast = plusOne(nextLast);     // 2. nextLast 往右移一格
        size += 1;                        // 3. 元素个数加一
    }

    /**
     * Return the element at the front of the deque, if it exists.
     *
     * @return element, otherwise {@code null}.
     */
    @Override
    public T getFirst() {
        if (size == 0){
            return null;
        }
        return items[plusOne(nextFirst)];
    }

    /**
     * Return the element at the back of the deque, if it exists.
     *
     * @return element, otherwise {@code null}.
     */
    @Override
    public T getLast() {
        if (size == 0){
            return null;
        }
        return items[minusOne(nextLast)];
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
//        if (index > items.length| index < 0 | index >= size){
//            return null;
//        }
        //index > items.length可以删除，因为其实index>=size已经包含这个条件了
        if (index < 0 | index >= size){
            return null;
        }
        //|和||的区别,||只要前面成立就不在往下算
        int start = plusOne(nextFirst);
        return items[(start + index) % items.length];
//        return items[index];
        //在使用者的角度用户根本不知道我们的起始点是从那个索引下标开始的，也不需要知道，这些其实都应该封装起来
        //所以使用这个start来定义起始点，index作为具体的索引下标，当索引下标start + index > 数组的长度的时候，取余，达成回环的操作

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
        throw new UnsupportedOperationException("No need to implement getRecursive for ArrayDeque61B.");
    }

    /**
     * Returns if the deque is empty. Does not alter the deque.
     *
     * @return {@code true} if the deque has no elements, {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
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
     * Returns the length of the ArrayDeque61B. Does not alter the deque.
     **/
    public int length(){
        return items.length;
    }

    /**
     * Returns a List copy of the deque. Does not alter the deque.
     *
     * @return a new list copy of the deque.
     */
    @Override
    public List<T> toList() {
        List<T> returnList = new ArrayList<>();
        for(int i = 0;i<size;i++){
            returnList.add(get(i));
        }
        return returnList;
    }

    /**
     * Remove and return the element at the front of the deque, if it exists.
     *
     * @return removed element, otherwise {@code null}.
     */
    @Override
    public T removeFirst() {
        if (size == 0){
            return null;
        }
        int firstIndex = plusOne(nextFirst);
        T temp = items[firstIndex];
        items[firstIndex] = null;//清除引用
        nextFirst = firstIndex;
        size--;
//        if( (size / items.length) < 0.25){//这个判断条件很严重,这样的话整型数据类型相处，会丢失小数位，必须要在比较前强转
        if(items.length >= 8 && (double) size / items.length <= 0.25){
            resizedown();//还是得有一个过程，因为你删除元素也是一次一次操作的，所以这个缩容的操作也不可能一次就操作完毕，所以使用if
        }
        return temp;
    }

    /**
     * Remove and return the element at the back of the deque, if it exists.
     *
     * @return removed element, otherwise {@code null}.
     */
    @Override
    public T removeLast() {
        if(size == 0){
            return null;
        }
        int lastIndex = minusOne(nextLast);
        T temp = items[lastIndex];
        items[lastIndex] = null;
        nextLast = lastIndex;
        size--;
        if(items.length >= 8 && (double) size / items.length <= 0.25){
            resizedown();//同removeFirst一样
        }
        return temp;
    }
}
