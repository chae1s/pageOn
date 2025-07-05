package com.pageon.backend.repository;

import com.pageon.backend.entity.Creators;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorRepository extends JpaRepository<Creators, Long> {
}
