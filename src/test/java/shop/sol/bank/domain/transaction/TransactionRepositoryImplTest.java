package shop.sol.bank.domain.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import shop.sol.bank.config.dummy.DummyObject;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.account.AccountRepository;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest  // DB 관련된 Bean이 다 올라간다.
class TransactionRepositoryImplTest extends DummyObject {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        autoIncrementReset();
        dateSetting();
        em.clear();  // 레포지토리 테스트에서 퍼시스턴스 컨텍스트 비우기는 필수!
    }

    @Test
    void dataJpa_test1() {
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction) -> {
            System.out.println("transaction.getId() = " + transaction.getId());
            System.out.println("transaction.getSender() = " + transaction.getSender());
            System.out.println("transaction.getReceiver() = " + transaction.getReceiver());
            System.out.println("transaction.getDivision() = " + transaction.getDivision());
            System.out.println("====================================");
        });
    }

    @Test
    void dataJpa_test2() {
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction) -> {
            System.out.println("transaction.getId() = " + transaction.getId());
            System.out.println("transaction.getSender() = " + transaction.getSender());
            System.out.println("transaction.getReceiver() = " + transaction.getReceiver());
            System.out.println("transaction.getDivision() = " + transaction.getDivision());
            System.out.println("====================================");
        });
    }

    @Test
    void findTransactionList_all_test() throws Exception {
        // given
        Long accountId = 1L;

        // when
        List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountId, "ALL", 0);
        for (Transaction transactionPS : transactionListPS) {
            System.out.println("getId() = " + transactionPS.getId());
            System.out.println("getAmount() = " + transactionPS.getAmount());
            System.out.println("getSender() = " + transactionPS.getSender());
            System.out.println("getReceiver() = " + transactionPS.getReceiver());
            System.out.println("getWithdrawAccountBalance() = " + transactionPS.getWithdrawAccountBalance());
            System.out.println("getDepositAccountBalance() = " + transactionPS.getDepositAccountBalance());
            System.out.println("출금 Account의 잔액 = " + transactionPS.getWithdrawAccount().getBalance());
            System.out.println("출금계좌 유저의 Fullname = " + transactionPS.getWithdrawAccount().getUser().getFullname());
            System.out.println("===================================");
        }

        // then
        assertEquals(800L, transactionListPS.get(3).getDepositAccountBalance());
    }

    private void dateSetting() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스,"));
        User love = userRepository.save(newUser("love", "러브"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
        Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        Account loveAccount = accountRepository.save(newAccount(3333L, love));
        Account ssarAccount2 = accountRepository.save(newAccount(4444L, ssar));

        Transaction withdrawTransaction1 = transactionRepository
                .save(newWithdrawTransaction(ssarAccount1, accountRepository));
        Transaction depositTransaction1 = transactionRepository
                .save(newDepositTransaction(cosAccount, accountRepository));
        Transaction transferTransaction1 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, cosAccount, accountRepository));
        Transaction transferTransaction2 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, loveAccount, accountRepository));
        Transaction transferTransaction3 = transactionRepository
                .save(newTransferTransaction(cosAccount, ssarAccount1, accountRepository));
    }

    private void autoIncrementReset() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE account_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE transaction_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}