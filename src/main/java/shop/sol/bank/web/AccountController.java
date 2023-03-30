package shop.sol.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shop.sol.bank.config.auth.LoginUser;
import shop.sol.bank.dto.ResponseDto;
import shop.sol.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.sol.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import shop.sol.bank.service.AccountService;

import javax.validation.Valid;

import static shop.sol.bank.service.AccountService.AccountListResponseDto;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveRequestDto accountSaveRequestDto,
                                         BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {  // 인증이 되서 필터를 통과해 컨트롤러로 들어오면 @AuthenticationPrincipal 어노테이션으로 UserDetails를 바로 꺼내올 수 있다.
        AccountSaveResponseDto accountSaveResponseDto = accountService.registerAccount(accountSaveRequestDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌등록 성공", accountSaveResponseDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser) {
        AccountListResponseDto accountListResponseDto = accountService.getAccountByUser(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "유저 계좌목록보기 성공", accountListResponseDto), HttpStatus.OK);
    }
}
