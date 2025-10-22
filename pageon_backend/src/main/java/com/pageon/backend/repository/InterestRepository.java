package com.pageon.backend.repository;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Boolean existsByUser_IdAndContentTypeAndContentId(Long userId, ContentType contentType, Long contentId);

    Optional<Interest> findByUser_IdAndContentTypeAndContentId(Long userId, ContentType contentType, Long contentId);
}
