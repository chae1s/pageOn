package com.pageon.backend.service;

import com.pageon.backend.common.enums.TransactionType;
import com.pageon.backend.entity.PointTransaction;
import com.pageon.backend.entity.User;
import com.pageon.backend.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .amount(amount)
                .balance(user.getPointBalance())
                .description(description)
                .domainId(domainId)
                .build();

        pointTransactionRepository.save(pointTransaction);
    }
}
