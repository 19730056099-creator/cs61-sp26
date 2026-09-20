public class StarTriangleN {
   /**
     * Prints a right-aligned triangle of stars ('*') with N lines.
     * The first row contains 1 star, the second 2 stars, and so on. 
     */
   public static void starTriangle(int N) {
      // TODO: Fill in this function
      for(int i = 1;i <=  N;i++){
         for (int space  = 0; space < N -i;space ++){
            System.out.print(" ");
         }
         for (int star = 0;star < i; star++){
            System.out.print("*");
         }
         System.out.println();
      }

   }
   
   public static void main(String[] args) {
      starTriangle(7);
   }
}