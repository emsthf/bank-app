package shop.sol.bank.dto.account;

import lombok.Data;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class AccountResponseDto {

    @Data
    public static class AccountSaveResponseDto {
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveResponseDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }

    @Data
    public static class AccountListResponseDto {
        private String fullname;
        private List<AccountDto> accounts;

        public AccountListResponseDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accounts = accounts.stream()
                    .map(AccountDto::new)
                    .collect(Collectors.toList());
        }

        @Data
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }
}
