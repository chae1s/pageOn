package com.pageon.backend.service;

import com.pageon.backend.dto.request.EpisodeRatingRequest;
import com.pageon.backend.entity.User;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.service.provider.ContentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EpisodeService {
    private final List<ContentProvider> providers;
    private final UserRepository userRepository;

    @Transactional
    public Object getEpisodeDetail(Long userId, String contentType, Long episodeId) {
        ContentProvider provider = getProvider(contentType);

        return provider.findEpisodeDetail(userId, episodeId);
    }

    @Transactional
    public void rateEpisode(Long userId, String contentType, Long episodeId, EpisodeRatingRequest request) throws CustomException {

        final Integer score = request.getScore();
        User user = userRepository.getReferenceById(userId);

        ContentProvider provider = getProvider(contentType);
        provider.rateEpisode(user, episodeId, score);
    }

    @Transactional
    public void updateEpisodeRating(Long userId, String contentType, Long commentId, EpisodeRatingRequest request) {

        final Integer newScore = request.getScore();

        ContentProvider provider = getProvider(contentType);
        provider.updateEpisodeRating(userId, commentId, newScore);

    }


    private ContentProvider getProvider(String contentType) {
        return providers.stream()
                .filter(p -> p.supports(contentType))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CONTENT_TYPE));
    }
}
