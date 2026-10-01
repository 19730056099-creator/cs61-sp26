import java.util.Random;

public class IntListMystery {

    /**
     *  Builds an IntList containing some mystery numbers.
     *  You don't have to understand how all this code works.
     */
    public static void mystery() {
        Random r = new Random(829);
        IntList L = null;

        int idx = 0;
        int nextInt;
        while (idx < 1000) {
            nextInt = r.nextInt(0, 800);
            L = new IntList(nextInt, L);
            idx += 1;
        }
    }

    /**
     *  Returns the first five numbers in the mystery IntList.
     */
    public static int[] firstFiveNumbers() {
        // TODO: Replace the 0s with the numbers you found during debugging.
        return new int[]{241, 326, 30, 140, 21};
    }

    /**
     *  Returns the 500th number added to the mystery IntList.
     */
    public static int middleNumber() {
        // TODO: Replace the 0 with the number you found during debugging.
        return 491;
    }

    public static void main(String[] args) {
        mystery();//不能直接在这边设置条件断点，idx这个是 mystery 这个方法里面的局部变量，在main方法中根本不存在，所以会报错
        //报错信息：Cannot find local variable idex
        //solution: 在mystery中设置断点，然后再次点击main方法的debug按钮，可是为什么只有main方法才能debug，mystery方法不能debug?
        //reason:因为调试器是运行一个程序，而程序的入口只有main,mystery本身是没有人去启动它的，它是被main里面的mystery();调用的。
        //      所以说，断点可以打在任何方法里面，但程序必须从main启动。你不需要，也没办法单独启动mystery().
    }
}
