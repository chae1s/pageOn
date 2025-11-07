package com.pageon.backend.repository;

import com.pageon.backend.entity.WebtoonEpisodeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebtoonEpisodeCommentRepository extends JpaRepository<WebtoonEpisodeComment, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<WebtoonEpisodeComment> findAllByWebtoonEpisode_IdAndIsDeletedFalse(Long episodeId, Pageable pageable);

    @EntityGraph(attributePaths = {"webtoonEpisode", "webtoonEpisode.webtoon", "user"})
    Page<WebtoonEpisodeComment> findAllByUser_IdAndIsDeletedFalse(Long userId, Pageable pageable);
}
