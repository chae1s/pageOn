package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.entity.Interest;
import com.pageon.backend.entity.User;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.InterestRepository;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebnovelRepository;
import com.pageon.backend.repository.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final WebnovelRepository webnovelRepository;
    private final WebtoonRepository webtoonRepository;

    public void registerInterest(Long userId, ContentType contentType, Long contentId) {
        User user = userRepository.findByIdAndDeleted(userId, false).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (contentType == ContentType.WEBNOVEL) {
            webnovelRepository.findById(contentId).orElseThrow(
                    () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
            );

        } else if (contentType == ContentType.WEBTOON) {
            webtoonRepository.findById(contentId).orElseThrow(
                    () -> new CustomException(ErrorCode.WEBTOON_NOT_FOUND)
            );
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
}
