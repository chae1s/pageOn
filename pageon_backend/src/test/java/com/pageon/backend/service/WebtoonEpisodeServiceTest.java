package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.entity.*;
import com.pageon.backend.repository.WebtoonEpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("WebtoonEpisodeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class WebtoonEpisodeServiceTest {
    @InjectMocks
    private WebtoonEpisodeService webtoonEpisodeService;
    @Mock
    private WebtoonEpisodeRepository webtoonEpisodeRepository;

    @BeforeEach
    void setUp() {
        webtoonEpisodeRepository.deleteAll();
    }

    @Test
    @DisplayName("webtoonId로 episode 리스트를 return")
    void getEpisodesByWebtoonId_withWebtoonId_shouldReturnWebtoonEpisodes() {
        // given
        List<WebtoonEpisode> webtoonEpisodes = createMockWebtoonEpisodes();

        when(webtoonEpisodeRepository.findByWebtoonId(1L)).thenReturn(webtoonEpisodes);

        //when
        List<EpisodeListResponse> results = webtoonEpisodeService.getEpisodesByWebtoonId(1L);

        // then

        assertEquals(webtoonEpisodes.size(), results.size());
        assertEquals(webtoonEpisodes.get(0).getEpisodeNum(), results.get(0).getEpisodeNum());
    }

    private List<WebtoonEpisode> createMockWebtoonEpisodes() {
        Creator creator = Creator.builder()
                .id(1L)
                .penName("필명")
                .contentType(ContentType.WEBTOON)
                .agreedToAiPolicy(true)
                .aiPolicyAgreedAt(LocalDateTime.now())
                .build();

        Webtoon webtoon = Webtoon.builder()
                .id(1L)
                .title("테스트")
                .description("테스트")
                .creator(creator)
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();

        List<WebtoonImage> images = new ArrayList<>();
        List<WebtoonEpisode> episodes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            episodes.add(
                    new WebtoonEpisode((long) i, webtoon, i, "웹툰 제목 " + i, images, 300, 500)
            );
        }

        return episodes;
    }

}