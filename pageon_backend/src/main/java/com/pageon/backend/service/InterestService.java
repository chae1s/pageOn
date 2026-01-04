package com.pageon.backend.service;

import com.pageon.backend.common.enums.ActionType;
import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.utils.PageableUtil;
import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final ActionLogService actionLogService;

    @Transactional
    public void registerInterest(Long userId, Long contentId) {
        User user = userRepository.getReferenceById(userId);
        Content content = contentRepository.findByIdAndDeletedAtIsNull(contentId).orElseThrow(
                () -> new CustomException(ErrorCode.CONTENT_NOT_FOUND)
        );

        Interest interest = Interest.builder()
                .user(user)
                .content(content)
                .build();

        interestRepository.save(interest);

        actionLogService.createActionLog(userId, contentId, ActionType.INTEREST);

    }

    @Transactional
    public void deleteInterest(Long userId, Long contentId) {

        Interest interest = interestRepository.findByUser_IdAndContentId(userId, contentId).orElseThrow(
                () -> new CustomException(ErrorCode.INTEREST_NOT_FOUND)
        );

        interestRepository.delete(interest);

    }

    @Transactional(readOnly = true)
    public Page<ContentResponse.InterestContent> getInterestedContents(Long userId, String contentType, String sort, Pageable pageable) {
        Pageable sortedPageable = PageableUtil.createInterestPageable(pageable, sort);

        switch (contentType) {
            case "all" -> {
                Page<Interest> interests = interestRepository.findAllInterests(userId, sortedPageable);
                return interests.map(interest -> ContentResponse.InterestContent.fromEntity(interest.getContent()));
            }
            case "webnovels" -> {
                Page<Interest> interests = interestRepository.findWebnovelInterests(userId, sortedPageable);
                return interests.map(interest -> ContentResponse.InterestContent.fromEntity(interest.getContent()));
            }
            case "webtoons" -> {
                Page<Interest> interests = interestRepository.findWebtoonInterests(userId, sortedPageable);
                return interests.map(interest -> ContentResponse.InterestContent.fromEntity(interest.getContent()));
            }

        };

        throw new CustomException(ErrorCode.INVALID_CONTENT_TYPE);
    }

}
