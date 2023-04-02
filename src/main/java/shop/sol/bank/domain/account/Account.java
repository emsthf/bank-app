package shop.sol.bank.domain.account;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shop.sol.bank.domain.user.User;
import shop.sol.bank.handler.ex.CustomApiException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 4)
    private Long number;  // 계좌번호

    @Column(nullable = false, length = 4)
    private Long password;

    @Column(nullable = false)
    private Long balance;  // 잔액(기본값 1000)

    // 항상 ORM에서 fk의 주인은 Many Entity 쪽이다.
    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩 전략 선택. 지연 로딩이 발동하는 시점은 account.getUser().아무필드호출() 이다. account.getUser()를 한다고 해서 지연 로딩이 발동하지 않는다!!
    private User user;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Account(Long id, Long number, Long password, Long balance, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void checkOwner(Long userId) {
//        String testUsername = user.getUsername();  // Lazy 로딩이 되어야 함
//        System.out.println("testUsername = " + testUsername);
        if (!Objects.equals(user.getId(), userId)) {  // Lazy 로딩이어도 id를 조회할 때는 select 쿼리가 날아가지 않는다.
            throw new CustomApiException("계좌 소유자가 아닙니다.");  // 리턴해주는 것 보다 바로 에러 던지는게 편하다.
        }
    }

    public void deposit(Long amount) {
        balance = balance + amount;
    }
}
