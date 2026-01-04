package com.pageon.backend.service;

import com.pageon.backend.common.enums.ActionType;
import com.pageon.backend.entity.ContentActionLog;
import com.pageon.backend.repository.ActionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    public void createActionLog(Long userId, Long contentId, ActionType actionType) {

        ContentActionLog actionLog = ContentActionLog.builder()
                .contentId(contentId)
                .userId(userId)
                .actionType(actionType)
                .build();

        actionLogRepository.save(actionLog);
    }
}
