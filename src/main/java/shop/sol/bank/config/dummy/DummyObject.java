package shop.sol.bank.config.dummy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.account.AccountRepository;
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

    protected Transaction newDepositTransaction(Account account, AccountRepository accountRepository) {
        account.deposit(100L);  // 1000원이 있었다면 900원이 됨

        // Repository Test에서는 더티체킹 됨
        // Controller Test에서는 더티체킹 안됨
        // 어디선 되고 어디선 안되면 그냥 모든 곳에서 되도록 만들어라. 더티체킹을 신경쓰지말고 강제 save해서 내가 제어권을 가지면 됨.
        if (accountRepository != null) {
            accountRepository.save(account);
        }
        return Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(account)
                .withdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .amount(100L)
                .division(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber() + "")
                .tel("01022227777")
                .build();
    }

    protected Transaction newWithdrawTransaction(Account account, AccountRepository accountRepository) {
        account.withdraw(100L);  // 1000원이 있었다면 1100원이 됨

        if (accountRepository != null) {
            accountRepository.save(account);
        }
        return Transaction.builder()
                .withdrawAccount(account)
                .depositAccount(null)
                .withdrawAccountBalance(account.getBalance())
                .depositAccountBalance(null)
                .amount(100L)
                .division(TransactionEnum.WITHDRAW)
                .sender(account.getNumber() + "")
                .receiver("ATM")
                .build();
    }

    protected Transaction newTransferTransaction(Account withdrawAccount, Account depositAccount, AccountRepository accountRepository) {
        withdrawAccount.withdraw(100L);
        depositAccount.deposit(100L);

        if (accountRepository != null) {
            accountRepository.save(withdrawAccount);
            accountRepository.save(depositAccount);
        }
        return Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .withdrawAccountBalance(withdrawAccount.getBalance())
                .depositAccountBalance(depositAccount.getBalance())
                .amount(100L)
                .division(TransactionEnum.TRANSFER)
                .sender(withdrawAccount.getNumber() + "")
                .receiver(depositAccount.getNumber() + "")
                .build();
    }
}
