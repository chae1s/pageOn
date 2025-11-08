package com.pageon.backend.repository;


import com.pageon.backend.entity.WebnovelEpisodeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebnovelEpisodeCommentRepository extends JpaRepository<WebnovelEpisodeComment, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<WebnovelEpisodeComment> findAllByWebnovelEpisode_IdAndIsDeletedFalse(Long episodeId, Pageable pageable);

    @EntityGraph(attributePaths = {"webnovelEpisode", "webnovelEpisode.webnovel"})
    Page<WebnovelEpisodeComment> findAllByUser_IdAndIsDeletedFalse(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "webnovelEpisode"})
    Optional<WebnovelEpisodeComment> findById(Long commentId);
}
