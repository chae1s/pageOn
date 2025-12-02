package com.pageon.backend.dto.response;

import com.pageon.backend.entity.PointTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointTransactionResponse {
    private LocalDateTime createdAt;
    private String description;
    private Integer amount;
    private Integer balance;

    public static PointTransactionResponse fromEntity(PointTransaction pointTransaction) {
        return PointTransactionResponse.builder()
                .createdAt(pointTransaction.getCreatedAt())
                .description(pointTransaction.getDescription())
                .amount(pointTransaction.getAmount())
                .balance(pointTransaction.getBalance())
                .build();
    }
}
