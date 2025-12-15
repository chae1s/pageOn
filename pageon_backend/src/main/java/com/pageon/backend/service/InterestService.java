package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.utils.PageableUtil;
import com.pageon.backend.dto.response.ContentSimpleResponse;
import com.pageon.backend.dto.response.InterestContentResponse;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final WebnovelRepository webnovelRepository;
    private final WebtoonRepository webtoonRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public void registerInterest(Long userId, Long contentId) {
        User user = userRepository.getReferenceById(userId);
        Content content = contentRepository.findByIdAndDeletedAtIsNull(contentId).orElseThrow(
                () -> new CustomException(ErrorCode.CONTENT_NOT_FOUND)
        );

        ContentType contentType = ContentType.valueOf(content.getDtype());

        Interest interest = Interest.builder()
                .user(user)
                .contentType(contentType)
                .contentId(contentId)
                .build();

        interestRepository.save(interest);

    }

    @Transactional
    public void deleteInterest(Long userId, Long contentId) {

        Interest interest = interestRepository.findByUser_IdAndContentId(userId, contentId).orElseThrow(
                () -> new CustomException(ErrorCode.INTEREST_NOT_FOUND)
        );

        interestRepository.delete(interest);

    }

    @Transactional(readOnly = true)
    public Page<InterestContentResponse> getInterestedContents(Long userId, String contentType, String sort, Pageable pageable) {
        Pageable sortedPageable = PageableUtil.createInterestPageable(pageable, sort);

        return switch (contentType) {
            case "all" -> contentRepository.findByInterestedContents(userId, sortedPageable);
            case "webnovels" -> webnovelRepository.findByInterestedWebnovels(userId, sortedPageable);
            case "webtoons" -> webtoonRepository.findByInterestedWebtoons(userId, sortedPageable);
            default -> throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
        };

    }

}
