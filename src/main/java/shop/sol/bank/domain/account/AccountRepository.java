package shop.sol.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // 만약 Account를 조회할 때 User의 id를 제외한 데이터가 필요할 경우 Lazy 로딩 말고 Fetch join을 사용하는 것이 좋다! (계좌 소유자 확인 시에 select 쿼리가 두 번 나가기 때문에)
    // fetch join을 하면 조인해서 객체에 값을 미리 가져올 수 있다.
//    @Query("SELECT ac FROM Account ac JOIN FETCH ac.user WHERE ac.number = :number")
    Optional<Account> findByNumber(@Param("number") Long number);

    // select * from account where user_id = :id (Account 안에 있는 User의 id로 조회)
    List<Account> findByUser_id(Long id);
}
