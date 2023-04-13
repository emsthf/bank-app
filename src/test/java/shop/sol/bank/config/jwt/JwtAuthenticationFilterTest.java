package shop.sol.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.sol.bank.config.dummy.DummyObject;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.user.UserRequestDto.LoginRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Transactional  // 각 테스트가 끝났을 때 롤백시킴 (BeforeEach로 각 테스트를 실행 전에 유저 등록하면 같은유저라 테스트 통과X. @Order(1)를 사용해서 순서를 지정해줄 순 있는데 각 테스트는 독립적이어야 하므로 사용을 권장하지 않는다.)
// Transactional으로 롤백만 시키면 내가 예상하는 PK가 아닌게 들어갈 수 있다.
@Sql("classpath:db/teardown.sql")  // SpringBootTest 하는 곳은 전부 teardown.sql을 붙여주자. 실행시점 : BeforeEach 실행 직전 마다!
@ActiveProfiles("test")  // 해당 프로파일로 동작함
@AutoConfigureMockMvc  // mockito 환경에 mockMvc가 new 됨
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)  // 가짜 환경으로 스프링의 컴포넌트들을 스캔해서 올림
class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User ssol = userRepository.save(newUser("ssol", "솔"));
    }

    @Test
    void successfulAuthentication_test() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("ssol");
        loginRequestDto.setPassword("1234");
        String requestBody = om.writeValueAsString(loginRequestDto);
        System.out.println("requestBody = " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("responseBody = " + responseBody);
        System.out.println("jwtToken = " + jwtToken);

        // then
        resultActions.andExpect(status().isOk());
        assertThat(jwtToken).isNotNull();
        assertThat(jwtToken).startsWith(JwtVO.TOKEN_PREFIX);
        resultActions.andExpect(jsonPath("$.data.username").value("ssol"));
    }

    @Test
    void unsuccessfulAuthentication_test() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("");
        loginRequestDto.setPassword("1234555");
        String requestBody = om.writeValueAsString(loginRequestDto);
        System.out.println("requestBody = " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("responseBody = " + responseBody);
        System.out.println("jwtToken = " + jwtToken);

        // then
        resultActions.andExpect(status().isUnauthorized());
    }
}