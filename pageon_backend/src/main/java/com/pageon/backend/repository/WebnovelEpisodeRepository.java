package com.pageon.backend.repository;

import com.pageon.backend.entity.WebnovelEpisode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebnovelEpisodeRepository extends JpaRepository<WebnovelEpisode, Long> {

    List<WebnovelEpisode> findByWebnovelId(Long id);
}
