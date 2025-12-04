package com.pageon.backend.service;

import com.pageon.backend.common.base.EpisodeBase;
import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.entity.ReadingHistory;
import com.pageon.backend.entity.User;
import com.pageon.backend.repository.ReadingHistoryRepository;
import com.pageon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingHistoryService {

    private final ReadingHistoryRepository readingHistoryRepository;
    private final UserRepository userRepository;

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
}
