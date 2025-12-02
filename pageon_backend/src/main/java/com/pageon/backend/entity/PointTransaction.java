package com.pageon.backend.entity;

import com.pageon.backend.common.base.BaseTimeEntity;
import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

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

    private TransactionType transactionType;
    private Integer amount;
    private Integer balance;
    private String description;

    private Long domainId;

}
