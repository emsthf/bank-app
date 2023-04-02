package shop.sol.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import shop.sol.bank.domain.account.Account;
import shop.sol.bank.domain.account.AccountRepository;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject {

    @Profile("dev")  // prod 모드에서는 실행되면 안된다.
    @Bean
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository) {
        return (args) -> {
            // 서버 실행시에 무조건 실행된다.
            User ssol = userRepository.save(newUser("ssol", "솔"));
            User kim = userRepository.save(newUser("kim", "김"));
            Account ssolAccount1 = accountRepository.save(newAccount(1111L, ssol));
            Account kimAccount1 = accountRepository.save(newAccount(2222L, kim));
        };
    }
}
