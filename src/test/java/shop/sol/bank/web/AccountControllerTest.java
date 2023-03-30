package shop.sol.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import shop.sol.bank.config.dummy.DummyObject;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc  // MockMvc 객체를 자동 구성
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(newUser("ssol", "솔"));
    }

    // JWT token -> 인증필터 -> 시큐리티 세션생성
    // setupBefore=TEST_METHOD (setUp 메서드 실행 전에 수행)
    // setupBefore=TEST_EXECUTION (saveAccount_test 메서드 실행 전에 수행)
    @Test
    @WithUserDetails(value = "ssol", setupBefore = TestExecutionEvent.TEST_EXECUTION)  // DB에서 username=ssol로 조회해서 세션 담아주는 어노테이션
    void saveAccount_test() throws Exception {
        // given
        AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto();
        accountSaveRequestDto.setNumber(9999L);
        accountSaveRequestDto.setPassword(1234L);
        String requestBody = om.writeValueAsString(accountSaveRequestDto);
        System.out.println("requestBody = " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.debug("responseBody = " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(value = "ssol", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void findUserAccount() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/api/s/account/login-user"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.debug("responseBody = " + responseBody);

        // then
        resultActions.andExpect(status().isOk());
    }
}