package shop.sol.bank.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.account.AccountRepository;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.sol.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import shop.sol.bank.handler.ex.CustomApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AccountSaveResponseDto registerAccount(AccountSaveRequestDto accountSaveRequestDto, Long userId) {
        // User가 DB에 있는지 체크
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다"));

        // 해당 계좌가 DB에 있는지 중복여부 체크
        Optional<Account> accountOP = accountRepository.findByNumber(accountSaveRequestDto.getNumber());
        if (accountOP.isPresent()) {
            throw new CustomApiException("해당 계좌가 이미 존재합니다");
        }

        // 계좌 등록
        Account accountPS = accountRepository.save(accountSaveRequestDto.toEntity(userPS));

        // Dto를 응답
        return new AccountSaveResponseDto(accountPS);
    }

    public AccountListResponseDto getAccountByUser(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다"));

        // 유저의 모든 계좌목록
        List<Account> accountListPS = accountRepository.findByUser_id(userId);
        return new AccountListResponseDto(userPS, accountListPS);
    }

    @Data
    public static class AccountListResponseDto {
        private String fullname;
        private List<AccountDto> accounts = new ArrayList<>();

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
}
