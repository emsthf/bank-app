package shop.sol.bank.domain.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum TransactionEnum {
    WITHDRAW("출금"),
    DEPOSIT("입금"),
    TRANSFER("이체"),
    ALL("입출금내역");

    private String value;
}
