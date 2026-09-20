public class StarTriangle5 {
   /**
     * Prints a right-aligned triangle of stars ('*') with 5 lines.
     * The first row contains 1 star, the second 2 stars, and so on. 
     */
   public static void starTriangle5() {
      // TODO: Fill in this function
      //以下这个代码是做上三角型，但目标要使用的右上三角型
//      int j ;
//      for (int  i = 0;i < 5; i++){
//         for (j = 0; j <= i; j ++){
//            System.out.print("*");
//         }
//         System.out.println();
//      }
      //代码逻辑：
      //最初的情况是：代码逻辑能够想得通，但是代码实现上有问题
      //私以为这是代码功底不熟练的表现，这个应该是能够靠勤学苦练代码来实现的
      //经过查阅gemini后，思路矫正，应该使用for循环嵌套
      //外层for循环控制行数
      //内层for循环控制空格数和*号数
      int totalraw =  5;
      for(int i =1; i <= totalraw; i++){
         for(int space = 0;space < totalraw - i; space++){
            System.out.print(" ");
         }
         for(int star = 0; star < i; star ++){
            System.out.print("*");
         }
         System.out.println();
      }

   }
//         5-i i
//       * 4   1 i+j = 5
//      ** 3   2
//     *** 2   3
//    **** 1   4
//   ***** 0   5
   public static void main(String[] args) {
      starTriangle5();
   }
}