package shop.sol.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.sol.bank.config.dummy.auth.LoginUser;
import shop.sol.bank.dto.user.UserResponseDto;
import shop.sol.bank.dto.user.UserResponseDto.LoginResponseDto;
import shop.sol.bank.util.CustomResponseUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static shop.sol.bank.dto.user.UserRequestDto.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
    }

    // POST : "/login"일 때 동작하는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            ObjectMapper om = new ObjectMapper();
            LoginRequestDto loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);

            // 강제 로그인
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequestDto.getUsername(), loginRequestDto.getPassword()
            );

            // UserDetailsService의 loadUserByUsername 호출
            // JWT를 쓴다 하더라도, 컨트롤러 진입을 하면 시큐리티의 권한체크, 인증체크의 도움을 받을 수 있게 세션을 만든다.
            // 이 세션의 유효기간은 request하고, response하면 끝!!
            return authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            // catch를 타면 SecurityConfig의 exceptionHandlering().authenticationEntryPoint()에 걸림
            // 이 부분은 필터의 과정이기 때문에 @ControllerAdvice로 보낼 수 없다!
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    // 위 메서드의 return authenticationManager.authenticate(authenticationToken);가 잘 작동되면 호출
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtVO.HEADER, jwtToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto(loginUser.getUser());
        CustomResponseUtil.success(response, loginResponseDto);
    }
}
