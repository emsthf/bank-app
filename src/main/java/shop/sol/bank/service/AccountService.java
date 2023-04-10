package shop.sol.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.account.AccountRepository;
import shop.sol.bank.domain.transaction.Transaction;
import shop.sol.bank.domain.transaction.TransactionEnum;
import shop.sol.bank.domain.transaction.TransactionRepository;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import shop.sol.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.sol.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;
import shop.sol.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
import shop.sol.bank.dto.account.AccountResponseDto.*;
import shop.sol.bank.handler.ex.CustomApiException;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

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

    @Transactional
    public void deleteAccount(Long number, Long userId) {
        // 1. 계좌 확인
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다."));

        // 2. 계좌 소유자 확인
        accountPS.checkOwner(userId);

        // 3. 계좌 삭제
        accountRepository.deleteById(accountPS.getId());
    }

    // 입금시에는 인증이 필요 없다.
    @Transactional
    public AccountDepositResponseDto depositAccount(AccountDepositRequestDto accountDepositRequestDto) {  // ATM -> 누군가의 계좌
        // 입금 금액 0원 체크
        if (accountDepositRequestDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }

        // 입금계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountDepositRequestDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 입금(해당 계좌 balance 조정 - update문. 더티체킹)
        depositAccountPS.deposit(accountDepositRequestDto.getAmount());

        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(depositAccountPS)
                .withdrawAccountBalance(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountDepositRequestDto.getAmount())
                .division(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(accountDepositRequestDto.getNumber() + "")
                .tel(accountDepositRequestDto.getTel())
                .build();
        Transaction transactionPS = transactionRepository.save(transaction);

        return new AccountDepositResponseDto(depositAccountPS, transactionPS);
    }

    @Transactional
    public AccountWithdrawResponseDto withdrawAccount(AccountWithdrawRequestDto accountWithdrawRequestDto, Long userId) {
        // 0원 체크
        if (accountWithdrawRequestDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다");
        }

        // 출금계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawRequestDto.getNumber())
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 출금 소유자 확인 (로그인한 사람과 동일한지)
        withdrawAccountPS.checkOwner(userId);

        // 출금계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountWithdrawRequestDto.getPassword());

        // 출금계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawRequestDto.getAmount());

        // 출금하기
        withdrawAccountPS.withdraw(accountWithdrawRequestDto.getAmount());

        // 거래내역 남기기 (내 계좌에서 ATM으로 출금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(null)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawRequestDto.getAmount())
                .division(TransactionEnum.WITHDRAW)
                .sender(accountWithdrawRequestDto.getNumber() + "")
                .receiver("ATM")
                .build();
        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO 응답
        return new AccountWithdrawResponseDto(withdrawAccountPS, transactionPS);
    }

    public AccountTransferResponseDto transferAccount(AccountTransferRequestDto accountTransferRequestDto, Long userId) {
        // 출금계좌와 입금계좌가 동일하면 안됨
        if (accountTransferRequestDto.getWithdrawNumber().longValue() == accountTransferRequestDto.getDepositNumber().longValue()) {
            throw new CustomApiException("입출금계조가 동이로할 수 없습니다");
        }

        // 0원 체크
        if (accountTransferRequestDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다");
        }

        // 출금계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountTransferRequestDto.getWithdrawNumber())
                .orElseThrow(() -> new CustomApiException("출금계좌를 찾을 수 없습니다"));

        // 입금계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountTransferRequestDto.getDepositNumber())
                .orElseThrow(() -> new CustomApiException("입금계좌를 찾을 수 없습니다"));

        // 출금계좌 소유자 확인 (로그인한 사람과 동일한지)
        withdrawAccountPS.checkOwner(userId);

        // 출금계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountTransferRequestDto.getWithdrawPassword());

        // 출금계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountTransferRequestDto.getAmount());

        // 이체하기
        withdrawAccountPS.withdraw(accountTransferRequestDto.getAmount());
        depositAccountPS.deposit(accountTransferRequestDto.getAmount());

        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(depositAccountPS)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountTransferRequestDto.getAmount())
                .division(TransactionEnum.TRANSFER)
                .sender(accountTransferRequestDto.getWithdrawNumber() + "")
                .receiver(accountTransferRequestDto.getDepositNumber() + "")
                .build();
        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO 응답
        return new AccountTransferResponseDto(withdrawAccountPS, transactionPS);
    }
}
