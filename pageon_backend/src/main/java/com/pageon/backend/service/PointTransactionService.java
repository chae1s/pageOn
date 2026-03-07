package com.pageon.backend.service;

import com.pageon.backend.common.enums.TransactionStatus;
import com.pageon.backend.common.enums.TransactionType;
import com.pageon.backend.dto.response.PointTransactionResponse;
import com.pageon.backend.entity.PointTransaction;
import com.pageon.backend.entity.User;
import com.pageon.backend.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointTransactionService {
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional
    public void usePoint(User user, int amount, String description, Long domainId) {
        user.usePoints(amount);

        PointTransaction pointTransaction = PointTransaction.builder()
                .user(user)
                .transactionType(TransactionType.USE)
                .transactionStatus(TransactionStatus.COMPLETED)
                .amount(amount)
                .balance(user.getPointBalance())
                .description(description)
                .domainId(domainId)
                .paidAt(LocalDateTime.now())
                .build();

        pointTransactionRepository.save(pointTransaction);
    }

    @Transactional(readOnly = true)
    public Page<PointTransactionResponse> getPointHistory(Long userId, String type, Pageable pageable) {

        TransactionType transactionType = TransactionType.valueOf(type);
        Page<PointTransaction> pointPage
                = pointTransactionRepository.findAllByUser_IdAndTransactionTypeAndTransactionStatus(userId, transactionType, TransactionStatus.COMPLETED, pageable);

        return pointPage.map(PointTransactionResponse::fromEntity);

    }
}
