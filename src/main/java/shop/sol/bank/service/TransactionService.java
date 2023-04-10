package shop.sol.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.account.AccountRepository;
import shop.sol.bank.domain.transaction.Transaction;
import shop.sol.bank.domain.transaction.TransactionRepository;
import shop.sol.bank.dto.transaction.TransactionResponseDto.TransactionListResponseDto;
import shop.sol.bank.handler.ex.CustomApiException;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionListResponseDto findDepositAndWithdrawList(Long userId, Long accountNumber, String division, int page) {
        Account accountPS = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        accountPS.checkOwner(userId);

        List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountPS.getId(), division, page);
        return new TransactionListResponseDto(transactionListPS, accountPS);
    }
}
