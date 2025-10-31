package com.pageon.backend.repository;

import com.pageon.backend.entity.User;
import com.pageon.backend.entity.WebnovelEpisodeRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WebnovelEpisodeRatingRepository extends JpaRepository<WebnovelEpisodeRating, Long> {

    @Query("SELECT r.score FROM WebnovelEpisodeRating r WHERE r.webnovelEpisode.id = :episodeId AND r.user.id = :userId")
    Integer findScoreByWebnovelEpisodeAndUser(Long episodeId, Long userId);


}
