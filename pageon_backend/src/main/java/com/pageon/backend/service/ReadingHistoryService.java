package com.pageon.backend.service;

import com.pageon.backend.common.base.EpisodeBase;
import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.utils.PageableUtil;
import com.pageon.backend.dto.response.ReadingContentsResponse;
import com.pageon.backend.entity.ReadingHistory;
import com.pageon.backend.entity.User;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingHistoryService {

    private final ReadingHistoryRepository readingHistoryRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final WebnovelRepository webnovelRepository;
    private final WebtoonRepository webtoonRepository;

    @Transactional
    public void checkReadingHistory(Long userId, ContentType contentType, Long contentId, Long episodeId) {
        User user = userRepository.getReferenceById(userId);
        ReadingHistory readingHistory = readingHistoryRepository.findByUser_IdAndContentTypeAndContentId(user.getId(), contentType, contentId).orElse(null);

        if (readingHistory == null) {
            log.info("ReadingHistory not found");
            createReadingHistory(user, contentType, contentId, episodeId);
        } else {
            log.info("ReadingHistory found");
            updateReadingHistory(readingHistory, episodeId);
        }

    }

    private void createReadingHistory(User user, ContentType contentType, Long contentId, Long episodeId) {
        ReadingHistory readingHistory = ReadingHistory.builder()
                .user(user)
                .contentType(contentType)
                .contentId(contentId)
                .episodeId(episodeId)
                .build();

        readingHistoryRepository.save(readingHistory);
    }

    private void updateReadingHistory(ReadingHistory readingHistory, Long episodeId) {
        readingHistory.updateEpisodeId(episodeId);
    }

    public Page<ReadingContentsResponse> getReadingHistory(Long userId, String contentType, String sort, Pageable pageable) {
        Pageable sortedPageable = PageableUtil.createReadingHistory(pageable, sort);

        return switch (contentType) {
            case "all" -> contentRepository.findByReadingContents(userId, sortedPageable);
            case "webnovels" -> webnovelRepository.findByReadingWebnovels(userId, sortedPageable);
            case "webtoons" -> webtoonRepository.findByReadingWebtoons(userId, sortedPageable);
            default -> throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
        };
        
    }
}
