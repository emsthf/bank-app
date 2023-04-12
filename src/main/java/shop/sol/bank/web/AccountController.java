package shop.sol.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shop.sol.bank.config.auth.LoginUser;
import shop.sol.bank.dto.ResponseDto;
import shop.sol.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import shop.sol.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.sol.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;
import shop.sol.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
import shop.sol.bank.service.AccountService;

import javax.validation.Valid;

import static shop.sol.bank.dto.account.AccountResponseDto.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/s/accounts")
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveRequestDto accountSaveRequestDto,
                                         BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {  // 인증이 되서 필터를 통과해 컨트롤러로 들어오면 @AuthenticationPrincipal 어노테이션으로 UserDetails를 바로 꺼내올 수 있다.
        AccountSaveResponseDto accountSaveResponseDto = accountService.registerAccount(accountSaveRequestDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌등록 성공", accountSaveResponseDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/accounts/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser) {
        AccountListResponseDto accountListResponseDto = accountService.getAccountByUser(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "유저 계좌목록보기 성공", accountListResponseDto), HttpStatus.OK);
    }

    @DeleteMapping("/s/accounts/{accountNumber}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountNumber, @AuthenticationPrincipal LoginUser loginUser) {
        accountService.deleteAccount(accountNumber, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null), HttpStatus.OK);
    }

    @PostMapping("/accounts/deposit")
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositRequestDto accountDepositRequestDto, BindingResult bindingResult) {
        AccountDepositResponseDto accountDepositResponseDto = accountService.depositAccount(accountDepositRequestDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", accountDepositResponseDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/accounts/withdraw")
    public ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountWithdrawRequestDto accountWithdrawRequestDto,
                                             BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
        AccountWithdrawResponseDto accountWithdrawResponseDto = accountService.withdrawAccount(accountWithdrawRequestDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", accountWithdrawResponseDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/accounts/transfer")
    public ResponseEntity<?> transferAccount(@RequestBody @Valid AccountTransferRequestDto accountTransferRequestDto,
                                             BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
        AccountTransferResponseDto accountTransferResponseDto = accountService.transferAccount(accountTransferRequestDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 이체 완료", accountTransferResponseDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/accounts/{accountNumber}")
    public ResponseEntity<?> findDetailAccount(@PathVariable Long accountNumber,
                                               @RequestParam(value = "page", defaultValue = "0") int page,
                                               @AuthenticationPrincipal LoginUser loginUser) {
        AccountDetailResponseDto accountDetailResponseDto = accountService.findDetailAccount(accountNumber, loginUser.getUser().getId(), page);
        return ResponseEntity.ok(new ResponseDto<>(1, "계좌상세보기 성공", accountDetailResponseDto));
    }
}
