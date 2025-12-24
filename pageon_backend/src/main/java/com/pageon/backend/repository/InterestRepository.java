package com.pageon.backend.repository;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.response.InterestContentResponse;
import com.pageon.backend.entity.Interest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Boolean existsByUser_IdAndContentId(Long userId, Long contentId);

    Optional<Interest> findByUser_IdAndContentId(Long userId, Long contentId);

    Page<Interest> findAllByUser_Id(Long userId, Pageable pageable);


    @Query("SELECT new com.pageon.backend.dto.response.InterestContentResponse(c.id, c.title, c.creator.penName, c.episodeUpdatedAt, c.dtype, c.cover, c.serialDay, c.status) " +
            "FROM Interest i " +
            "JOIN i.content c " +
            "WHERE i.user.id = :userId")
    Page<InterestContentResponse> findAllInterests(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new com.pageon.backend.dto.response.InterestContentResponse(c.id, c.title, c.creator.penName, c.episodeUpdatedAt, c.dtype, c.cover, c.serialDay, c.status) " +
            "FROM Interest i " +
            "JOIN i.content c " +
            "WHERE i.user.id = :userId " +
            "AND TYPE(c) = Webnovel")
    Page<InterestContentResponse> findWebnovelInterests(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT new com.pageon.backend.dto.response.InterestContentResponse(c.id, c.title, c.creator.penName, c.episodeUpdatedAt, c.dtype, c.cover, c.serialDay, c.status) " +
            "FROM Interest i " +
            "JOIN i.content c " +
            "WHERE i.user.id = :userId " +
            "AND TYPE(c) = Webtoon")
    Page<InterestContentResponse> findWebtoonInterests(@Param("userId") Long userId, Pageable pageable);


}
