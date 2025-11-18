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
public class MyCommentResponse {
    private Long id;
    private String text;
    private Long episodeId;
    private Integer episodeNum;
    private Long contentId;
    private String contentTitle;
    private LocalDateTime createdAt;
    private Long likeCount;

    public static MyCommentResponse fromWebnovelEntity(WebnovelEpisodeComment comment, Long contentId, String contentTitle) {
        return MyCommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .episodeId(comment.getWebnovelEpisode().getId())
                .episodeNum(comment.getWebnovelEpisode().getEpisodeNum())
                .contentId(contentId)
                .contentTitle(contentTitle)
                .createdAt(comment.getCreatedAt())
                .likeCount(comment.getLikeCount())
                .build();
    }

    public static MyCommentResponse fromWebtoonEntity(WebtoonEpisodeComment comment, Long contentId, String contentTitle) {
        return MyCommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .episodeId(comment.getWebtoonEpisode().getId())
                .episodeNum(comment.getWebtoonEpisode().getEpisodeNum())
                .contentId(contentId)
                .contentTitle(contentTitle)
                .createdAt(comment.getCreatedAt())
                .likeCount(comment.getLikeCount())
                .build();
    }
}
