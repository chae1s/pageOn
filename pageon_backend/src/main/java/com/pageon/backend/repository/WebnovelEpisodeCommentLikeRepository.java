package com.pageon.backend.repository;

import com.pageon.backend.entity.WebnovelEpisodeCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebnovelEpisodeCommentLikeRepository extends JpaRepository<WebnovelEpisodeCommentLike,Long> {

    Boolean existsByUser_IdAndWebnovelEpisodeComment_Id(Long userId, Long commentId);

}
