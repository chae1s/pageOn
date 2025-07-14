package com.pageon.backend.entity;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.DeleteStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@DynamicUpdate
@Table(name = "content_delete_requests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContentDeleteRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ContentType contentType;
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Creator creator;
    private String reason;
    private DeleteStatus deleteStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;


}
