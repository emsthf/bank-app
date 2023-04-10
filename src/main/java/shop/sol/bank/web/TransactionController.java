package shop.sol.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.sol.bank.config.auth.LoginUser;
import shop.sol.bank.dto.ResponseDto;
import shop.sol.bank.dto.transaction.TransactionResponseDto.TransactionListResponseDto;
import shop.sol.bank.service.TransactionService;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/s/accounts/{number}/transaction")
    public ResponseEntity<?> findTransactionList(@PathVariable Long number,
                                                 @RequestParam(value = "division", defaultValue = "ALL") String division,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @AuthenticationPrincipal LoginUser loginUser) {
        TransactionListResponseDto transactionListResponseDto = transactionService
                .findDepositAndWithdrawList(loginUser.getUser().getId(), number, division, page);
        return ResponseEntity.ok().body(new ResponseDto<>(1, "입출금목록보기 성공", transactionListResponseDto));
    }
}
