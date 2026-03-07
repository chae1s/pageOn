package com.pageon.backend.entity;

import com.pageon.backend.common.base.BaseTimeEntity;
import com.pageon.backend.common.enums.TransactionStatus;
import com.pageon.backend.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "point_transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointTransaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus transactionStatus;
    private Integer amount;
    private Integer balance;
    private String description;

    @Column(unique = true)
    private String orderId;

    private Long domainId;

    private LocalDateTime paidAt;

    public void completedCharge(LocalDateTime paidAt, Integer balance) {
        transactionStatus = TransactionStatus.COMPLETED;
        this.paidAt = paidAt;
        this.balance = balance;
    }

    public void failedCharge() {
        transactionStatus = TransactionStatus.FAILED;
    }

}
