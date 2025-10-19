package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.entity.Like;
import com.pageon.backend.entity.User;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.LikeRepository;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebnovelRepository;
import com.pageon.backend.repository.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final WebnovelRepository webnovelRepository;
    private final WebtoonRepository webtoonRepository;

    public void registerLike(Long userId, Long contentId, ContentType contentType) {
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

        Like like = Like.builder()
                .user(user)
                .contentType(contentType)
                .contentId(contentId)
                .build();

        likeRepository.save(like);


    }
}
