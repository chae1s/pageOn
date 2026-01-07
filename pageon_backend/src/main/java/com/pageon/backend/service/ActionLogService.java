package com.pageon.backend.service;

import com.pageon.backend.common.base.EpisodeBase;
import com.pageon.backend.common.enums.ActionType;
import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.response.ActionCountResponse;
import com.pageon.backend.entity.ContentActionLog;
import com.pageon.backend.repository.ActionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    @Transactional
    public void createActionLog(Long userId, Long contentId, ContentType contentType, ActionType actionType, Integer ratingScore) {

        ContentActionLog actionLog = ContentActionLog.builder()
                .contentId(contentId)
                .userId(userId)
                .contentType(contentType)
                .actionType(actionType)
                .ratingScore(ratingScore)
                .build();

        actionLogRepository.save(actionLog);
    }
}
