package shop.sol.bank.config.jwt;

import org.junit.jupiter.api.Test;
import shop.sol.bank.config.auth.LoginUser;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserEnum;

import static org.junit.jupiter.api.Assertions.*;

class JwtProcessTest {

    @Test
    void creat_user() {
        // given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);

        // when
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        // then
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
    }
    
    @Test
    void verify_user() {
        // given
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5rIiwicm9sZSI6IkNVU1RPTUVSIiwiaWQiOjEsImV4cCI6MTY4MDYwODEzM30.4OZorvvpDZQNm3uCfhprxO3kJnLPczo4P3wS2zOg2Q34xTktW_4UhBKwxQM6A_jtA8sxMb5jRjm0DqK4TQXE3g";
        
        // when
        LoginUser loginUser = JwtProcess.verify(jwtToken);
        System.out.println("loginUser.getUser().getRole() = " + loginUser.getUser().getId());
        System.out.println("loginUser.getUser().getRole() = " + loginUser.getUser().getRole());
        
        // then
        assertEquals(1L, (long) loginUser.getUser().getId());
        assertEquals(UserEnum.CUSTOMER, loginUser.getUser().getRole());
    }

}