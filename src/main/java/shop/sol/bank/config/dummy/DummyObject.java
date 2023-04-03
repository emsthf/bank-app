package shop.sol.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.transaction.Transaction;
import shop.sol.bank.domain.transaction.TransactionEnum;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserEnum;

import java.time.LocalDateTime;

public class DummyObject {

    protected User newUser(String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@gmail.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    protected User newMockUser(Long id, String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@gmail.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    protected Account newAccount(Long number, User user) {
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }

    protected Account newMockAccount(Long id, Long number, Long balance, User user) {
        return Account.builder()
                .id(id)
                .number(number)
                .password(1234L)
                .balance(balance)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 계좌 1111L 잔액 1000원
    // 입급 트랜잭션 -> 잔액 1100원 변경 -> 입급 트랜잭션 히스토리가 생성되어야 함
    protected Transaction newMockDepositTransaction(Long id, Account account) {
        account.deposit(100L);
        return Transaction.builder()
                .id(id)
                .withdrawAccount(null)
                .depositAccount(account)
                .withdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .amount(100L)
                .division(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber() + "")
                .tel("01088889999")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
