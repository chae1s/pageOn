package com.pageon.backend.service;

import com.pageon.backend.entity.User;
import com.pageon.backend.entity.WebtoonEpisodeComment;
import com.pageon.backend.entity.WebtoonEpisodeCommentLike;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebtoonEpisodeCommentLikeRepository;
import com.pageon.backend.repository.WebtoonEpisodeCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebtoonEpisodeCommentLikeService {

    private final UserRepository userRepository;
    private final WebtoonEpisodeCommentRepository webtoonEpisodeCommentRepository;
    private final WebtoonEpisodeCommentLikeRepository webtoonEpisodeCommentLikeRepository;


    @Transactional
    public void createCommentLike(Long userId, Long commentId) {
        log.info("[START] createCommentLike: userId = {}, commentId = {}", userId, commentId);
        User user = userRepository.getReferenceById(userId);

        WebtoonEpisodeComment comment = getEpisodeComment(commentId);

        Boolean hasData = webtoonEpisodeCommentLikeRepository.existsByUser_IdAndWebtoonEpisodeComment_Id(userId, commentId);

        if (hasData) {
            log.error("WebtoonEpisodeComment already liked: commentId = {}", commentId);
            throw new CustomException(ErrorCode.COMMENT_ALREADY_LIKED);
        }

        WebtoonEpisodeCommentLike commentLike = WebtoonEpisodeCommentLike.builder().user(user).webtoonEpisodeComment(comment).build();

        webtoonEpisodeCommentLikeRepository.save(commentLike);

        log.info("Webtoon comments update like count: commentId = {}", commentId);
        comment.updateLikeCount();

        log.info("[SUCCESS] createCommentLike saved: userId = {}, commentId = {}, commentLikeId = {}", userId, commentId, commentLike.getId());
    }

    @Transactional
    public void deleteCommentLike(Long userId, Long commentId) {
        WebtoonEpisodeComment comment = getEpisodeComment(commentId);

        WebtoonEpisodeCommentLike commentLike = webtoonEpisodeCommentLikeRepository.findByUser_IdAndWebtoonEpisodeComment_Id(userId, commentId).orElseThrow(
                () -> {
                    log.error("WebtoonEpisodeCommentLike not found: userId = {}, commentId = {}", userId, commentId);
                    return new CustomException(ErrorCode.COMMENT_LIKE_NOT_FOUND);
                }
        );

        webtoonEpisodeCommentLikeRepository.delete(commentLike);

        log.info("Webtoon comments delete like count: commentId = {}", commentId);
        comment.deleteLikeCount();

        log.info("[SUCCESS] commentLike deleted: userId = {}, commentId = {}", userId, commentId);
    }

    private WebtoonEpisodeComment getEpisodeComment(Long commentId) {
        WebtoonEpisodeComment comment = webtoonEpisodeCommentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.error("WebtoonEpisodeComment not found: commentId = {}", commentId);
                    return new CustomException(ErrorCode.COMMENT_NOT_FOUND);
                }
        );

        if (comment.getDeletedAt() != null) {
            log.error("WebtoonEpisodeComment is deleted: commentId = {}", commentId);
            throw new CustomException(ErrorCode.COMMENT_ALREADY_DELETED);
        }

        return comment;
    }
}
