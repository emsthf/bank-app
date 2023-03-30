package shop.sol.bank.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sol.bank.dto.ResponseDto;
import shop.sol.bank.dto.user.UserRequestDto;
import shop.sol.bank.dto.user.UserResponseDto.JoinResponseDto;
import shop.sol.bank.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequestDto.JoinRequestDto joinRequestDto,
                                  BindingResult bindingResult) {
        // valid를 통과하지 못하면 bindingResult에 모든 오류가 담긴다.

        JoinResponseDto joinResponseDto = userService.joinMember(joinRequestDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "회원가입 성공", joinResponseDto), HttpStatus.CREATED);
    }
}
