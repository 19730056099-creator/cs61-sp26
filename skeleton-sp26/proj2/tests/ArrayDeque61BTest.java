import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.google.common.truth.Truth.assertThat;

public class ArrayDeque61BTest {
    /** a test for the getFirst and getLast methods **/
    //    public int getFirstAndGetLastTest()其实这个返回类型可以为空，不需要为int类型的，因为检测都在断言方法中，
    //    实际这个测试方法可以没有返回值，如果你要执意添加int类型的话，可以在方法末加上return 1;表示方法结束，并象征性返回一个数字满足方法对返回类型的期望
    @Test
    public void getFirstAndGetLastTest(){
        //方法名后面一定要有括号，第一次编写这个方法时候，忘记添加()导致这个方法一直失效
        Deque61B<Integer> test = new ArrayDeque61B<Integer>();
        //当数组队列为空时，应该返回null
//        assertThat(test.getFirst()).isEqualTo(null);
//        assertThat(test.getLast()).isEqualTo(null);
//        检查是否为空的时候，Truth有专门的写法isNull
        assertThat(test.getFirst()).isNull();
        assertThat(test.getLast()).isNull();
        test.addFirst(7);
        test.addLast(8);
        assertThat(test.getFirst()).isEqualTo(7);
        assertThat(test.getLast()).isEqualTo(8);
    }

    /** a test for the getFirst and getLast methods **/
    @Test
    public void getAndGetRecursiveTest(){
        Deque61B<Integer> test = new ArrayDeque61B<Integer>();
        assertThat(test.get(-1)).isNull();
        assertThat(test.getRecursive(-1)).isNull();
        assertThat(test.get(9)).isNull();
        assertThat(test.getRecursive(9)).isNull();
        assertThat(test.get(0)).isNull();
        assertThat(test.getRecursive(0)).isNull();
        test.addFirst(6);
        assertThat(test.get(0)).isEqualTo(6);
        assertThat(test.getRecursive(0)).isEqualTo(6);
        //第一遍的时候错误，因为get方法就没有写好。没有站在使用者的角度考虑
        assertThat(test.get(-1)).isNull();
        assertThat(test.getRecursive(-1)).isNull();
        assertThat(test.get(9)).isNull();
        assertThat(test.getRecursive(9)).isNull();
    }

    /** a test for the isEmpty and size methods **/
    @Test
    public void isEmptyAndSizeTest(){
        Deque61B<Integer> test = new ArrayDeque61B<>();
        //队列为空
        assertThat(test.isEmpty()).isTrue();
        assertThat(test.size()).isEqualTo(0);
        //队列非空
        test.addFirst(6);
        test.addLast(7);
        test.addLast(8);
        assertThat(test.isEmpty()).isFalse();
        assertThat(test.size()).isEqualTo(3);
    }
}
