package shop.sol.bank.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;
import shop.sol.bank.dto.user.UserReqDto;
import shop.sol.bank.dto.user.UserResDto.JoinResDto;
import shop.sol.bank.handler.ex.CustomApiException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 서비스는 Dto를 요청받고, Dto를 응답한다.
    @Transactional
    public JoinResDto joinMember(UserReqDto.JoinReqDto joinReqDto) {
        // 동일 유저네임 존재 검사
        Optional<User> userOp = userRepository.findByUsername(joinReqDto.getUsername());
        if (userOp.isPresent()) {
            throw new CustomApiException("동일한 username이 존재합니다");
        }

        // 패스워드 인코딩
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // dto 응답
        return new JoinResDto(userPS);
    }
}