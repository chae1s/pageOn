package com.pageon.backend.service;

import com.pageon.backend.common.enums.ActionType;
import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.utils.PageableUtil;
import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.dto.response.BestCommentResponse;
import com.pageon.backend.dto.response.EpisodeCommentResponse;
import com.pageon.backend.dto.response.MyCommentResponse;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.WebnovelEpisode;
import com.pageon.backend.entity.WebnovelEpisodeComment;
import com.pageon.backend.entity.WebtoonEpisodeComment;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebnovelEpisodeCommentLikeRepository;
import com.pageon.backend.repository.WebnovelEpisodeCommentRepository;
import com.pageon.backend.repository.WebnovelEpisodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebnovelEpisodeCommentService {

    private final WebnovelEpisodeCommentRepository webnovelEpisodeCommentRepository;
    private final UserRepository userRepository;
    private final WebnovelEpisodeRepository webnovelEpisodeRepository;
    private final WebnovelEpisodeCommentLikeRepository webnovelEpisodeCommentLikeRepository;
    private final ActionLogService actionLogService;

    @Transactional
    public void createComment(Long userId, Long episodeId, ContentEpisodeCommentRequest commentRequest) {
        final String text = commentRequest.getText();
        final Boolean isSpoiler = commentRequest.getIsSpoiler();

        log.info("[START] createComment: userId = {}, episodeId = {}", userId, episodeId);

        User user = userRepository.getReferenceById(userId);
        WebnovelEpisode webnovelEpisode = getWebnovelEpisode(episodeId);

        if (text.isBlank()) {
            log.error("Comment text is blank: episodeId = {}", episodeId);
            throw new CustomException(ErrorCode.COMMENT_TEXT_IS_BLANK);
        }

        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder()
                .user(user)
                .webnovelEpisode(webnovelEpisode)
                .text(text)
                .isSpoiler(isSpoiler)
                .build();

        webnovelEpisodeCommentRepository.save(comment);

        actionLogService.createActionLog(userId, webnovelEpisode.getWebnovel().getId(), ContentType.WEBNOVEL, ActionType.COMMENT, 0);
        log.info("[SUCCESS] createComment committed: userId = {}, episodeId = {}", userId, episodeId);

    }

    @Transactional
    public Page<EpisodeCommentResponse> getCommentsByEpisodeId(Long userId, Long episodeId, Pageable pageable, String sort) {
        log.info("[START] getCommentsByEpisodeId: userId = {}, episodeId = {}, sort = {}", userId, episodeId, sort);
        Pageable sortedPageable = PageableUtil.createCommentPageable(pageable, sort);

        WebnovelEpisode webnovelEpisode = getWebnovelEpisode(episodeId);

        String webnovelTitle = webnovelEpisode.getWebnovel().getTitle();
        Integer episodeNum = webnovelEpisode.getEpisodeNum();

        Page<WebnovelEpisodeComment> commentPage = webnovelEpisodeCommentRepository.findAllByWebnovelEpisode_IdAndDeletedAtNull(episodeId, sortedPageable);

        return commentPage.map(comment -> {
            Boolean isLiked = webnovelEpisodeCommentLikeRepository.existsByUser_IdAndWebnovelEpisodeComment_Id(userId, comment.getId());

            return EpisodeCommentResponse.fromWebnovelEntity(comment, userId, webnovelTitle, episodeNum, isLiked);
        });

    }

    @Transactional
    public Page<MyCommentResponse> getCommentsByUserId(Long userId, Pageable pageable) {
        Pageable sortedPageable = PageableUtil.createCreatedAtPageable(pageable);

        Page<WebnovelEpisodeComment> commentPage = webnovelEpisodeCommentRepository.findAllByUser_IdAndDeletedAtNull(userId, sortedPageable);

        return commentPage.map(comment -> {
            Long contentId = comment.getWebnovelEpisode().getWebnovel().getId();
            String contentTitle = comment.getWebnovelEpisode().getWebnovel().getTitle();

            return MyCommentResponse.fromWebnovelEntity(comment, contentId, contentTitle);

        });

    }

    @Transactional
    public void updateComment(Long userId, Long commentId, ContentEpisodeCommentRequest commentRequest) {
        final String newText = commentRequest.getText();
        final Boolean isSpoiler = commentRequest.getIsSpoiler();

        log.info("[START] updateComment: contentType = {}, userId = {}, commentId = {}", ContentType.WEBNOVEL, userId, commentId);
        WebnovelEpisodeComment comment = webnovelEpisodeCommentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.error("Comment id not found: commentId = {}", commentId);
                    return new CustomException(ErrorCode.COMMENT_NOT_FOUND);
                }
        );

        if (!comment.getUser().getId().equals(userId)) throw new CustomException(ErrorCode.COMMENT_FORBIDDEN);

        if (newText.isBlank()) throw new CustomException(ErrorCode.COMMENT_TEXT_IS_BLANK);

        comment.updateComment(newText, isSpoiler);

        log.info("[SUCCESS] updateComment committed: userId = {}, commentId = {}", userId, commentId);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {

        log.info("[START] deleteComment: contentType = {}, userId = {}, commentId = {}", ContentType.WEBNOVEL, userId, commentId);

        WebnovelEpisodeComment comment = webnovelEpisodeCommentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.error("Comment id not found: commentId = {}", commentId);
                    return new CustomException(ErrorCode.COMMENT_NOT_FOUND);
                }
        );

        if (!comment.getUser().getId().equals(userId)) throw new CustomException(ErrorCode.COMMENT_FORBIDDEN);

        if (comment.getDeletedAt() != null) throw new CustomException(ErrorCode.COMMENT_ALREADY_DELETED);

        comment.deleteComment(LocalDateTime.now());

        log.info("[SUCCESS] deleteComment committed: userId = {}, commentId = {}", userId, commentId);
    }

    @Transactional(readOnly = true)
    public BestCommentResponse getBestCommentsByEpisodeId(Long episodeId) {
        WebnovelEpisodeComment comment =
                webnovelEpisodeCommentRepository.findFirstByWebnovelEpisode_IdAndDeletedAtIsNullOrderByLikeCountDescCreatedAtDesc(episodeId).orElse(null);

        if (comment == null) {
            return BestCommentResponse.fromWebnovelEntity(null, 0L);
        }

        Long totalCount = webnovelEpisodeCommentRepository.countByWebnovelEpisode_IdAndDeletedAtIsNull(episodeId);

        if (comment.getLikeCount() == 0) {
            comment = null;
        }

        return BestCommentResponse.fromWebnovelEntity(comment, totalCount);
    }

    private WebnovelEpisode getWebnovelEpisode(Long episodeId) {
        WebnovelEpisode webnovelEpisode = webnovelEpisodeRepository.findByIdWithWebnovel(episodeId).orElseThrow(
                () -> {
                    log.error("Failed to find WebnovelEpisode: episodeId = {}", episodeId);
                    return new CustomException(ErrorCode.EPISODE_NOT_FOUND);
                }
        );

        if (webnovelEpisode.getDeletedAt() != null) {
            log.error("WebnovelEpisode is deleted: episodeId = {}", episodeId);
            throw new CustomException(ErrorCode.EPISODE_IS_DELETED);
        }

        return webnovelEpisode;
    }


}
