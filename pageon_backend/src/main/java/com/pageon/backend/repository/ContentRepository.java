package com.pageon.backend.repository;

import com.pageon.backend.dto.response.InterestContentResponse;
import com.pageon.backend.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {

    Optional<Content> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT c FROM Content c " +
            "WHERE (c.title LIKE %:query% OR c.creator.penName LIKE %:query%) " +
            "AND c.deletedAt IS NULL")
    Page<Content> findByTitleOrPenNameContaining(@Param("query") String query, Pageable pageable);

    @Query("SELECT new com.pageon.backend.dto.response.InterestContentResponse(c.id, c.title, c.creator.penName, c.episodeUpdatedAt, c.dtype, c.cover, c.serialDay, c.status) " +
            "FROM Interest i " +
            "JOIN Content c ON i.contentId = c.id " +
            "WHERE i.user.id = :userId")
    Page<InterestContentResponse> findByInterestedContents(@Param("userId") Long userId, Pageable pageable);
}
