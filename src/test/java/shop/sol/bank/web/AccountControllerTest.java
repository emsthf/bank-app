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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shop.sol.bank.config.dummy.DummyObject;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.account.AccountRepository;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.sol.bank.handler.ex.CustomApiException;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:db/teardown.sql")
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

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        User ssol = userRepository.save(newUser("ssol", "솔"));
        User kim = userRepository.save(newUser("kim", "김"));
        Account ssolAccount1 = accountRepository.save(newAccount(1111L, ssol));
        Account kimAccount1 = accountRepository.save(newAccount(2222L, kim));
        em.clear();  // Persistence Context를 비워줌
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

    /*
     * 테스트시에는 insert한 것이 모두 Persistence Context에 올라감 (영속화)
     * 영속화 된 것을 초기화 해주는 것이 실제 실행 모드와 동일한 환경으로 테스트를 하는 것!
     * 최초 select는 쿼리가 발생하지만 Persistence Context에 있으면 1차 캐시를 함\
     * Lazy 로딩은 쿼리도 발생안함 - Persistence Context에 있다면!
     * Lazy 로딩할 때 Persistence Context에 없다면 쿼리가 발생함
     */
    @Test
    @WithUserDetails(value = "ssol", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteAccount_test() throws Exception {
        // given
        Long number = 1111L;

        // when
        ResultActions resultActions = mvc.perform(delete("/api/s/account/" + number));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.debug("responseBody = " + responseBody);

        // then
        // JUnit 테스트에서 delete 쿼리는 DB관련(DML)으로 가장 마지막에 실행되면 발동안함
        assertThrows(CustomApiException.class, () -> accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다")
        ));
    }
}