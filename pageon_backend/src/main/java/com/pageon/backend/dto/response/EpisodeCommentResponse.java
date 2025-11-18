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
public class EpisodeCommentResponse {
    private Long id;
    private String text;
    private Integer episodeNum;
    private String contentTitle;
    private String nickname;
    private LocalDateTime createdAt;
    private Boolean isSpoiler;
    private Boolean isMine;
    private Long likeCount;
    private Boolean isLiked;


    public static EpisodeCommentResponse fromWebnovelEntity(WebnovelEpisodeComment comment, Long currentUserId, String contentTitle, Integer episodeNum, Boolean isLiked) {
        Boolean isMine = (comment.getUser().getId().equals(currentUserId));

        return EpisodeCommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .episodeNum(episodeNum)
                .contentTitle(contentTitle)
                .nickname(comment.getUser().getNickname())
                .createdAt(comment.getCreatedAt())
                .isSpoiler(comment.getIsSpoiler())
                .isMine(isMine)
                .likeCount(comment.getLikeCount())
                .isLiked(isLiked)
                .build();
    }

    public static EpisodeCommentResponse fromWebtoonEntity(WebtoonEpisodeComment comment, Long currentUserId, String contentTitle, Integer episodeNum, Boolean isLiked) {
        Boolean isMine = (comment.getUser().getId().equals(currentUserId));

        return EpisodeCommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .episodeNum(episodeNum)
                .contentTitle(contentTitle)
                .nickname(comment.getUser().getNickname())
                .createdAt(comment.getCreatedAt())
                .isSpoiler(comment.getIsSpoiler())
                .isMine(isMine)
                .likeCount(comment.getLikeCount())
                .isLiked(isLiked)
                .build();
    }
}
