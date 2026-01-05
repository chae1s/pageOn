package com.pageon.backend.repository;

import com.pageon.backend.entity.ContentRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RankingRepository extends JpaRepository<ContentRanking, Long> {

    @Query("SELECT DISTINCT r FROM ContentRanking r JOIN FETCH r.content c ORDER BY r.rankNo, r.content.id")
    List<ContentRanking> findAllRankings();

    @Query("SELECT DISTINCT r FROM ContentRanking r " +
            "JOIN FETCH r.content c " +
            "WHERE TYPE(c) = Webnovel ORDER By r.rankNo")
    List<ContentRanking> findWebnovelRankings();

    @Query("SELECT DISTINCT r FROM ContentRanking r " +
            "JOIN FETCH r.content c " +
            "WHERE TYPE(c) = Webtoon ORDER By r.rankNo")
    List<ContentRanking> findWebtoonRankings();
}
