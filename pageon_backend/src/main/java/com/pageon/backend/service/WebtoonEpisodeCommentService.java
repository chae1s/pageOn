package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.utils.PageableUtil;
import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.dto.response.BestCommentResponse;
import com.pageon.backend.dto.response.EpisodeCommentResponse;
import com.pageon.backend.dto.response.MyCommentResponse;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.WebtoonEpisode;
import com.pageon.backend.entity.WebtoonEpisodeComment;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebtoonEpisodeCommentLikeRepository;
import com.pageon.backend.repository.WebtoonEpisodeCommentRepository;
import com.pageon.backend.repository.WebtoonEpisodeRepository;
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
public class WebtoonEpisodeCommentService {

    private final WebtoonEpisodeCommentRepository webtoonEpisodeCommentRepository;
    private final UserRepository userRepository;
    private final WebtoonEpisodeRepository webtoonEpisodeRepository;
    private final WebtoonEpisodeCommentLikeRepository webtoonEpisodeCommentLikeRepository;

    @Transactional
    public void createComment(Long userId, Long episodeId, ContentEpisodeCommentRequest commentRequest) {

        final String text = commentRequest.getText();
        final Boolean isSpoiler = commentRequest.getIsSpoiler();

        log.info("[START] createComment: userId = {}, episodeId = {}", userId, episodeId);

        User user = userRepository.getReferenceById(userId);
        WebtoonEpisode webtoonEpisode = getWebtoonEpisode(episodeId);

        if (text.isBlank()) {
            log.error("Comment text is blank: episodeId = {}", episodeId);
            throw new CustomException(ErrorCode.COMMENT_TEXT_IS_BLANK);
        }

        WebtoonEpisodeComment comment = WebtoonEpisodeComment.builder()
                .user(user)
                .webtoonEpisode(webtoonEpisode)
                .text(text)
                .isSpoiler(isSpoiler)
                .build();

        webtoonEpisodeCommentRepository.save(comment);
        log.info("[SUCCESS] createComment committed: userId = {}, episodeId = {}", userId, episodeId);

    }

    @Transactional
    public Page<EpisodeCommentResponse> getCommentsByEpisodeId(Long userId, Long episodeId, Pageable pageable, String sort) {
        log.info("[START] getCommentsByEpisodeId in WebtoonEpisodeComments: userId = {}, episodeId = {}, sort = {}", userId, episodeId, sort);
        Pageable sortedPageable = PageableUtil.createCommentPageable(pageable, sort);

        WebtoonEpisode webtoonEpisode = getWebtoonEpisode(episodeId);

        String webtoonTitle = webtoonEpisode.getWebtoon().getTitle();
        Integer episodeNum = webtoonEpisode.getEpisodeNum();

        Page<WebtoonEpisodeComment> commentPage = webtoonEpisodeCommentRepository.findAllByWebtoonEpisode_IdAndDeletedAtNull(episodeId, sortedPageable);

        return commentPage.map(comment -> {
            Boolean isLiked = webtoonEpisodeCommentLikeRepository.existsByUser_IdAndWebtoonEpisodeComment_Id(userId, comment.getId());

            return EpisodeCommentResponse.fromWebtoonEntity(comment, userId, webtoonTitle, episodeNum, isLiked);
        });
    }

    @Transactional
    public Page<MyCommentResponse> getCommentsByUserId(Long userId, Pageable pageable) {
        Pageable sortedPageable = PageableUtil.createCreatedAtPageable(pageable);

        Page<WebtoonEpisodeComment> commentPage = webtoonEpisodeCommentRepository.findAllByUser_IdAndDeletedAtNull(userId, sortedPageable);

        return commentPage.map(comment -> {
            Long contentId = comment.getWebtoonEpisode().getWebtoon().getId();
            String contentTitle = comment.getWebtoonEpisode().getWebtoon().getTitle();

            return MyCommentResponse.fromWebtoonEntity(comment, contentId, contentTitle);
        });

    }

    @Transactional
    public void updateComment(Long userId, Long commentId, ContentEpisodeCommentRequest commentRequest) {
        final String newText = commentRequest.getText();
        final Boolean isSpoiler = commentRequest.getIsSpoiler();

        log.info("[START] updateComment: contentType = {}, userId = {}, commentId = {}", ContentType.WEBTOON, userId, commentId);

        WebtoonEpisodeComment comment = webtoonEpisodeCommentRepository.findById(commentId).orElseThrow(
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
        log.info("[START] deleteComment: contentType = {}, userId = {}, commentId = {}", ContentType.WEBTOON, userId, commentId);

        WebtoonEpisodeComment comment = webtoonEpisodeCommentRepository.findById(commentId).orElseThrow(
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
        WebtoonEpisodeComment comment =
                webtoonEpisodeCommentRepository.findFirstByWebtoonEpisode_IdAndDeletedAtIsNullOrderByLikeCountDescCreatedAtDesc(episodeId).orElse(null);

        if (comment == null) {
            return BestCommentResponse.fromWebnovelEntity(null, 0L);
        }

        Long totalCount = webtoonEpisodeCommentRepository.countByWebtoonEpisode_IdAndDeletedAtIsNull(episodeId);

        if (comment.getLikeCount() == 0) {
            comment = null;
        }

        return BestCommentResponse.fromWebtoonEntity(comment, totalCount);

    }
    private WebtoonEpisode getWebtoonEpisode(Long episodeId) {
        WebtoonEpisode webtoonEpisode = webtoonEpisodeRepository.findByIdWithWebtoon(episodeId).orElseThrow(
                () -> {
                    log.error("Failed to find WebtoonEpisode: episodeId = {}", episodeId);
                    return new CustomException(ErrorCode.EPISODE_NOT_FOUND);
                }
        );

        if (webtoonEpisode.getDeletedAt() != null) {
            log.error("WebtoonEpisode is deleted: episodeId = {}", episodeId);
            throw new CustomException(ErrorCode.EPISODE_IS_DELETED);
        }

        return webtoonEpisode;
    }



}
