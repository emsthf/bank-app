package shop.sol.bank.dto.account;

import lombok.Data;
import shop.sol.bank.domain.account.Account;

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
}
