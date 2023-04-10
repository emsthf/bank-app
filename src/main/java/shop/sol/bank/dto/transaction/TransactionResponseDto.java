package shop.sol.bank.dto.transaction;

import lombok.Data;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.transaction.Transaction;
import shop.sol.bank.util.CustomDateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionResponseDto {

    @Data
    public static class TransactionListResponseDto {
        private List<TransactionDto> transactions = new ArrayList<>();

        public TransactionListResponseDto(List<Transaction> transactions, Account account) {
            this.transactions = transactions.stream()
                    .map((transaction) -> new TransactionDto(transaction, account.getNumber()))
                    .collect(Collectors.toList());
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
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
                this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();

                // 입금 계좌만 있을 때(ATM에서 입금), 출금 계좌만 있을 때(ATM에서 출금)
                if (transaction.getDepositAccount() == null) {
                    this.balance = transaction.getWithdrawAccountBalance();
                } else if (transaction.getWithdrawAccount() == null) {
                    this.balance = transaction.getDepositAccountBalance();
                } else {
                    // 입/출금 계좌 값이 모두 있을 때 (파라미터로 받은 계좌에 따라 balance 설정)
                    if (accountNumber.longValue() == transaction.getDepositAccount().getNumber()) {
                        this.balance = transaction.getDepositAccountBalance();
                    } else {
                        this.balance = transaction.getWithdrawAccountBalance();
                    }
                }
            }
        }
    }
}
