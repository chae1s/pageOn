package com.pageon.backend.repository;

import com.pageon.backend.common.enums.TransactionType;
import com.pageon.backend.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Integer> {

    Page<PointTransaction> findAllByUser_IdAndTransactionType(Long userId, TransactionType transactionType, Pageable pageable);
}
