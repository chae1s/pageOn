package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.dto.response.ContentSimpleResponse;
import com.pageon.backend.entity.Interest;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.InterestRepository;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebnovelRepository;
import com.pageon.backend.repository.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public void registerInterest(Long userId, ContentType contentType, Long contentId) {
        User user = userRepository.getReferenceById(userId);

        if (contentType == ContentType.WEBNOVEL) {
            webnovelRepository.findById(contentId).orElseThrow(
                    () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
            );

        } else if (contentType == ContentType.WEBTOON) {
            webtoonRepository.findById(contentId).orElseThrow(
                    () -> new CustomException(ErrorCode.WEBTOON_NOT_FOUND)
            );
            log.info("WEBTOON ID: {}", contentId);
        }

        Interest interest = Interest.builder()
                .user(user)
                .contentType(contentType)
                .contentId(contentId)
                .build();

        interestRepository.save(interest);

    }

    public void deleteInterest(Long userId, ContentType contentType, Long contentId) {

        userRepository.findByIdAndDeleted(userId, false).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Interest interest = interestRepository.findByUser_IdAndContentTypeAndContentId(userId, contentType, contentId).orElseThrow(
                () -> new CustomException(ErrorCode.INTEREST_NOT_FOUND)
        );

        interestRepository.delete(interest);

    }

    public Page<ContentSimpleResponse> getInterestedContents(Long userId, ContentType contentType, Pageable pageable) {
        Page<Interest> interests;

        if (contentType != null) {
            interests = interestRepository.findAllByUser_IdAndContentType(userId, contentType, pageable);
        } else {
            interests = interestRepository.findAllByUser_Id(userId, pageable);
        }

        List<Interest> interestsOnThisPage = interests.getContent();

        List<Long> webtoonIds = interestsOnThisPage.stream()
                .filter(i -> i.getContentType() == ContentType.WEBTOON)
                .map(Interest::getContentId).toList();

        List<Long> webnovelIds = interestsOnThisPage.stream()
                .filter(i -> i.getContentType() == ContentType.WEBNOVEL)
                .map(Interest::getContentId).toList();

        Map<Long, Webtoon> webtoonMap = webtoonRepository.findAllByIdIn(webtoonIds).stream()
                .collect(Collectors.toMap(Webtoon::getId, Function.identity()));

        Map<Long, Webnovel> webnovelMap = webnovelRepository.findAllByIdIn(webnovelIds).stream()
                .collect(Collectors.toMap(Webnovel::getId, Function.identity()));

        List<ContentSimpleResponse> contentSimpleResponses = new ArrayList<>();

        for (Interest interest: interestsOnThisPage) {
            if (interest.getContentType() == ContentType.WEBTOON) {
                Webtoon webtoon = webtoonMap.get(interest.getContentId());
                if (webtoon != null) {
                    contentSimpleResponses.add(ContentSimpleResponse.fromEntity(webtoon.getId(), webtoon.getTitle(), webtoon.getCreator().getPenName(), webtoon.getCover(), "webtoons"));
                }
            } else if (interest.getContentType() == ContentType.WEBNOVEL) {
                Webnovel webnovel = webnovelMap.get(interest.getContentId());
                if (webnovel != null) {
                    contentSimpleResponses.add(ContentSimpleResponse.fromEntity(webnovel.getId(), webnovel.getTitle(), webnovel.getCreator().getPenName(), webnovel.getCover(), "webnovels"));
                }
            }
        }

        return new PageImpl<>(contentSimpleResponses, pageable, interests.getTotalElements());

    }
}
