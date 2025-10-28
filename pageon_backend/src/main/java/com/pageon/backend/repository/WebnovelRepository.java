package com.pageon.backend.repository;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Webnovel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WebnovelRepository extends JpaRepository<Webnovel, Long> {

    Optional<Webnovel> findById(Long id);
    Optional<Webnovel> findByIdAndDeleted(Long id, boolean isDeleted);

    List<Webnovel> findByCreator(Creator creator);

    List<Webnovel> findByDeleted(boolean deleted);

    @Query("SELECT w FROM Webnovel w WHERE w.serialDay = :serialDay AND w.deleted = false ORDER BY w.viewCount DESC")
    List<Webnovel> findDailyRanking(SerialDay serialDay, Pageable pageable);

    @Query("SELECT w FROM Webnovel w JOIN FETCH w.creator WHERE w.id IN :ids")
    List<Webnovel> findAllByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT w FROM Webnovel w JOIN w.keywords k WHERE k.name = :keywordName")
    Page<Webnovel> findByKeywordName(@Param("keywordName") String keywordName, Pageable pageable);

    @Query("SELECT w FROM Webnovel w WHERE"
            + "(w.title LIKE %:query% OR w.creator.penName LIKE %:query%) "
            + "AND w.deleted = false")
    Page<Webnovel> findByTitleOrPenNameContaining(@Param("query") String query, Pageable pageable);
}
