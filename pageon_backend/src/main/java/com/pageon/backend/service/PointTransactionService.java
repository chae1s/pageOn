package com.pageon.backend.service;

import com.pageon.backend.common.enums.TransactionType;
import com.pageon.backend.common.utils.PageableUtil;
import com.pageon.backend.dto.response.PointTransactionResponse;
import com.pageon.backend.entity.PointTransaction;
import com.pageon.backend.entity.User;
import com.pageon.backend.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public Page<PointTransactionResponse> getPointHistory(Long userId, String type, Pageable pageable) {
        Pageable sortedPageable = PageableUtil.createCreatedAtPageable(pageable);

        TransactionType transactionType = TransactionType.valueOf(type);
        Page<PointTransaction> pointPage = pointTransactionRepository.findAllByUser_IdAndTransactionType(userId, transactionType, sortedPageable);

        return pointPage.map(PointTransactionResponse::fromEntity);

    }
}
