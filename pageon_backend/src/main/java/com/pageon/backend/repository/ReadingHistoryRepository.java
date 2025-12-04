package com.pageon.backend.repository;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.entity.ReadingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory,Long> {

    Optional<ReadingHistory> findByUser_IdAndContentTypeAndContentId(Long userId, ContentType contentType, Long contentId);

}
