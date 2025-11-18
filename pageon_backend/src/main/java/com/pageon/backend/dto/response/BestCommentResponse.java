package com.pageon.backend.dto.response;

import com.pageon.backend.entity.WebnovelEpisodeComment;
import com.pageon.backend.entity.WebtoonEpisodeComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestCommentResponse {
    private Long id;
    private String text;
    private String nickname;
    private LocalDateTime createdAt;
    private Long totalCount;

    public static BestCommentResponse fromWebnovelEntity(WebnovelEpisodeComment comment, Long totalCount) {
        if (comment == null) {
            return BestCommentResponse.builder()
                    .totalCount(totalCount).build();
        }

        return BestCommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .nickname(comment.getUser().getNickname())
                .createdAt(comment.getCreatedAt())
                .totalCount(totalCount)
                .build();
    }

    public static BestCommentResponse fromWebtoonEntity(WebtoonEpisodeComment comment, Long totalCount) {
        if (comment == null) {
            return BestCommentResponse.builder()
                    .totalCount(totalCount).build();
        }

        return BestCommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .nickname(comment.getUser().getNickname())
                .createdAt(comment.getCreatedAt())
                .totalCount(totalCount)
                .build();
    }
}
