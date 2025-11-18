package com.pageon.backend.repository;

import com.pageon.backend.entity.WebtoonEpisodeCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebtoonEpisodeCommentLikeRepository extends JpaRepository<WebtoonEpisodeCommentLike, Long> {

    Boolean existsByUser_IdAndWebtoonEpisodeComment_Id(Long userId, Long commentId);
}
