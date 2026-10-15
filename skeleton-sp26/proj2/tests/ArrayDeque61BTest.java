import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ArrayDeque61BTest {

//    Flags for add tests

    @Test
    /** In this test, we have three different assert statements that verify that addFirst works correctly. */
    public void addFirstTestBasic() {
        Deque61B<String> lld1 = new ArrayDeque61B<>();

        lld1.addFirst("back"); // after this call we expect: ["back"]
        assertThat(lld1.toList()).containsExactly("back").inOrder();

        lld1.addFirst("middle"); // after this call we expect: ["middle", "back"]
        assertThat(lld1.toList()).containsExactly("middle", "back").inOrder();

        lld1.addFirst("front"); // after this call we expect: ["front", "middle", "back"]
        assertThat(lld1.toList()).containsExactly("front", "middle", "back").inOrder();

         /* Note: The first two assertThat statements aren't really necessary. For example, it's hard
            to imagine a bug in your code that would lead to ["front"] and ["front", "middle"] failing,
            but not ["front", "middle", "back"].
          */
    }

    @Test
    /** In this test, we use only one assertThat statement. IMO this test is just as good as addFirstTestBasic.
     *  In other words, the tedious work of adding the extra assertThat statements isn't worth it. */
    public void addLastTestBasic() {
        Deque61B<String> lld1 = new ArrayDeque61B<>();

        lld1.addLast("front"); // after this call we expect: ["front"]
        lld1.addLast("middle"); // after this call we expect: ["front", "middle"]
        lld1.addLast("back"); // after this call we expect: ["front", "middle", "back"]
        assertThat(lld1.toList()).containsExactly("front", "middle", "back").inOrder();
    }

    @Test
    /** This test performs interspersed addFirst and addLast calls. */
    public void addFirstAndAddLastTest() {
        Deque61B<Integer> lld1 = new ArrayDeque61B<>();

         /* I've decided to add in comments the state after each call for the convenience of the
            person reading this test. Some programmers might consider this excessively verbose. */
        lld1.addLast(0);   // [0]
        lld1.addLast(1);   // [0, 1]
        lld1.addFirst(-1); // [-1, 0, 1]
        lld1.addLast(2);   // [-1, 0, 1, 2]
        lld1.addFirst(-2); // [-2, -1, 0, 1, 2]

        assertThat(lld1.toList()).containsExactly(-2, -1, 0, 1, 2).inOrder();
    }

    @Test
    /**
    add_first_trigger_resize: Check that addFirst works when called on a full underlying array
    **/
    public void addFirstTriggerResize(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);
        lld.addFirst(9);
        lld.addFirst(8);
        lld.addFirst(7);
        lld.addFirst(6);
        lld.addFirst(5);
        lld.addFirst(4);
        lld.addFirst(3);
        //因为我们知道初始的时候，其实数组是8个位置的
        //所以如果再加入的话，就会触发扩容机制
        assertThat(lld.size()).isEqualTo(8);
        lld.addFirst(2);
        assertThat(lld.size()).isEqualTo(9);
        assertThat(lld.get(8)).isEqualTo(10);
    }

    @Test
    /**
    add_last_trigger_resize: Check that addLast works when called on a full underlying array
     **/
    public void addLastTriggerResize(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addLast(10);
        lld.addLast(9);
        lld.addLast(8);
        lld.addLast(7);
        lld.addLast(6);
        lld.addLast(5);
        lld.addLast(4);
        lld.addLast(3);
        //因为我们知道初始的时候，其实数组是8个位置的
        //所以如果再加入的话，就会触发扩容机制
        assertThat(lld.size()).isEqualTo(8);
        lld.addLast(2);
        assertThat(lld.size()).isEqualTo(9);
        assertThat(lld.get(8)).isEqualTo(2);
    }

    //Flags for add after remove tests

    /**
     * “remove_first_to_empty”: Add some elements to a deque and remove almost all of them.
     *                          Check that removing the last element with removeFirst works.
     * **/
    @Test
    public void addLastAfterRemoveToEmpty(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addLast(10);
        lld.addLast(9);
        lld.addLast(8);
        lld.removeLast();
        lld.removeLast();
        lld.removeFirst();
        assertThat(lld.toList()).isEmpty();
    }

    @Test
    /**
     *“remove_last_to_empty”: Add some elements to a deque and remove almost all of them.
     *                        Check that removing the last element with removeLast works.
     * **/
    public void addFirstAfterRemoveToEmpty(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);
        lld.addFirst(9);
        lld.addFirst(8);
        lld.removeFirst();
        lld.removeLast();
        lld.removeLast();
        assertThat(lld.toList()).isEmpty();
    }

    //Flags for remove Tests

    @Test
    /** remove_first: Check that removeFirst works **/
    public void RemoveFirstTest(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        assertThat(lld.removeFirst()).isEqualTo(null);
        lld.addFirst(10);
        lld.addLast(5);
        assertThat(lld.removeFirst()).isEqualTo(10);
        assertThat(lld.get(0)).isEqualTo(5);
    }

    @Test
    /** remove_last: Check that removeLast works. **/
    public void RemoveLastTest(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        assertThat(lld.removeLast()).isEqualTo(null);
        lld.addFirst(10);
        lld.addLast(5);
        assertThat(lld.removeLast()).isEqualTo(5);
        assertThat(lld.get(0)).isEqualTo(10);
    }

    @Test
    /**
    remove_first_to_empty: Add some elements to a deque and remove almost all of them. Check that removing the last element with removeFirst works.
     **/
    public void removeFirstToEmpty(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);
        lld.addLast(9);
        lld.addLast(8);
        lld.addLast(7);
        assertThat(lld.size()).isEqualTo(4);
        lld.removeFirst();
        lld.removeFirst();
        lld.removeFirst();
        assertThat(lld.toList()).containsExactly(7);
        assertThat(lld.removeFirst()).isEqualTo(7);
        assertThat(lld.toList()).isEmpty();
        assertThat(lld.size()).isEqualTo(0);
    }

    @Test
    /**
    remove_last_to_empty: Add some elements to a deque and remove almost all of them.
                        Check that removing the last element with removeLast works.
     **/
    public void removeLastToEmpty(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);
        lld.addLast(9);
        lld.addLast(8);
        lld.addLast(7);
        assertThat(lld.size()).isEqualTo(4);
        lld.removeLast();
        lld.removeLast();
        lld.removeLast();
        assertThat(lld.toList()).containsExactly(10);
        assertThat(lld.removeLast()).isEqualTo(10);
        assertThat(lld.toList()).isEmpty();
        assertThat(lld.size()).isEqualTo(0);
    }

    @Test
    /**
     *“remove_first_to_one”: Add some elements to a deque and remove almost all of them.
     *                      Check that removing the second to last element with removeFirst works.
     **/
    public void removeFirstToOne(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);
        lld.addLast(9);
        lld.addLast(8);
        lld.addLast(7);
        assertThat(lld.toList()).containsExactly(10,9,8,7).inOrder();
        lld.removeFirst();
        lld.removeLast();
        assertThat(lld.toList()).containsExactly(9,8).inOrder();
        assertThat(lld.removeFirst()).isEqualTo(9);
        assertThat(lld.get(0)).isEqualTo(8);
    }

    @Test
    /**
     “remove_last_to_one”: Add some elements to a deque and remove almost all of them.
     Check that removing the second to last element with removeLast works.
     **/
    public void removeLastToOne(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);
        lld.addLast(9);
        lld.addLast(8);
        lld.addLast(7);
        assertThat(lld.toList()).containsExactly(10,9,8,7).inOrder();
        lld.removeFirst();
        lld.removeLast();
        assertThat(lld.toList()).containsExactly(9,8).inOrder();
        lld.removeLast();
        assertThat(lld.removeLast()).isEqualTo(9);
    }

    /** a test for the removeFirst And removeLast **/
    @Test
    public void removeFirstAndRemoveLastTest(){
        Deque61B<Integer> test = new ArrayDeque61B<>();
        test.addFirst(5);
        test.addLast(6);
        assertThat(test.toList()).containsExactly(5,6).inOrder();
        assertThat(test.removeFirst()).isEqualTo(5);
        assertThat(test.removeLast()).isEqualTo(6);
    }

    /**
    remove_first_trigger_resize: Called when usage factor is <= 25% and array size > 8.
                                Checks that the array resizes appropriately.
     **/
    @Test
    public void removeFirstTriggerResizeTest() {
        ArrayDeque61B<Integer> test = new ArrayDeque61B<>();
        for (int i = 0; i < 64; i++) {
            test.addLast(i);
        }
        assertThat(test.length()).isEqualTo(64);
        for (int i = 0; i < 60; i++) {
            test.removeFirst();
        }
        assertThat(test.toList()).containsExactly(60, 61, 62, 63).inOrder();
    }

    /**
    remove_last_trigger_resize: Called when usage factor is <= 25% and array size > 8.
                                Checks that the array resizes appropriately.
     **/
    @Test
    public void removeLastTriggerResizeTest() {
        ArrayDeque61B<Integer> test = new ArrayDeque61B<>();
        for (int i = 0; i < 64; i++) {
            test.addLast(i);
        }
        assertThat(test.length()).isEqualTo(64);
        for (int i = 0; i < 60; i++) {
            test.removeLast();
        }
        assertThat(test.toList()).containsExactly().inOrder();
    }

    //Flags for get tests


//    “get_oob_large”: Check that get works on a large, out of bounds index.
//    “get_oob_neg”: Check that get works on a negative index.

    @Test
    /**
     * get_first_empty: Check that getFirst works on an empty ArrayDeque.
     */
    public void getFirstEmpty(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        assertThat(lld.getFirst()).isEqualTo(null);
    }

    @Test
    /**
     * get_first_valid: Check that getFirst works on a nonempty ArrayDeque.
     **/
    public void getFirstValid(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(5);
        assertThat(lld.getFirst()).isEqualTo(5);
    }

    @Test
    /**
     * get_last_empty: Check that getLast works on an empty ArrayDeque.
     */
    public void getLastEmpty(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        assertThat(lld.getLast()).isEqualTo(null);
    }

    @Test
    /**
     * get_last_valid: Check that getLast works on a nonempty ArrayDeque.
     **/
    public void getLastValid(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addLast(5);
        assertThat(lld.getLast()).isEqualTo(5);
    }

    @Test
    /**
     * “get_valid”: Check that get works on a valid index.
     * **/
    public void getValid() {
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);

        assertThat(lld.get(28723)).isEqualTo(null);
        assertThat(lld.get(-1)).isEqualTo(null);
        assertThat(lld.get(0)).isEqualTo(10);
    }

    @Test
    /**
     * get_oob_large: Check that get works on a large, out of bounds index.
     * */
    public void getOobLarge(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);

        assertThat(lld.get(8)).isEqualTo(null);
    }

    @Test
    /**
     * get_oob_neg: Check that get works on a negative index.
     * */
    public void getOobNeg(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);

        assertThat(lld.get(-1)).isEqualTo(null);
    }


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

    //Flags for size tests

    @Test
//    “size”: Check that size works.
    /** This test performs is size() **/
    public void sizeTest(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        //刚创建的时候，双端队列为空，没有元素，所以size应该为0
        assertThat(lld.size()).isEqualTo(0);

        //创建后，加入元素
        lld.addFirst(3);
        assertThat(lld.size()).isEqualTo(1);

        lld.addLast(3);
        assertThat(lld.size()).isEqualTo(2);

        lld.addLast(5);
        assertThat(lld.size()).isEqualTo(3);
    }

    @Test
//    “size_after_remove_to_empty”: Add some elements to a deque and remove them all,
//                                  then check that size still works.
    public void sizeAfterRemoveToEmpty(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        //刚创建的时候，双端队列为空，没有元素，所以size应该为0
        assertThat(lld.size()).isEqualTo(0);

        //创建后，加入元素后,检查size
        lld.addFirst(3);
        lld.addLast(3);
        lld.addLast(5);
        assertThat(lld.size()).isEqualTo(3);
        assertThat(lld.toList()).containsExactly(3,3,5).inOrder();

        //全部删除时候再检查这个size
        lld.removeFirst();
        lld.removeLast();
        lld.removeLast();
        assertThat(lld.toList()).containsExactly();//当列表为空的时候，containsExactly()中不需要填写null,
        // 为空的话默认就是null了,如果加入null的话，该方法会认为需要包含"null"字符串
        assertThat(lld.size()).isEqualTo(0);
    }

    @Test
//    “size_after_remove_from_empty”: Remove from an empty deque, then check that size still works.
    //该场景检测的是，对空列表使用remove，这个size不能变成负数
    public void sizeAfterRemoveFromEmpty(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        assertThat(lld.removeFirst()).isEqualTo(null);
        assertThat(lld.removeLast()).isEqualTo(null);
        System.out.println(lld.size());
        assertThat(lld.size()).isEqualTo(0);
        //使用断言表达式
//        isAtLeast(x)：大于等于 x
//        isAtMost(x)：小于等于 x
//        isGreaterThan(x)：大于 x
//        isLessThan(x)：小于 x
        //但是这个场景中不适合用这个断言方法，因为如果bug导致此时这个size变为1或5的话
        //isEqualTo(0)依旧能把这个错误捕获出来
    }

//    Flags for isEmpty Tests

    @Test
    /**
     * is_empty_true: Check that size works on an empty deque.
     * */
    public void isEmptyTrue(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        assertThat(lld.size()).isEqualTo(0);
    }

    @Test
    /**
     * is_empty_false: Check that size works on a non- empty deque.
     * */
    public void isEmptyFalse(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();
        lld.addFirst(10);
        assertThat(lld.size()).isEqualTo(1);
    }

    @Test
    /** a test for the isEmpty and size methods **/
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

    //Flags for toList tests

//    to_list_empty: Check that toList works with empty ArrayDeque.
//    to_list_nonempty: Check that toList works with non-empty ArrayDeque.

    /** a test for the tolist method **/
    @Test
    public void toListTest(){
        Deque61B<Integer> test = new ArrayDeque61B<>();
        //为空
        assertThat(test.toList()).isEmpty();
        assertThat(test.get(0)).isNull();
        //当队列不为空的时候
        test.addFirst(9);
        test.addLast(8);
        test.addLast(7);
        test.addLast(6);
        test.addLast(5);
        test.addLast(4);
        assertThat(test.toList()).containsExactly(9,8,7,6,5,4).inOrder();
    }




    @Test
//    “to_list_empty”: Check that toList works with empty LinkedListDeque61B.
//    “to_list_nonempty”: Check that toList works with non-empty LinkedListDeque61B.
    public void toListEmptyAndToListNonempty(){
        Deque61B<Integer> lld = new ArrayDeque61B<>();

        assertThat(lld.toList()).containsExactly().inOrder();
        lld.addFirst(10);
        lld.addLast(9);
        lld.addLast(8);
        lld.addLast(7);
        lld.addLast(6);
        assertThat(lld.toList()).containsExactly(10,9,8,7,6).inOrder();
    }

//    Flags for advanced resize tests
    @Test
    /**
     * resize_up_and_resize_down: Trigger a resize up and then a resize down in the same test.
     */
    public void resizeUpAndResizeDown(){
        ArrayDeque61B<Integer> lld = new ArrayDeque61B<>();
        for (int i = 0;i < 64;i++){
            lld.addLast(i);
        }
        assertThat(lld.length()).isEqualTo(64);
        for (int i = 0;i < 60;i++){
            lld.removeLast();
        }
        assertThat(lld.length()).isEqualTo(8);
    }

    @Test
    public void addLastTestBasicWithoutToList() {
        Deque61B<String> ad = new ArrayDeque61B<>();

        ad.addLast("front"); // after this call we expect: ["front"]
        ad.addLast("middle"); // after this call we expect: ["front", "middle"]
        ad.addLast("back"); // after this call we expect: ["front", "middle", "back"]
        assertThat(ad).containsExactly("front", "middle", "back");
    }

    @Test
    public void interatorTest() {
        Deque61B<String> test = new ArrayDeque61B<>();
        test.addLast("front");
        test.addLast("middle");
        test.addLast("back");
        for (String s : test) {
            System.out.println(s);
        }
        //为什么for-each用法会和迭代器产生关系
        /**
         * 因为for-each本身不会"遍历",它只是一种简写（语法糖），编译器会把它翻译成迭代器的代码
         * 编译器做的翻译
         *
         * 你写的：
         *
         * java
         * for (String s : ad) {
         *     System.out.println(s);
         * }
         *
         * 编译器实际生成的：
         *
         * java
         * Iterator<String> it = ad.iterator();
         * while (it.hasNext()) {
         *     String s = it.next();
         *     System.out.println(s);
         * }
         *
         * 所以for-each要能工作，test必须有iterator()方法,也就是必须实现Iterable.没有的话编译器就翻译不下去，
         *                    于是报错"foreach not applicable to type"
         * **/
    }

    @Test
    public void testEqualDeques61B(){
        Deque61B<String> ad = new ArrayDeque61B<>();
        Deque61B<String> ad2 = new ArrayDeque61B<>();

        ad.addLast("front");
        ad.addLast("middle");
        ad.addLast("back");

        ad2.addLast("front");
        ad2.addLast("middle");
        ad2.addLast("back");

        assertThat(ad.equals(ad2)).isTrue();
    }

    @Test
    public void toStringDeque61BTest(){
        Deque61B<String> test = new ArrayDeque61B<>();
        test.addFirst("front");
        test.addLast("middle");
        test.addLast("back");
        System.out.println(test.toString());
    }
}
