package com.locket.user.domain.budget.repository;

import com.locket.user.domain.budget.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {

    @Query(value = "SELECT COALESCE(SUM(pt.amount), 0) FROM payment_transaction pt " +
            "WHERE pt.buyer_id = :buyerId " +
            "AND pt.payment_transaction_status = 'SUCCESS' " +
            "AND DATE_PART('year', pt.payment_timestamp) = :year " +
            "AND DATE_PART('month', pt.payment_timestamp) = :month", nativeQuery = true)
    BigDecimal sumSuccessAmountByUserAndYearMonth(@Param("buyerId") long buyerId,
                                                  @Param("year") int year,
                                                  @Param("month") int month);
}
