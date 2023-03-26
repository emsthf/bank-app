package shop.sol.bank.dto.user;

import lombok.Data;
import lombok.ToString;
import shop.sol.bank.domain.user.User;

public class UserResDto {

    @Data
    @ToString
    public static class JoinResDto {
        private Long id;
        private String username;
        private String fullname;

        public JoinResDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}
