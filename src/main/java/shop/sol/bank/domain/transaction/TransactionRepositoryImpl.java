package shop.sol.bank.domain.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

interface Dao {
    List<Transaction> findTransactionList(@Param("accountId") Long accountId,
                                          @Param("division") String division,
                                          @Param("page") Integer page);
}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {
    private final EntityManager entityManager;

    @Override
    public List<Transaction> findTransactionList(Long accountId, String division, Integer page) {
        // JPQL 동적쿼리(division 값으로)
        String sql = "";
        sql += "select t from Transaction t ";  // 쿼리 연결을 위해 끝에 한칸 띄워주는거 잊지 말고!

        if (division.equals("WITHDRAW")) {
            sql += "join fetch t.withdrawAccount wa ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId";
        } else if (division.equals("DEPOSIT")) {
            sql += "join fetch t.depositAccount da ";
            sql += "where t.depositAccount.id = :depositAccountId";
        } else {
            sql += "left join fetch t.withdrawAccount wa ";
            sql += "left join fetch t.depositAccount da ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql += "or ";
            sql += "t.depositAccount.id = :depositAccountId";
        }

        TypedQuery<Transaction> query = entityManager.createQuery(sql, Transaction.class);

        if (division.equals("WITHDRAW")) {
            query = query.setParameter("withdrawAccountId", accountId);
        } else if (division.equals("DEPOSIT")) {
            query = query.setParameter("depositAccountId", accountId);
        } else {
            query = query.setParameter("withdrawAccountId", accountId);
            query = query.setParameter("depositAccountId", accountId);
        }

        query.setFirstResult(page * 5);
        query.setMaxResults(5);

        return query.getResultList();
    }
}
