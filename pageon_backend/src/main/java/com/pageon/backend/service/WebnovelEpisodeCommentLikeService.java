package com.pageon.backend.service;

import com.pageon.backend.entity.User;
import com.pageon.backend.entity.WebnovelEpisodeComment;
import com.pageon.backend.entity.WebnovelEpisodeCommentLike;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebnovelEpisodeCommentLikeRepository;
import com.pageon.backend.repository.WebnovelEpisodeCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebnovelEpisodeCommentLikeService {

    private final UserRepository userRepository;
    private final WebnovelEpisodeCommentRepository webnovelEpisodeCommentRepository;
    private final WebnovelEpisodeCommentLikeRepository webnovelEpisodeCommentLikeRepository;


    @Transactional
    public void createCommentLike(Long userId, Long commentId) {
        log.info("[START] createCommentLike: userId = {}, commentId = {}", userId, commentId);
        User user = userRepository.getReferenceById(userId);

        WebnovelEpisodeComment comment = getEpisodeComment(commentId);

        Boolean hasData = webnovelEpisodeCommentLikeRepository.existsByUser_IdAndWebnovelEpisodeComment_Id(userId, commentId);

        if (hasData) {
            log.error("WebnovelEpisodeComment already liked: commentId = {}", commentId);
            throw new CustomException(ErrorCode.COMMENT_ALREADY_LIKED);
        }

        WebnovelEpisodeCommentLike commentLike = WebnovelEpisodeCommentLike.builder().user(user).webnovelEpisodeComment(comment).build();

        webnovelEpisodeCommentLikeRepository.save(commentLike);

        log.info("Webnovel comments update like count: commentId = {}", commentId);
        comment.updateLikeCount();

        log.info("[SUCCESS] createCommentLike saved: userId = {}, commentId = {}, commentLikeId = {}", userId, commentId, commentLike.getId());

    }

    private WebnovelEpisodeComment getEpisodeComment(Long commentId) {
        WebnovelEpisodeComment comment = webnovelEpisodeCommentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.error("WebnovelEpisodeComment not found: commentId = {}", commentId);
                    return new CustomException(ErrorCode.COMMENT_NOT_FOUND);
                }
        );

        if (comment.getDeletedAt() != null) {
            log.error("WebnovelEpisodeComment is deleted: commentId = {}", commentId);
            throw new CustomException(ErrorCode.COMMENT_ALREADY_DELETED);
        }

        return comment;
    }
}
