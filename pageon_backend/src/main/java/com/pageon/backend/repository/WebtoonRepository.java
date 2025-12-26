package com.pageon.backend.repository;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.InterestContentResponse;
import com.pageon.backend.dto.response.ReadingContentsResponse;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.ReadingHistory;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {
    Optional<Webtoon> findById(Long id);

    Optional<Webtoon> findByIdAndDeletedAtIsNull(Long id);

    List<Webtoon> findByCreator(Creator creator);

    List<Webtoon> findByDeletedAtIsNull();

    @Query("SELECT w FROM Webtoon w WHERE w.serialDay = :serialDay AND w.status = 'ONGOING' AND w.deletedAt IS NULL ORDER BY w.viewCount DESC")
    List<Webtoon> findDailyRanking(SerialDay serialDay, Pageable pageable);

    @Query("SELECT DISTINCT w FROM Webtoon w JOIN w.keywords k WHERE k.name = :keywordName")
    Page<Webtoon> findByKeywordName(@Param("keywordName") String keywordName, Pageable pageable);

    @Query("SELECT w FROM Webtoon w WHERE"
            + "(w.title LIKE %:query% OR w.creator.penName LIKE %:query%) "
            + "AND w.deletedAt IS NULL")
    Page<Webtoon> findByTitleOrPenNameContaining(@Param("query") String query, Pageable pageable);

    // 최근 신작 조회(신작 등록 후 2주가 지나지 않은 콘텐츠만 리턴)
    @Query("SELECT w FROM Webtoon w " +
            "WHERE w.createdAt >= :since " +
            "AND w.deletedAt IS NULL")
    Page<Webtoon> findRecentWebtoons(LocalDateTime since, Pageable pageable);
}
