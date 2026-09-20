public class PrintIndexed {
   /**
     * Prints each character of a given string followed by the reverse of its index.
     * Example: printIndexed("hello") -> h4e3l2l1o0
     */
   public static void printIndexed(String s) {
      // TODO: Fill in this function
      int length = s.length();
      String newThing = "";
      //外循环控制里面的函数会循环几次，里面的函数每循环一次代表字符串的一个字符与其对应的序号
      for(int i = 1;i <= length;i++){
         newThing += (s.charAt(i-1) +"" + (length-i));
      }
      System.out.println(newThing);
   }

   public static void main(String[] args) {
      printIndexed("hello");
      printIndexed("cat"); // should print c2a1t0
   }
}
//Firstly,in java，if you  want to get a length of string,you can use the sth.length()  to get the length of sth

//Secondly,in  jave,you don't directly use sth[i] to get  the character in the index.
//if  you  want to get  the character,you can use sth.charAt(index)  to get it
//(Notice! The  sth  is a String object(Maybe it is  object,so you can use .method to operate the sth))
