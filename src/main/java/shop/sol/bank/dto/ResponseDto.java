package shop.sol.bank.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseDto<T> {
    // 응답 Dto는 한번 만들면 수정할 일이 없기 때문에 final

    private final Integer code;  // 1 성공, -1 실패
    private final String msg;
    private final T data;
}
