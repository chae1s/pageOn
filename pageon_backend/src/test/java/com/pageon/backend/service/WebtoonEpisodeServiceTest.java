package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.dto.response.WebtoonEpisodeDetailResponse;
import com.pageon.backend.dto.response.WebtoonImagesResponse;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
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
import java.util.Optional;

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
    @Mock
    private WebtoonImageService webtoonImageService;

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

    @Test
    @DisplayName("DB에서 webtoonEpisodeId로 가져온 웹툰 에피소드 정보를 return")
    void getWebtoonEpisodeById_shouldReturnWebtoonEpisode() {
        // given
        Webtoon webtoon = Webtoon.builder()
                .id(1L)
                .title("테스트")
                .description("test")
                .serialDay(SerialDay.SATURDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();

        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder()
                .id(1L)
                .webtoon(webtoon)
                .episodeNum(1)
                .episodeTitle("웹툰")
                .rentalPrice(100)
                .purchasePrice(300)
                .build();

        List<WebtoonImagesResponse> webtoonImages = createMockWebtoonImages(webtoonEpisode);

        doReturn(webtoonImages).when(webtoonImageService).getWebtoonImages(webtoonEpisode.getId());

        when(webtoonEpisodeRepository.findById(1L)).thenReturn(Optional.of(webtoonEpisode));

        //when
        WebtoonEpisodeDetailResponse result = webtoonEpisodeService.getWebtoonEpisodeById(1L, 1L);


        // then
        assertEquals(webtoonEpisode.getEpisodeTitle(), result.getTitle());
        assertEquals(webtoonImages.size(), result.getImages().size());

    }

    @Test
    @DisplayName("DB에 존재하지 않는 에피소드일 경우 CustomException 발생")
    void getWebtoonEpisodeById_whenInvalidWebtoonEpisodeId_shouldThrowCustomException() {

        // given
        when(webtoonEpisodeRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> webtoonEpisodeService.getWebtoonEpisodeById(1L, 1L));

        // then
        assertEquals("해당 에피소드를 찾을 수 없습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

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
                    new WebtoonEpisode((long) i, webtoon, i, "웹툰 제목 " + i, images, null, 300, 500, 10.0, 1L)
            );
        }

        return episodes;
    }

    private List<WebtoonImagesResponse> createMockWebtoonImages(WebtoonEpisode webtoonEpisode) {

        List<WebtoonImagesResponse> images = new ArrayList<>();

        String signUrl = "https://cdn.test.example.com";

        for (int i = 1; i <= 10; i++) {
            WebtoonImage image = new WebtoonImage((long)i, i, "url" + i, webtoonEpisode);
            images.add(WebtoonImagesResponse.fromEntity(image, signUrl));
        }

        return images;
    }

}