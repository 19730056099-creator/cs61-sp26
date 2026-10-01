package lecture7.src;

public class AList {
    private int[] items;// Formerly known as listArray
    private int size;

    public AList(){
        items = new int[999];//但这会造成内存浪费啊
        size = 0;
    }

//    Add x to the end of our list
    public void addLast(int x){
        items[size] = x;
        size += 1;
    }
//    Retuen the item at idex i.
    public  int get(int i){
        return items[i];
    }

    public void removeLast(){
        int i = 0;
        while(items[i] != 0){
            i++;
        }
        items[i -1] = 0;
    }
    public int size(){
        return size;//方法一定要有返回类型的，要么就是void，一定是不能为空，构造方法除外
    }
}
