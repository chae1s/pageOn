package com.pageon.backend.repository;

import com.pageon.backend.entity.WebtoonEpisode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebtoonEpisodeRepository extends JpaRepository<WebtoonEpisode, Long> {

    List<WebtoonEpisode> findByWebtoonId(Long id);
}
