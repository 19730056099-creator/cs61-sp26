package lecture7.test;
import lecture7.src.SLList;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class TestSLList {
    @Test
    //这个是第一次自己编写的测试类
    public void testSLList(){
        SLList<Integer> myList = new SLList<>();
        //后面的<>叫钻石运算符(diamond operator),它的作用是告诉编译器:"这里的类型参数,请你根据左边自动推断。"

        myList.addFirst(1);
        myList.addFirst(2);

        assertThat(myList.get(0)).isEqualTo(2);//alt + enter可以快速导入这个第三方的库
        //assertThat在测试里面是"断言"方法，意思是:"我断言这个值应该满足某个条件"
        //注意
        //库和包的区别
        //包是"整理代码的文件夹",库是"别人写好、打包好的一套代码"。

        //更换另一版本的jdk后，记得重新编译下项目
        //还有，如果项目默认版本为25的话，更好完之后，还得更改一下project中source中的language level

        //在java中有两种不同data type
        Integer x = 5;// a wrapper type for an int that makes an int look like an object and behave like an object
        // in java, if you want to use a generic type,it has to be an object,so that's why we have to use this capital I Integer wrapper class
        // in java

        //为什么基本类型就不能为null?
        //因为基本类型变量里直接存的是"值",而不是"指向值的地址"。 null 的意思是"没有指向任何对象",基本类型根本没有"指向"这回事,所以不存在 null。

        //但其实在java中Integer和Int之间是很好转换的
        int y = x;//java会自动帮你转换好的，也就是隐式转换
//        int y = 5;
        System.out.println(y);

    }
}
