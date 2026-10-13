package lec9_inheritance1;

public class WordUtils {
    //定义一个公开的静态方法 longest,接收一个存字符串的链表,返回其中的一个字符串(很可能是最长的那个)。
    public static String longest(List61B<String> list) {
        int maxDex = 0;
        for (int i = 0; i < list.size(); i += 1) {
            String longestString = list.get(maxDex);
            String thisString = list.get(i);
            if (thisString.length() > longestString.length()) {
                maxDex = i;
            }
        }
        return list.get(maxDex);
    }

   public static void main(String[] args) {
     AList<String> someList = new AList<>();
     someList.addLast("elk");
     someList.addLast("are");
     someList.addLast("watching");
     System.out.println(longest(someList));

     //someList.print();
  }
}

