package com.pageon.backend.service;

import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.WebtoonEpisode;
import com.pageon.backend.entity.WebtoonEpisodeComment;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebtoonEpisodeCommentRepository;
import com.pageon.backend.repository.WebtoonEpisodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebtoonEpisodeCommentService {

    private final WebtoonEpisodeCommentRepository webtoonEpisodeCommentRepository;
    private final UserRepository userRepository;
    private final WebtoonEpisodeRepository webtoonEpisodeRepository;

    public void createComment(Long userId, Long episodeId, ContentEpisodeCommentRequest commentRequest) {

        final String text = commentRequest.getText();
        final Boolean isSpoiler = commentRequest.getIsSpoiler();

        log.info("[START] createComment: userId = {}, episodeId = {}", userId, episodeId);

        User user = userRepository.getReferenceById(userId);
        WebtoonEpisode webtoonEpisode = webtoonEpisodeRepository.findByIdWithWebtoon(episodeId).orElseThrow(
                () -> {
                    log.error("Failed to find WebtoonEpisode: episodeId = {}", episodeId);
                    return new CustomException(ErrorCode.EPISODE_NOT_FOUND);
                }
        );

        if (webtoonEpisode.getDeleted()) {
            log.error("WebtoonEpisode is deleted: episodeId = {}", episodeId);
            throw new CustomException(ErrorCode.EPISODE_IS_DELETED);
        }

        if (text.replaceAll("\\s", "").isEmpty()) {
            log.error("Comment text is blank: episodeId = {}", episodeId);
            throw new CustomException(ErrorCode.COMMENT_TEXT_IS_BLANK);
        }

        WebtoonEpisodeComment comment = WebtoonEpisodeComment.builder()
                .user(user)
                .webtoonEpisode(webtoonEpisode)
                .text(text)
                .build();

        webtoonEpisodeCommentRepository.save(comment);
        log.info("[SUCCESS] createComment committed: userId = {}, episodeId = {}", userId, episodeId);

    }
}
