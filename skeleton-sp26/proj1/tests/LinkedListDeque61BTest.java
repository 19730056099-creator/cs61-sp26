import jh61b.utils.Reflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Deque;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

/** Performs some basic linked list tests. */
public class LinkedListDeque61BTest {

     @Test
     /** In this test, we have three different assert statements that verify that addFirst works correctly. */
     public void addFirstTestBasic() {
         Deque61B<String> lld1 = new LinkedListDeque61B<>();

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
         Deque61B<String> lld1 = new LinkedListDeque61B<>();

         lld1.addLast("front"); // after this call we expect: ["front"]
         lld1.addLast("middle"); // after this call we expect: ["front", "middle"]
         lld1.addLast("back"); // after this call we expect: ["front", "middle", "back"]
         assertThat(lld1.toList()).containsExactly("front", "middle", "back").inOrder();
     }

     @Test
     /** This test performs interspersed addFirst and addLast calls. */
     public void addFirstAndAddLastTest() {
         Deque61B<Integer> lld1 = new LinkedListDeque61B<>();

         /* I've decided to add in comments the state after each call for the convenience of the
            person reading this test. Some programmers might consider this excessively verbose. */
         lld1.addLast(0);   // [0]
         lld1.addLast(1);   // [0, 1]
         lld1.addFirst(-1); // [-1, 0, 1]
         lld1.addLast(2);   // [-1, 0, 1, 2]
         lld1.addFirst(-2); // [-2, -1, 0, 1, 2]

         assertThat(lld1.toList()).containsExactly(-2, -1, 0, 1, 2).inOrder();
     }

    // Below, you'll write your own tests for LinkedListDeque61B.


    @Test
    /** This test performs is RemoveFirst **/
    public void RemoveFirstTest(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
        assertThat(lld.removeFirst()).isEqualTo(null);
        lld.addFirst(10);
        lld.addLast(5);
        assertThat(lld.removeFirst()).isEqualTo(10);
        assertThat(lld.get(0)).isEqualTo(5);
    }

    @Test
    /** This test performs is RemoveLast **/
    public void RemoveLastTest(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
        assertThat(lld.removeLast()).isEqualTo(null);
        lld.addFirst(10);
        lld.addLast(5);
        assertThat(lld.removeLast()).isEqualTo(5);
        assertThat(lld.get(0)).isEqualTo(10);
    }
    /**
     * “remove_first_to_empty”: Add some elements to a deque and remove almost all of them.
     *                          Check that removing the last element with removeFirst works.
     * **/
    @Test
    public void addLastAfterRemoveToEmpty(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
        lld.addLast(10);
        lld.addLast(9);
        lld.addLast(8);
        lld.removeLast();
        lld.removeLast();
        lld.removeFirst();
        lld.addLast(6);
        assertThat(lld.get(0)).isEqualTo(6);
    }

    @Test
    /**
     *“remove_last_to_empty”: Add some elements to a deque and remove almost all of them.
     *                        Check that removing the last element with removeLast works.
     * **/
    public void addFirstAfterRemoveToEmpty(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
        lld.addFirst(10);
        lld.addFirst(9);
        lld.addFirst(8);
        lld.removeFirst();
        lld.removeLast();
        lld.removeLast();
        lld.addFirst(6);
        assertThat(lld.get(0)).isEqualTo(6);
    }

    //Flags for remove tests

    @Test
    /**
    *“remove_first_to_one”: Add some elements to a deque and remove almost all of them.
     *                      Check that removing the second to last element with removeFirst works.
         **/
    public void removeFirstToOne(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
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
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
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


    //Flags for get tests

    @Test
//    “get_first_empty”: Check that getFirst works.
//    “get_last_empty”: Check that getLast works.
//    “get_first_valid”: Check that getFirst works on a empty deque.
//    “get_last_valid”: Check that getLast works on a empty deque.
    /*** This tset performs interspersed getFirst and getLast calls */
    public void getFirstAndLastTest(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
        //要获取，肯定要区分为 Deque61B 为空 和不为空的情况
        //先是为空的时候
        assertThat(lld.getFirst()).isEqualTo(null);
        assertThat(lld.getLast()).isEqualTo(null);
        //不为空的时候
        //这个是在claude的提醒下添加的，当只有一个元素的时候，第一个和最后一个是同一个节点的时候，这是很容易出现bug的时候
        lld.addFirst(10);
        assertThat(lld.getFirst()).isEqualTo(10);
        assertThat(lld.getLast()).isEqualTo(10);
        //添加完这个只有一个元素的情况后，测试依旧通过

        lld.addLast(5);
        assertThat(lld.getFirst()).isEqualTo(10);
        assertThat(lld.getLast()).isEqualTo(5);
    }

    @Test
//    “get_valid”: Check that get works on a valid index.
//    “get_oob_large”: Check that get works on a large, out of bounds index.
//    “get_oob_neg”: Check that get works on a negative index.

    /** This test performs is get **/
    public void getTest(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
        lld.addFirst(10);

        assertThat(lld.get(28723)).isEqualTo(null);
        assertThat(lld.get(-1)).isEqualTo(null);
        assertThat(lld.get(0)).isEqualTo(10);

    }
    @Test
//    “get_recursive_valid”: Check that getRecursive works on a valid index.
//    “get_recursive_oob_large”: Check that getRecursive works on a large, out of bounds index.
//    “get_recursive_oob_neg”: Check that getRecursive works on a negative index.
    /** This test performs is getRecursive **/
    //操蛋了，理解错了，这个是递归取得而不是逆序取得
    public void getRecursiveTest(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
        lld.addFirst(10);
        lld.addLast(5);

        assertThat(lld.getRecursive(28723)).isEqualTo(null);
        assertThat(lld.getRecursive(-1)).isEqualTo(null);
        assertThat(lld.getRecursive(1)).isEqualTo(5);
        System.out.println(lld.getRecursive(0));
    }

    //Flags for size tests

    @Test
//    “size”: Check that size works.
    /** This test performs is size() **/
    public void sizeTest(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
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
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
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
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
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

    //Flags for isEmpty tests

    //按照先写测试，再实现方法的原则(驱动开发),我先开发了测试
    //测试写完之后，发现测试不通过，我还以为是代码写错了
    //再一想，isEmpty方法都没有实现，测试怎么可能成功呢
    @Test
    /** This test performs is Empty **/
    public void isEmptyTest(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();
        //刚创建: 空
        assertThat(lld.isEmpty()).isTrue();

        //创建后加入元素: 非空
        lld.addFirst(10);
        assertThat(lld.isEmpty()).isFalse();
    }


    //Flags for toList tests

    @Test
//    “to_list_empty”: Check that toList works with empty LinkedListDeque61B.
//    “to_list_nonempty”: Check that toList works with non-empty LinkedListDeque61B.
    public void toListEmptyAndToListNonempty(){
        Deque61B<Integer> lld = new LinkedListDeque61B<>();

        assertThat(lld.toList()).containsExactly().inOrder();
        lld.addFirst(10);
        lld.addLast(9);
        lld.addLast(8);
        lld.addLast(7);
        lld.addLast(6);
        assertThat(lld.toList()).containsExactly(10,9,8,7,6).inOrder();
    }
}