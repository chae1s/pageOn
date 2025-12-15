package com.pageon.backend.repository;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.InterestContentResponse;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {
    Optional<Webtoon> findById(Long id);

    Optional<Webtoon> findByIdAndDeletedAtIsNull(Long id);

    List<Webtoon> findByCreator(Creator creator);

    List<Webtoon> findByDeletedAtIsNull();

    @Query("SELECT w FROM Webtoon w WHERE w.serialDay = :serialDay AND w.deletedAt IS NULL ORDER BY w.viewCount DESC")
    List<Webtoon> findDailyRanking(SerialDay serialDay, Pageable pageable);

    @Query("SELECT w FROM Webtoon w JOIN FETCH w.creator WHERE w.id IN :ids")
    List<Webtoon> findAllByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT w FROM Webtoon w JOIN w.keywords k WHERE k.name = :keywordName")
    Page<Webtoon> findByKeywordName(@Param("keywordName") String keywordName, Pageable pageable);

    @Query("SELECT w FROM Webtoon w WHERE"
            + "(w.title LIKE %:query% OR w.creator.penName LIKE %:query%) "
            + "AND w.deletedAt IS NULL")
    Page<Webtoon> findByTitleOrPenNameContaining(@Param("query") String query, Pageable pageable);

    @Query("SELECT new com.pageon.backend.dto.response.InterestContentResponse(c.id, c.title, c.creator.penName, c.episodeUpdatedAt, c.dtype, c.cover, c.serialDay, c.status) " +
            "FROM Interest i " +
            "JOIN Webtoon c ON i.contentId = c.id " +
            "WHERE i.user.id = :userId")
    Page<InterestContentResponse> findByInterestedWebtoons(@Param("userId") Long userId, Pageable pageable);
}
