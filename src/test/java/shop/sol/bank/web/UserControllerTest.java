package shop.sol.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.sol.bank.config.dummy.DummyObject;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.user.UserRequestDto.JoinRequestDto;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:db/teardown.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc  // MockMvc 객체를 자동 구성. 추가 코드 작성 없이 25~26 라인의 MockMvc 객체를 사용할 수 있다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        dataSetting();
    }

    @Test
    void join_success_test() throws Exception {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("park");
        joinRequestDto.setPassword("1234");
        joinRequestDto.setEmail("park@gmail.com");
        joinRequestDto.setFullname("박박박");

        String requestBody = om.writeValueAsString(joinRequestDto);
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
    void join_fail_test() throws Exception {
        // given
        // 중복 이름으로 회원가입
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername("ssol");
        joinRequestDto.setPassword("1234");
        joinRequestDto.setEmail("ssol@gmail.com");
        joinRequestDto.setFullname("솔");

        String requestBody = om.writeValueAsString(joinRequestDto);
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
        userRepository.save(newUser("ssol", "솔"));
        em.clear();
    }
}