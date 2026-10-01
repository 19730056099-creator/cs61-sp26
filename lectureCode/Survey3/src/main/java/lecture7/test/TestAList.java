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
}
