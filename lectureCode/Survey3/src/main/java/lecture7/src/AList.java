package lecture7.src;

public class AList<T> {
    private int size;
    private T[] items;// Formerly known as listArray

    @SuppressWarnings("unchecked")
    public AList(){
        items = (T[]) new Object[999];//但这会造成内存浪费啊
        size = 0;
    }

//    public void resize(T capacity){
//        // int [] resized = new int[size+1]
//        T [] resized = new T[capacity];
//        // Copy over the array items
////            resized = items;这个代码并不能起到一个将数组中的内容赋值到新的数组中的能力
//        //应该要使用循环遍历，将数组中每个下标所对应的数据都复制到新的数组中
//        for (int i = 0; i < size; i ++){
//            resized[i] = items[i];
//        }
//        // items = resized
//        items = resized;//最后再将新数组的指针复制给旧数组指针，起到一个扩容数组的效果，但是
//        //现在的问题是这样做会极大地增加CPU 和内存带宽？
//        //我的想法是当数组加到倒数第二个数字的时候，将最后一个下标位中存储一个指针，指向新的数组，起到一个扩容的作用
////            items[size] = x;
////            size ++;这一步不应该放在这个位置，该if循环是起到一个数组扩容的作用，与添加数字是解耦的
//    }
//    Add x to the end of our list
    public void addLast(T x){
        //YOUR CODE HERE
        //start putting stuff in moreItems!

        //when the array is too full...
//        if (size == items.length){
//            resize(size + 1);
//            //感受:伯克利就是伯克利，教授的水平就是很高，这个代码解耦，都是一遍出的，不报错
//            //说明其对java的特性掌握非常的深厚
//        }
        items[size] = x;
        size += 1;
    }
//    Retuen the item at idex i.
    public  T get(int i){
        return items[i];
    }

    public int size(){
        return size;//方法一定要有返回类型的，要么就是void，一定是不能为空，构造方法除外
    }

    public T removeLast(){
        size -= 1;
        return items[size];
    }
}
