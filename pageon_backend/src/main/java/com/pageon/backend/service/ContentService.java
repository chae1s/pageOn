package com.pageon.backend.service;

import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.entity.Content;
import com.pageon.backend.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public Page<ContentResponse.Search> getContentsByTitleOrCreator(String query, Pageable sortedPageable) {

        log.debug("Entering getContentsByTitleOrCreator. Query = [{}], Pageable = {}", query, sortedPageable);
        Page<Content> contentPage = contentRepository.findByTitleOrPenNameContaining(query, sortedPageable);

        log.info("Content search by title/creator successful. Query: [{}]. Found {} total results.",
                query,
                contentPage.getTotalElements());

        return contentPage.map(ContentResponse.Search::fromEntity);
    }




}
