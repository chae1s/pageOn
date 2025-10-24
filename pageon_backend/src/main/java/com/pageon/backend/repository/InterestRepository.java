package com.pageon.backend.repository;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.entity.Interest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Boolean existsByUser_IdAndContentTypeAndContentId(Long userId, ContentType contentType, Long contentId);

    Optional<Interest> findByUser_IdAndContentTypeAndContentId(Long userId, ContentType contentType, Long contentId);

    Page<Interest> findAllByUser_Id(Long userId, Pageable pageable);

    Page<Interest> findAllByUser_IdAndContentType(Long userId, ContentType contentType, Pageable pageable);
}
