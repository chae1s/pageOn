package com.pageon.backend.repository;

import com.pageon.backend.entity.Creators;
import com.pageon.backend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreatorRepository extends JpaRepository<Creators, Long> {
    Optional<Creators> findByUser(Users users);
}
