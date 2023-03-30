package shop.sol.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // ToDo 리팩토링 해야함! (계좌 소유자 확인시에 쿼리가 두 번 나가기 때문에 join fetch)
    Optional<Account> findByNumber(Long number);

    // select * from account where user_id = :id (Account 안에 있는 User의 id로 조회)
    List<Account> findByUser_id(Long id);
}
