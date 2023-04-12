package shop.sol.bank.temp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LongTest {

    @Test
    void long_test() throws Exception {
        // given
        Long number1 = 1111L;
        Long number2 = 1111L;

        Long amount1 = 100L;
        Long amount2 = 1000L;

        // when
        if (number1.longValue() == number2.longValue()) {
            System.out.println("테스트 : 동일합니다");
        } else {
            System.out.println("테스트 : 동일하지 않습니다");
        }

        if (amount1 < amount2) {
            System.out.println("테스트 : amount1이 작습니다");
        } else {
            System.out.println("테스트 : amount1이 큽니다");
        }

        // then

    }

    @Test
    void long_test2() throws Exception {
        // given
        Long v1 = 1000L;
        Long v2 = 1000L;

        // when
        if (v1 == v2) {
            System.out.println("테스트 : 같습니다");
        }

        // then

    }

    @Test
    void long_test3() throws Exception {
        // given
        Long v1 = 1280L;
        Long v2 = 1280L;

        // when


        // then
        assertThat(v1).isEqualTo(v2);
        assertEquals(v2, v1);
    }
}
