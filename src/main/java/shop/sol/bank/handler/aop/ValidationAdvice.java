package shop.sol.bank.handler.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import shop.sol.bank.handler.ex.CustomValidationException;

import java.util.HashMap;
import java.util.Map;

@Aspect  // aop에 사용할 관점이라는 뜻
@Component
public class ValidationAdvice {
    // requestBody에 값이 들어오는 것은 POST와 PUT 뿐이므로 이 2개에 대해 포인트컷(AOP를 끼워넣을 지점)을 설정하자

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping() {
    }

    // 포인트컷에 적용시킬 관점
    @Around("postMapping() || putMapping()")  // joinPoint(AOP를 작동시킬 메서드)의 전후 제어 가능
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();  // joinPoint의 매개변수
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult bindingResult = (BindingResult) arg;

                if (bindingResult.hasErrors()) {
                    Map<String, String> errorMap = new HashMap<>();
                    for (FieldError error : bindingResult.getFieldErrors()) {
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    throw new CustomValidationException("유효성검사 실패", errorMap);
                }
            }
        }
        return proceedingJoinPoint.proceed();  // 정상적으로 해당 메서드를 실행.
    }
}
