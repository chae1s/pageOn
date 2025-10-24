package com.pageon.backend.repository;

import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Webtoon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {
    Optional<Webtoon> findById(Long id);

    Optional<Webtoon> findByIdAndDeleted(Long id, boolean deleted);

    List<Webtoon> findByCreator(Creator creator);

    List<Webtoon> findByDeleted(boolean deleted);

    @Query("SELECT w FROM Webtoon w WHERE w.serialDay = :serialDay AND w.deleted = false ORDER BY w.viewCount DESC")
    List<Webtoon> findDailyRanking(SerialDay serialDay, Pageable pageable);

    @Query("SELECT w FROM Webtoon w JOIN FETCH w.creator WHERE w.id IN :ids")
    List<Webtoon> findAllByIdIn(@Param("ids") List<Long> ids);
}
