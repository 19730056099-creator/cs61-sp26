package lecture7.test;

import lecture7.src.AList;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class TestAList {
    @Test
    public void testAList(){
        AList a = new AList();
        System.out.println(a.size());
        assertThat(a.size()).isEqualTo(0);

        a.addLast(4);
        a.addLast(10);
        assertThat(a.get(0)).isEqualTo(4);
        assertThat(a.get(1)).isEqualTo(10);

        a.removeLast();
        assertThat(a.get(1)).isEqualTo(0);
    }

    @Test
    public void test2000AList(){
        AList list = new AList();
        for (int i = 0;i < 2000; i++){
            list.addLast(i);
        }

        for (int i = 0; i < 2000; i++){
            assertThat(list.get(i)).isEqualTo(i);
        }
    }


    @Test
    public void AListTest(){
        AList<String> list = new AList<>();
        list.addLast("apple");
        list.addLast("banana");
        assertThat(list.get(0)).isEqualTo("apple");
        assertThat(list.get(1)).isEqualTo("banana");
//     items = new T[999];
    // 报错：java: 创建泛型数组
    // 原因：Java 不允许 new T[999]。
    // Java 泛型采用类型擦除：编译后 T 被替换成 Object，运行时 JVM 不知道 T 是什么。
    // 而数组创建时必须知道确切的元素类型（数组在运行时检查类型），所以不能创建 T[]。
    //
    // 普通泛型使用没问题：类型安全在编译期检查，编译器会在取值处自动插入强转，运行时只看到 Object。
    //
    // C++ 不会出现这个问题：模板是编译期实例化，会为每种类型（int、string 等）
    // 各生成一份代码，T 在每份代码里都是确定的类型。
    //
    // 解决办法：(T[]) new Object[999]，并用 @SuppressWarnings("unchecked") 抑制警告。
    // 这是由 Java（类型擦除）和 C++（模板实例化）的设计差异导致的。
    }
}
