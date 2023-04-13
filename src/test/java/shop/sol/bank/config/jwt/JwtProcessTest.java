package shop.sol.bank.config.jwt;

import org.junit.jupiter.api.Test;
import shop.sol.bank.config.auth.LoginUser;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserEnum;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProcessTest {

    private String createToken() {
        // given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.ADMIN)
                .build();
        LoginUser loginUser = new LoginUser(user);

        // when
        return JwtProcess.create(loginUser);
    }

    @Test
    void creat_user() {
        // given

        // when
        String jwtToken = createToken();
        System.out.println("jwtToken = " + jwtToken);

        // then
        assertThat(jwtToken).startsWith(JwtVO.TOKEN_PREFIX);
    }
    
    @Test
    void verify_user() {
        // given
        String token = createToken();
        String jwtToken = token.replace(JwtVO.TOKEN_PREFIX, "");
        
        // when
        LoginUser loginUser = JwtProcess.verify(jwtToken);
        System.out.println("loginUser.getUser().getRole() = " + loginUser.getUser().getId());
        System.out.println("loginUser.getUser().getRole() = " + loginUser.getUser().getRole());
        
        // then
        assertThat(loginUser.getUser().getId()).isEqualTo(1L);
        assertThat(loginUser.getUser().getRole()).isEqualTo(UserEnum.ADMIN);
    }

}