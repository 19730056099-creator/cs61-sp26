public class DoubleUp {
   /**
     * Returns a new string where each character of the given string is repeated twice.
     * Example: doubleUp("hello") -> "hheelllloo"
     */
   public static String doubleUp(String s) {
      // TODO: Fill in this function
      int length = s.length();
      String newString = "";
      for (int i = 0; i < length;i++) {
         newString += s.charAt(i) +""+ s.charAt(i);
      }
      return newString;
   }
   
   public static void main(String[] args) {
      String s = doubleUp("hello");
      System.out.println(s);//hheelllloo
      
      System.out.println(doubleUp("cat"));//ccaatt
   }
}