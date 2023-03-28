package shop.sol.bank.dto.user;

import lombok.Data;
import lombok.ToString;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.util.CustomDateUtil;

public class UserResponseDto {

    @Data
    public static class LoginResponseDto {
        private Long id;
        private String username;
        private String createdAt;

        public LoginResponseDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.createdAt = CustomDateUtil.toStringFormat(user.getCreatedAt());
        }
    }

    @Data
    @ToString
    public static class JoinResponseDto {
        private Long id;
        private String username;
        private String fullname;

        public JoinResponseDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}
