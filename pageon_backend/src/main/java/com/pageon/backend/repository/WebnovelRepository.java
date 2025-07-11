package com.pageon.backend.repository;

import com.pageon.backend.entity.Webnovel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebnovelRepository extends JpaRepository<Webnovel, Long> {
}
