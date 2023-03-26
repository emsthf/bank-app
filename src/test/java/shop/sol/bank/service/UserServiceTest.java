package shop.sol.bank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.sol.bank.config.dummy.DummyObject;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.user.UserReqDto.JoinReqDto;
import shop.sol.bank.dto.user.UserResDto.JoinResDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)  // 서비스를 테스트할 때. Spring 관련 Bean들이 하나도 없는 환경!!
class UserServiceTest extends DummyObject {

    @InjectMocks  // @Mock으로 생성한 가짜를 주입하는 곳
    private UserService userService;

    @Mock  // 가짜를 만들어서 InjectMocks에 주입
    private UserRepository userRepository;

    @Spy  // 진짜를 띄워서 InjectMocks에 주입
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void 회원가입() throws Exception {
        //given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("ssol");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("ssol@naver.com");
        joinReqDto.setFullname("솔");

        // stub
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
//        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        User ssol = newMockUser(1L, "ssol", "솔");
        when(userRepository.save(any())).thenReturn(ssol);

        // when
        JoinResDto joinResDto = userService.joinMember(joinReqDto);
        System.out.println("joinResDto = " + joinResDto);

        // then
        assertEquals(joinResDto.getId(), 1L);
        assertEquals(joinResDto.getUsername(), "ssol");
    }
}