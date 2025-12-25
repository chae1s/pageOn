package com.pageon.backend.repository;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.ReadingContentsResponse;
import com.pageon.backend.entity.ReadingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory,Long> {

    Optional<ReadingHistory> findByUser_IdAndContent_Id(Long userId, Long contentId);

    @Query("SELECT new com.pageon.backend.dto.response.ReadingContentsResponse(c.id, c.title, c.creator.penName, c.cover, c.episodeUpdatedAt, r.lastReadAt, r.episodeId, c.dtype, c.serialDay, c.status) " +
            "FROM ReadingHistory r " +
            "JOIN r.content c " +
            "WHERE r.user.id = :userId")
    Page<ReadingContentsResponse> findAllReadingHistories(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new com.pageon.backend.dto.response.ReadingContentsResponse(c.id, c.title, c.creator.penName, c.cover, c.episodeUpdatedAt, r.lastReadAt, r.episodeId, c.dtype, c.serialDay, c.status) " +
            "FROM ReadingHistory r " +
            "JOIN r.content c " +
            "WHERE r.user.id = :userId " +
            "AND TYPE(c) = Webnovel")
    Page<ReadingContentsResponse> findWebnovelReadingHistories(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new com.pageon.backend.dto.response.ReadingContentsResponse(c.id, c.title, c.creator.penName, c.cover, c.episodeUpdatedAt, r.lastReadAt, r.episodeId, c.dtype, c.serialDay, c.status) " +
            "FROM ReadingHistory r " +
            "JOIN r.content c " +
            "WHERE r.user.id = :userId " +
            "AND TYPE(c) = Webtoon")
    Page<ReadingContentsResponse> findWebtoonReadingHistories(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM ReadingHistory r " +
            "JOIN FETCH r.content c " +
            "JOIN FETCH c.creator cr " +
            "WHERE r.user.id = :userId AND c.status = 'ONGOING' AND c.serialDay = :serialDay")
    List<ReadingHistory> findWithContentByUserIdAndSerialDay(@Param("userId") Long userId, @Param("serialDay") SerialDay serialDay);
}
