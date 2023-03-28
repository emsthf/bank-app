package shop.sol.bank.dto.user;

import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserRequestDto {

    @Data
    public static class LoginRequestDto {
        // 이 Dto는 컨트롤러를 가기 전 필터에서 사용되므로 Validation 어노테이션 불가능
        private String username;
        private String password;
    }


    @Data
    public static class JoinRequestDto {
        // Validation은 컨트롤러에서 검사하는 것이다.
        // 영문, 숫자는 되고, 길이 최소 2~20자 이내
        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z0-9]{2,10}$", message = "영문/숫자 2~20자 이내로 작성해주세요")
        private String username;

        // 길이 4~20
        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;

        // 이메일 형식
        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z0-9]{2,10}+@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        // 영어, 한글, 1~20
        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z가-힣]{1,10}$", message = "한글/영문 1~10자 이내로 작성해주세요")
        private String fullname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
