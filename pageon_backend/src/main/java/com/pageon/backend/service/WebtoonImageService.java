package com.pageon.backend.service;

import com.pageon.backend.dto.response.WebtoonImagesResponse;
import com.pageon.backend.entity.WebtoonImage;
import com.pageon.backend.repository.WebtoonImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebtoonImageService {

    private final WebtoonImageRepository webtoonImageRepository;
    private final CloudFrontSignerService cloudFrontSignerService;

    public List<WebtoonImagesResponse> getWebtoonImages(Long episodeId) {
        List<WebtoonImagesResponse> webtoonImagesResponses = new ArrayList<>();

        List<WebtoonImage> webtoonImages = webtoonImageRepository.findByWebtoonEpisodeIdOrderBySequenceAsc(episodeId);

        for (WebtoonImage webtoonImage : webtoonImages) {
            webtoonImagesResponses.add(WebtoonImagesResponse.fromEntity(webtoonImage, cloudFrontSignerService.signUrl(webtoonImage.getImageUrl())));
        }

        return webtoonImagesResponses;
    }



}
