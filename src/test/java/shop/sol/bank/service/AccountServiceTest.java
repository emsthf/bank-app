package shop.sol.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shop.sol.bank.config.dummy.DummyObject;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.account.AccountRepository;
import shop.sol.bank.domain.transaction.Transaction;
import shop.sol.bank.domain.transaction.TransactionRepository;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.sol.bank.handler.ex.CustomApiException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.sol.bank.dto.account.AccountRequestDto.*;
import static shop.sol.bank.dto.account.AccountResponseDto.*;
import static shop.sol.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;

@ExtendWith(MockitoExtension.class)  // 전체를 메모리에 띄울 필요 없이 Mockito 환경만 메모리에 띄움
class AccountServiceTest extends DummyObject {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @InjectMocks  // 모든 Mock들이 InjectMocks로 주입됨
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Spy  // 진짜 객체를 InjectMocks에 주입한다.
    private ObjectMapper om;

    @Test
    void 계좌등록_test() throws Exception {
        // given
        Long userId = 1L;

        AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto();
        accountSaveRequestDto.setNumber(1111L);
        accountSaveRequestDto.setPassword(1234L);

        // stub 1 (준비 단계)
        User ssol = newMockUser(userId, "ssol", "솔");
        when(userRepository.findById(any())).thenReturn(Optional.of(ssol));

        //stub 2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        // stub 3
        Account ssolAccount = newMockAccount(1L, 1111L, 1000L, ssol);
        when(accountRepository.save(any())).thenReturn(ssolAccount);

        // when
        AccountSaveResponseDto accountSaveResponseDto = accountService.registerAccount(accountSaveRequestDto, userId);
        String responseBody = om.writeValueAsString(accountSaveResponseDto);
        log.debug("responseBody = " + responseBody);

        // then
        assertEquals(1111L, accountSaveResponseDto.getNumber());
    }

    @Test
    void deleteAccount_test() throws Exception {
        // given
        Long number = 1111L;
        Long userId = 2L;

        // stub (삭제에 필요한 가짜 유저와 계좌 생성. findByNumber()는 JPA에서 만들어주는 query method이기 때문에 테스트 할 필요 없다.)
        User ssol = newMockUser(1L, "ssol", "솔");
        Account ssolAccount = newMockAccount(1L, 1111L, 1000L, ssol);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssolAccount));

        // when


        // then (Service의 1.계좌 확인과 3.계좌 삭제는 Repository의 역할이므로 검증을 할 필요가 없다. 그러니 2.계좌 소유자 확인 과정만 테스트 하면 됨)
        assertThrows(CustomApiException.class, () -> accountService.deleteAccount(number, userId));  // deleteAccount()의 결과 CustomApiException 예외가 발생 될 것을 기대
    }

    // 확인할 부분: Account -> balance 변경됬는지, Transaction -> balance 잘 기록됬는지
    @Test
    void depositAccount_test() throws Exception {
        // given
        AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
        accountDepositRequestDto.setNumber(1111L);
        accountDepositRequestDto.setAmount(100L);
        accountDepositRequestDto.setDivision("DEPOSIT");
        accountDepositRequestDto.setTel("01044448888");

        // stub 1
        User ssol1 = newMockUser(1L, "ssol", "솔");  // 실행됨
        Account ssolAccount1 = newMockAccount(1L, 1111L, 1000L, ssol1);  // 실행됨. ssolAccount1 -> 1000원
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssolAccount1));  // 실행안됨. service 호출 후 실행됨 -> 1100원

        // stub 2 (스텁이 진행될 때마다 연관된 객체는 새로 만들어서 주입하기 - 타이밍 때문에 꼬인다. stub끼리 완전 독립적으로 만들어야 한다.)
        User ssol2 = newMockUser(1L, "ssol", "솔");  // stub 2에 유저가 필요하다면 새로 만들어야 함
        Account ssolAccount2 = newMockAccount(1L, 1111L, 1000L, ssol2);  // stub 2에 Account가 필요하다면 새로 만들어야 함
        Transaction transaction = newMockDepositTransaction(1L, ssolAccount2);
        when(transactionRepository.save(any())).thenReturn(transaction);

        // when
        AccountDepositResponseDto accountDepositResponseDto = accountService.depositAccount(accountDepositRequestDto);
        log.debug("트랜잭션 입급계좌 잔액 = " + accountDepositResponseDto.getTransaction().getDepositAccountBalance());
        log.debug("계좌쪽 잔액 = " + ssolAccount1.getBalance());

        // then
        assertThat(ssolAccount1.getBalance()).isEqualTo(1100L);  // ssolAccount1이 deposit이 되는지 테스트
        assertThat(accountDepositResponseDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);
    }

    @Test
    void depositAccount_test2() throws Exception {
        // given
        AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
        accountDepositRequestDto.setNumber(1111L);
        accountDepositRequestDto.setAmount(100L);
        accountDepositRequestDto.setDivision("DEPOSIT");
        accountDepositRequestDto.setTel("01044448888");

        // stub 1
        User ssol1 = newMockUser(1L, "ssol", "솔");
        Account ssolAccount1 = newMockAccount(1L, 1111L, 1000L, ssol1);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssolAccount1));

        // stub 2
        User ssol2 = newMockUser(1L, "ssol", "솔");
        Account ssolAccount2 = newMockAccount(1L, 1111L, 1000L, ssol2);
        Transaction transaction = newMockDepositTransaction(1L, ssolAccount2);
        when(transactionRepository.save(any())).thenReturn(transaction);
        
        // when
        AccountDepositResponseDto accountDepositResponseDto = accountService.depositAccount(accountDepositRequestDto);
        String responseBody = om.writeValueAsString(accountDepositResponseDto);
        log.debug("테스트 : " + responseBody);

        // then
        assertThat(ssolAccount1.getBalance()).isEqualTo(1100L);
    }

    // 서비스를 테스트 하고 싶으면, 내가 지금 무엇을 여기서 테스트해야할지 명확히 구분해야 한다.(책임 분리)
    // DTO 생성 테스트는 컨트롤러 테스트에서도 가능하니까 컨트롤러 테스트로 넘기고, DB 관련은 서비스 것이 아니기에 굳이 여기서 볼 필요 없을 수도 있다.
    // 물론 서비스에 연관관계에 따라서 내가 체크해야할 수 많은 로직들이 있다면 stub을 하나씩 만들면서 정석대로 해야하겠지만 상황에 따라 더 간편하게 서비스를 테스트 할 수도 있다.
    @Test
    void depositAccount_test3() throws Exception {
        // given
        Account account = newMockAccount(1L, 1111L, 1000L, null);
        Long amount = 100L;

        // when
        if (amount <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }
        account.deposit(100L);

        // then
        assertThat(account.getBalance()).isEqualTo(1100L);
    }

    // Todo 계좌 출금 테스트
    @Test
    void withdrawAccount_test() throws Exception {
        // given
        Long amount = 100L;
        Long password = 1234L;
        Long userId = 1L;

        User ssol = newMockUser(1L, "ssol", "솔");
        Account ssolAccount = newMockAccount(1L, 1111L, 1000L, ssol);

        // when
        if (amount <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다");
        }
        ssolAccount.checkOwner(userId);
        ssolAccount.checkSamePassword(password);
//        ssolAccount.checkBalance(amount);
        ssolAccount.withdraw(amount);

        // then
        assertThat(ssolAccount.getBalance()).isEqualTo(900L);
    }

    // Todo 계좌 이체 테스트
    @Test
    void transferAccount_test() throws Exception {
        // given
        Long userId = 1L;
        AccountTransferRequestDto accountTransferRequestDto = new AccountTransferRequestDto();
        accountTransferRequestDto.setWithdrawNumber(1111L);
        accountTransferRequestDto.setDepositNumber(2222L);
        accountTransferRequestDto.setWithdrawPassword(1234L);
        accountTransferRequestDto.setAmount(100L);
        accountTransferRequestDto.setDivision("TRANSFER");

        User ssol = newMockUser(1L, "ssol", "솔");
        User kim = newMockUser(2L, "kim", "김");
        Account withdrawAccount = newMockAccount(1L, 1111L, 1000L, ssol);
        Account depositAccount = newMockAccount(2L, 2222L, 1000L, kim);

        // when
        if (accountTransferRequestDto.getWithdrawNumber().longValue() == accountTransferRequestDto.getDepositNumber().longValue()) {
            throw new CustomApiException("입출금계조가 동이로할 수 없습니다");
        }

        if (accountTransferRequestDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다");
        }
        withdrawAccount.checkOwner(userId);
        withdrawAccount.checkSamePassword(accountTransferRequestDto.getWithdrawPassword());
        withdrawAccount.checkBalance(accountTransferRequestDto.getAmount());

        withdrawAccount.withdraw(accountTransferRequestDto.getAmount());
        depositAccount.deposit(accountTransferRequestDto.getAmount());

        // then
        assertThat(withdrawAccount.getBalance()).isEqualTo(900L);
        assertThat(depositAccount.getBalance()).isEqualTo(1100L);
    }

    // Todo 유저별 계좌 목록보기 테스트

    // Todo 계좌 상세보기 테스트
}