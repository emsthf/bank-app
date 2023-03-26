package shop.sol.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.sol.bank.config.dummy.DummyObject;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.user.UserReqDto.JoinReqDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        dataSetting();
    }

    @Test
    @Order(1)
    void join_success_test() throws Exception {
        // given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("park");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("park@gmail.com");
        joinReqDto.setFullname("박박박");

        String requestBody = om.writeValueAsString(joinReqDto);
//        System.out.println("requestBody = " + requestBody);

        // when
        ResultActions resultActions = mvc.perform(post("/api/join")
                .content(requestBody).contentType(MediaType.APPLICATION_JSON));  // 바디 값이 있으면 바디 값의 타입을 꼭 설명해줘야 한다.
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("responseBody = " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void join_fail_test() throws Exception {
        // given
        // 중복 이름으로 회원가입
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("kim");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("kim@gmail.com");
        joinReqDto.setFullname("김김김");

        String requestBody = om.writeValueAsString(joinReqDto);
//        System.out.println("requestBody = " + requestBody);

        // when
        ResultActions resultActions = mvc.perform(post("/api/join")
                .content(requestBody).contentType(MediaType.APPLICATION_JSON));  // 바디 값이 있으면 바디 값의 타입을 꼭 설명해줘야 한다.
//        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
//        System.out.println("responseBody = " + responseBody);

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private void dataSetting() {
        userRepository.save(newUser("kim", "김김김"));
    }
}