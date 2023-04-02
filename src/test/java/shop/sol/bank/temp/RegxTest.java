package shop.sol.bank.temp;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegxTest {

    @Test
    void 한글만된다() throws Exception {
        String value = "가나다";
        boolean result = Pattern.matches("^[가-힣]+$", value);
        System.out.println("result = " + result);
        assertTrue(result);
    }

    @Test
    void 한글은안된다() throws Exception {
        String value = "가";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]*$", value);
        System.out.println("result = " + result);
        assertFalse(result);
    }

    @Test
    void 영어만된다() throws Exception {
        String value = "abc";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);
        System.out.println("result = " + result);
        assertTrue(result);
    }

    @Test
    void 영어는안된다() throws Exception {
        String value = "abc";
        boolean result = Pattern.matches("^[^a-zA-Z]*$", value);
        System.out.println("result = " + result);
        assertFalse(result);
    }

    @Test
    void 영어와숫자만된다() throws Exception {
        String value = "abc1234";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
        System.out.println("result = " + result);
        assertTrue(result);
    }

    @Test
    void 영어만되고_길이는_최소2_최대4이다() throws Exception {
        String value = "abcd";
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value);
        System.out.println("result = " + result);
        assertTrue(result);
    }

    @Test
    void user_username_regex() {
        // 영어, 숫자만 가능. 2~10자
        String username = "ssol12";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,10}$", username);
        System.out.println("result = " + result);
        assertTrue(result);
    }

    @Test
    void user_fullname_regex() {
        // 영어, 한글만 가능. 1~20자
        String fullname = "ssol박";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,10}$", fullname);
        System.out.println("result = " + result);
        assertTrue(result);
    }

    @Test
    void user_email_regex() {
        // 이메일 형식만 가능. 한글 불가능
        String email = "ssol@gmail.com";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,10}+@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", email);
        System.out.println("result = " + result);
        assertTrue(result);
    }

    @Test
    void account_division_regex() {
        String division = "DEPOSIT";
        boolean result = Pattern.matches("^(DEPOSIT)$", division);
        System.out.println("result = " + result);
        assertTrue(result);
    }

    @Test
    void tel_regex1() {
        String tel = "010-2222-3333";
        boolean result = Pattern.matches("^[0-9]{3}-[0-9]{4}-[0-9]{4}", tel);
        System.out.println("result = " + result);
        assertTrue(result);
    }

    @Test
    void tel_regex2() {
        String tel = "01022223333";
        boolean result = Pattern.matches("^[0-9]{11}", tel);
        System.out.println("result = " + result);
        assertTrue(result);
    }
}
