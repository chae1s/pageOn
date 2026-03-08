package com.pageon.backend.repository;

import com.pageon.backend.common.enums.TransactionStatus;
import com.pageon.backend.common.enums.TransactionType;
import com.pageon.backend.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Integer> {

    @Query("SELECT p FROM PointTransaction p " +
            "WHERE p.user.id = :userId " +
            "AND p.transactionType = :transactionType " +
            "AND (p.transactionStatus = 'COMPLETED' OR p.transactionStatus = 'REFUNDED')")
    Page<PointTransaction> findAllByTransactionStatus(Long userId, TransactionType transactionType, Pageable pageable);

    Optional<PointTransaction> findByUser_IdAndOrderId(Long userId, String orderId);

    Optional<PointTransaction> findByIdAndUser_Id(Long transactionId, Long userId);
}
