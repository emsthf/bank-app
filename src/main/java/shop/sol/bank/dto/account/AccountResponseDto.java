package shop.sol.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.transaction.Transaction;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.util.CustomDateUtil;

import java.util.ArrayList;
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

    // 입금은 인증이 안되어 있어서 잔액 확인 불가
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

    // DTO가 depoist과 똑같아도 재사용하지 않기 (나중에 만약에 출금할 때 조금 DTO가 달라져야 하면 DTO를 공유하고 있을 때 수정 잘못하면 망해... - 독립적으로 만들어야 함)
    @Data
    public static class AccountWithdrawResponseDto {
        private Long id;  // 계좌 id
        private Long number;  // 계좌번호
        private Long balance;  // 잔액
        private TransactionDto transaction;  // dto 안에 entity가 들어오면 안되므로

        public AccountWithdrawResponseDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);  // entity를 받아서 dto로 변환
        }

        @Data
        public class TransactionDto {
            private Long id;
            private String division;
            private String sender;
            private String receiver;
            private Long amount;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.division = transaction.getDivision().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Data
    public static class AccountTransferResponseDto {
        private Long id;  // 계좌 id
        private Long number;  // 계좌번호
        private Long balance;  // 잔액
        private TransactionDto transaction;  // dto 안에 entity가 들어오면 안되므로

        public AccountTransferResponseDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);  // entity를 받아서 dto로 변환
        }

        @Data
        public class TransactionDto {
            private Long id;
            private String division;
            private String sender;
            private String receiver;
            private Long amount;
            @JsonIgnore
            private Long depositAccountBalance;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.division = transaction.getDivision().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Data
    public static class AccountDetailResponseDto {
        private Long id;
        private Long number;
        private Long balance;
        private List<TransactionDto> transactions = new ArrayList<>();

        public AccountDetailResponseDto(Account account, List<Transaction> transactions) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transactions = transactions.stream()
                    .map((transaction) -> new TransactionDto(transaction, account.getNumber())).collect(Collectors.toList());
        }

        @Data
        public class TransactionDto {
            private Long id;
            private String division;
            private Long amount;
            private String sender;
            private String receiver;
            private String tel;
            private String createdAt;
            private Long balance;

            public TransactionDto(Transaction transaction, Long accountNumber) {
                this.id = transaction.getId();
                this.division = transaction.getDivision().getValue();
                this.amount = transaction.getAmount();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());

                if (transaction.getDepositAccount() == null) {
                    this.balance = transaction.getWithdrawAccountBalance();
                } else if (transaction.getWithdrawAccount() == null) {
                    this.balance = transaction.getDepositAccountBalance();
                } else {
                    if (accountNumber.longValue() == transaction.getDepositAccount().getNumber().longValue()) {
                        this.balance = transaction.getDepositAccountBalance();
                    } else {
                        this.balance = transaction.getWithdrawAccountBalance();
                    }
                }
            }
        }
    }
}
