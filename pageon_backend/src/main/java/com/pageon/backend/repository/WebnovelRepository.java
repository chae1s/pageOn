package com.pageon.backend.repository;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Webnovel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WebnovelRepository extends JpaRepository<Webnovel, Long> {

    Optional<Webnovel> findById(Long id);

    @Query("SELECT w FROM Webnovel w " +
            "JOIN FETCH w.creator " +
            "JOIN FETCH w.keywords " +
            "WHERE w.id = :webnovelId")
    Optional<Webnovel> findByIdWithEpisodes(@Param("webnovelId") Long webnovelId);

    List<Webnovel> findByCreator(Creator creator);

    List<Webnovel> findByDeletedAtIsNull();

    @Query("SELECT w FROM Webnovel w WHERE w.serialDay = :serialDay AND w.status = 'ONGOING' AND w.deletedAt IS NULL ORDER BY w.viewCount DESC")
    List<Webnovel> findDailyRanking(SerialDay serialDay, Pageable pageable);

    @Query(value = "SELECT DISTINCT w FROM Webnovel w " +
            "JOIN FETCH w.creator c " +
            "JOIN FETCH w.keywords k " +
            "WHERE k.name = :keywordName",
            countQuery = "SELECT DISTINCT COUNT(w.id) FROM Webnovel w " +
                    "JOIN w.keywords k " +
                    "WHERE k.name = :keywordName"
    )
    Page<Webnovel> findByKeywordName(@Param("keywordName") String keywordName, Pageable pageable);

    @Query(value = "SELECT DISTINCT c FROM Webnovel c " +
            "JOIN FETCH c.creator " +
            "JOIN FETCH c.keywords k " +
            "WHERE (c.title LIKE %:query% OR c.creator.penName LIKE %:query%) " +
            "AND c.deletedAt IS NULL",
            countQuery = "SELECT COUNT(DISTINCT c.id) FROM Webnovel c " +
                    "JOIN c.keywords k " +
                    "WHERE (c.title LIKE %:query% OR c.creator.penName LIKE %:query%) " +
                    "AND c.deletedAt IS NULL "
    )
    Page<Webnovel> findByTitleOrPenNameContaining(@Param("query") String query, Pageable pageable);

    // 최근 신작 조회(신작 등록 후 2주가 지나지 않은 콘텐츠만 리턴)
    @Query(value = "SELECT DISTINCT w FROM Webnovel w " +
            "JOIN FETCH w.creator " +
            "WHERE w.createdAt >= :since " +
            "AND w.deletedAt IS NULL",
            countQuery = "SELECT COUNT(DISTINCT w.id) FROM Webnovel w " +
                    "WHERE w.createdAt >= :since " +
                    "AND w.deletedAt IS NULL "
    )
    Page<Webnovel> findRecentWebnovels(LocalDateTime since, Pageable pageable);

}
