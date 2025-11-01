package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.dto.response.WebnovelEpisodeDetailResponse;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebnovelEpisodeRepository;
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
@DisplayName("WebnovelEpisodeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class WebnovelEpisodeServiceTest {
    @InjectMocks
    private WebnovelEpisodeService webnovelEpisodeService;
    @Mock
    private WebnovelEpisodeRepository webnovelEpisodeRepository;

    @BeforeEach
    void setUp() {
        webnovelEpisodeRepository.deleteAll();
    }

    @Test
    @DisplayName("webnovelId로 episode 리스트를 return")
    void getEpisodesByWebnovelId_withWebnovelId_shouldReturnWebnovelEpisodes() {
        // given
        List<WebnovelEpisode> webnovelEpisodes = createMockWebnovelEpisodes();

        when(webnovelEpisodeRepository.findByWebnovelId(1L)).thenReturn(webnovelEpisodes);

        //when
        List<EpisodeListResponse> results = webnovelEpisodeService.getEpisodesByWebnovelId(1L);

        // then

        assertEquals(webnovelEpisodes.size(), results.size());
        assertEquals(webnovelEpisodes.get(0).getEpisodeNum(), results.get(0).getEpisodeNum());
    }

    @Test
    @DisplayName("DB에서 webnovelEpisodeId로 가져온 웹소설 에피소드 정보를 return")
    void getWebnovelEpisodeById_shouldReturnWebnovelEpisode() {
        // given
        Webnovel webnovel = Webnovel.builder()
                .id(1L)
                .title("테스트")
                .description("테스트")
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();

        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder()
                .id(1L)
                .webnovel(webnovel)
                .episodeNum(1)
                .episodeTitle("웹소설")
                .content("웹소설 내용")
                .purchasePrice(100)
                .build();

        when(webnovelEpisodeRepository.findByIdWithWebnovel(1L)).thenReturn(Optional.of(webnovelEpisode));
        //when
        WebnovelEpisodeDetailResponse result = webnovelEpisodeService.getWebnovelEpisodeById(1L, 1L);

        // then
        assertEquals(webnovelEpisode.getEpisodeTitle(), result.getEpisodeTitle());
        assertEquals(webnovelEpisode.getEpisodeNum(), result.getEpisodeNum());
        assertEquals(webnovelEpisode.getContent(), result.getContent());

    }

    @Test
    @DisplayName("DB에 존재하지 않는 에피소드일 경우 CustomException 발생")
    void getWebnovelEpisodeById_whenInvalidWebnovelEpisodeId_shouldThrowCustomException() {
        // given
        when(webnovelEpisodeRepository.findByIdWithWebnovel(1L)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeService.getWebnovelEpisodeById(1L, 1L);
        });

        // then
        assertEquals("해당 에피소드를 찾을 수 없습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    private List<WebnovelEpisode> createMockWebnovelEpisodes() {
        Creator creator = Creator.builder()
                .id(1L)
                .penName("필명")
                .contentType(ContentType.WEBNOVEL)
                .agreedToAiPolicy(true)
                .aiPolicyAgreedAt(LocalDateTime.now())
                .build();

        Webnovel webnovel = Webnovel.builder()
                .id(1L)
                .title("테스트")
                .description("테스트")
                .creator(creator)
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();

        List<WebnovelEpisode> episodes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            episodes.add(
                    new WebnovelEpisode((long) i, webnovel, i, "웹소설 제목 " + i,"웹소설 내용", null, 100, 10.0, 1L )
            );
        }

        return episodes;
    }

}