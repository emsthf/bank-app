package shop.sol.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sol.bank.config.dummy.DummyObject;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.account.AccountRepository;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.sol.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;

@ExtendWith(MockitoExtension.class)  // 전체를 메모리에 띄울 필요 없이 Mockito 환경만 메모리에 띄움
class AccountServiceTest extends DummyObject {

    @InjectMocks  // 모든 Mock들이 InjectMocks로 주입됨
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

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
        System.out.println("responseBody = " + responseBody);

        // then
        assertEquals(1111L, accountSaveResponseDto.getNumber());
    }
}