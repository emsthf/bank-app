package shop.sol.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.transaction.Transaction;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.util.CustomDateUtil;

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

    @Data
    public static class AccountDepositResponseDto {
        private Long id;  // 계좌 id
        private Long number;  // 계좌번호
        private TransactionDto transaction;  // dto 안에 entity가 들어오면 안되므로

        public AccountDepositResponseDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction);  // entity를 받아서 dto로 변환
        }

        @Data
        public class TransactionDto {
            private Long id;
            private String division;
            private String sender;
            private String receiver;
            private Long amount;
            private String tel;
            private String createdAt;
            @JsonIgnore
            private Long depositAccountBalance;  // 클라이어느에게 전달 X. 서비스단에서 테스트 용도

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.division = transaction.getDivision().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }
}
