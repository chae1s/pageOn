package com.pageon.backend.repository;

import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Webtoon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {
    Optional<Webtoon> findById(Long id);

    List<Webtoon> findByCreator(Creator creator);
}
