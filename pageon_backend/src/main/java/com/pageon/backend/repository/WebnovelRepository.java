package com.pageon.backend.repository;

import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Webnovel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WebnovelRepository extends JpaRepository<Webnovel, Long> {

    Optional<Webnovel> findById(Long id);

    List<Webnovel> findByCreator(Creator creator);
}
