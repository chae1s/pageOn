package com.pageon.backend.service;

import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.WebnovelEpisode;
import com.pageon.backend.entity.WebnovelEpisodeComment;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebnovelEpisodeCommentRepository;
import com.pageon.backend.repository.WebnovelEpisodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebnovelEpisodeCommentService {

    private final WebnovelEpisodeCommentRepository webnovelEpisodeCommentRepository;
    private final UserRepository userRepository;
    private final WebnovelEpisodeRepository webnovelEpisodeRepository;

    public void createComment(Long userId, Long episodeId, ContentEpisodeCommentRequest commentRequest) {
        final String text = commentRequest.getText();
        final Boolean isSpoiler = commentRequest.getIsSpoiler();

        log.info("[START] createComment: userId = {}, episodeId = {}", userId, episodeId);

        User user = userRepository.getReferenceById(userId);
        WebnovelEpisode webnovelEpisode = webnovelEpisodeRepository.findByIdWithWebnovel(episodeId).orElseThrow(
                () -> {
                    log.error("Failed to find WebnovelEpisode: episodeId = {}", episodeId);
                    return new CustomException(ErrorCode.EPISODE_NOT_FOUND);
                }
        );

        if (webnovelEpisode.getDeleted()) {
            log.error("WebnovelEpisode is deleted: episodeId = {}", episodeId);
            throw new CustomException(ErrorCode.EPISODE_IS_DELETED);
        }

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
        log.info("[SUCCESS] createComment committed: userId = {}, episodeId = {}", userId, episodeId);

    }
}
