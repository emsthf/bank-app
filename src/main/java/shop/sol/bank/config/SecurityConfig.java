package shop.sol.bank.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.sol.bank.config.jwt.JwtAuthenticationFilter;
import shop.sol.bank.domain.user.UserEnum;
import shop.sol.bank.util.CustomResponseUtil;

@Configuration
public class SecurityConfig {

    // Slf4j 어노테이션을 사용해도 되지만 어노테이션 방식으로 하면 Junit을 사용할 때 문제가 생겨서 우선 이렇게 사용하자.
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean  // Ioc 컨테이너에 BCryptPasswordEncoder() 객체가 등록
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그: BCryptPasswordEncoder 빈 등록");
        return new BCryptPasswordEncoder();
    }

    // JWT 필터 등록
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            // AuthenticationManager에 접근해서 강제 세션 로그인
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            super.configure(builder);
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그: filterChain 빈 등록됨");
        http.headers().frameOptions().disable()  // iframe 허용 안함
                .and()
                .csrf().disable()  // enable이면 postman 작동 안함
                .cors().configurationSource(configurationSource())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()  // 브라우저 팝업창을 이용해서 사용자 인증 비활성
                .apply(new CustomSecurityFilterManager())  // 필터 적용
                .and()
                .exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
                    CustomResponseUtil.unAuthentication(response, "로그인을 진행해 주세요");
                })
                .and()
                .authorizeRequests()
                .antMatchers("/api/s/**").authenticated()
                .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN)  // 최근 공식문서 상에는 ROLE_ 프리픽스 안붙여도 됨
                .anyRequest().permitAll();
        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        log.debug("디버그: configurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");  // 모든 헤더 허용
        configuration.addAllowedMethod("*");  // 모든 http request Method 허용(=Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*");  // 모든 IP 주소 허용(=프론트 IP만 허용)
        configuration.setAllowCredentials(true);  // 클라이언트에서 쿠키 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 주소 요청에 configuration에서 설정한 옵션을 넣어준다.
        return source;
    }
}
