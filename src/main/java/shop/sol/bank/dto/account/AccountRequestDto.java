package shop.sol.bank.dto.account;

import lombok.Data;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.user.User;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

public class AccountRequestDto {

    @Data
    public static class AccountSaveRequestDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        public Account toEntity(User user) {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
    }
}
