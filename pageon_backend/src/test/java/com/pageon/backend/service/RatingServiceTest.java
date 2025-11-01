package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.OAuthProvider;
import com.pageon.backend.dto.request.ContentEpisodeRatingRequest;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("RatingService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    @InjectMocks
    private RatingService ratingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebnovelRepository webnovelRepository;
    @Mock
    private WebtoonRepository webtoonRepository;
    @Mock
    private WebnovelEpisodeRepository webnovelEpisodeRepository;
    @Mock
    private WebtoonEpisodeRepository webtoonEpisodeRepository;
    @Mock
    private WebtoonEpisodeRatingRepository webtoonEpisodeRatingRepository;
    @Mock
    private WebnovelEpisodeRatingRepository webnovelEpisodeRatingRepository;

    @BeforeEach
    void setUp() {
        webnovelEpisodeRatingRepository.deleteAll();
        webtoonEpisodeRatingRepository.deleteAll();
    }

    @Test
    @DisplayName("웹소설 에피소드에 평점을 등록하면 성공적으로 저장된다.")
    void shouldSaveWebnovelRating_whenValidRequestProvided() {
        // given
        Long userId = 1L;
        Long episodeId = 100L;

        User user = User.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .oAuthProvider(OAuthProvider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .deleted(false)
                .build();

        Webnovel webnovel = Webnovel.builder().id(1L).title("고양이가 사라진 마을").deleted(false).build();

        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).episodeTitle("고양이").webnovel(webnovel).build();

        ContentEpisodeRatingRequest contentEpisodeRatingRequest = new ContentEpisodeRatingRequest(ContentType.WEBNOVEL, 100L, 5);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));

        ArgumentCaptor<WebnovelEpisodeRating> ratingCaptor = ArgumentCaptor.forClass(WebnovelEpisodeRating.class);

        //when
        ratingService.createWebnovelRating(userId, contentEpisodeRatingRequest);

        // then
        verify(webnovelEpisodeRatingRepository).save(ratingCaptor.capture());
        WebnovelEpisodeRating rating = ratingCaptor.getValue();

        assertEquals("고양이", rating.getWebnovelEpisode().getEpisodeTitle());
        assertEquals(5, rating.getScore());

    }

    @Test
    @DisplayName("웹툰 에피소드에 평점을 등록하면 성공적으로 저장된다.")
    void shouldSaveWebtoonRating_whenValidRequestProvided() {
        // given
        Long userId = 1L;
        Long episodeId = 100L;

        User user = User.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .oAuthProvider(OAuthProvider.EMAIL)
                .providerId(null)
                .pointBalance(0)
                .deleted(false)
                .build();

        Webtoon webtoon = Webtoon.builder().id(3L).title("해와 달의 유치원").deleted(false).build();

        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).episodeTitle("모두의 빛").webtoon(webtoon).build();

        ContentEpisodeRatingRequest contentEpisodeRatingRequest = new ContentEpisodeRatingRequest(ContentType.WEBTOON, 100L, 5);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        ArgumentCaptor<WebtoonEpisodeRating> ratingCaptor = ArgumentCaptor.forClass(WebtoonEpisodeRating.class);

        //when
        ratingService.createWebtoonRating(userId, contentEpisodeRatingRequest);

        // then
        verify(webtoonEpisodeRatingRepository).save(ratingCaptor.capture());
        WebtoonEpisodeRating rating = ratingCaptor.getValue();

        assertEquals("모두의 빛", rating.getWebtoonEpisode().getEpisodeTitle());
        assertEquals(5, rating.getScore());

    }

    @Test
    @DisplayName("존재하지 않는 웹소설 에피소드에 평점을 등록하면 예외가 발생한다.")
    void shouldThrowException_whenWebnovelEpisodeDoesNotExist() {
        // given
        Long episodeId = 10L;
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .deleted(false)
                .build();

        ContentEpisodeRatingRequest contentEpisodeRatingRequest = new ContentEpisodeRatingRequest(ContentType.WEBNOVEL, 100L, 5);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            ratingService.createWebnovelRating(userId, contentEpisodeRatingRequest);
        });

        // then
        assertEquals("해당 에피소드를 찾을 수 없습니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("존재하지 않는 웹툰 에피소드에 평점을 등록하면 예외가 발생한다.")
    void shouldThrowException_whenWebtoonEpisodeDoesNotExist_() {
        // given
        Long episodeId = 10L;
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .deleted(false)
                .build();

        ContentEpisodeRatingRequest contentEpisodeRatingRequest = new ContentEpisodeRatingRequest(ContentType.WEBTOON, 100L, 5);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            ratingService.createWebtoonRating(userId, contentEpisodeRatingRequest);
        });

        // then
        assertEquals("해당 에피소드를 찾을 수 없습니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("평점 등록 시 해당 에피소드와 콘텐츠의 평균 평점이 정상적으로 갱신된다.")
    void shouldUpdateAverageRating_whenNewRatingAdded() {
        // given
        Long episodeId = 10L;
        Long userId = 1L;
        Long contentId = 3L;
        int score = 5;


        User user = User.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .deleted(false)
                .build();

        Webtoon webtoon = Webtoon.builder().id(contentId).title("해와 달의 유치원").deleted(false).build();

        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).episodeTitle("모두의 빛").webtoon(webtoon).build();

        WebtoonEpisodeRating.builder().user(user).webtoonEpisode(webtoonEpisode).score(score).build();

        ContentEpisodeRatingRequest contentEpisodeRatingRequest = new ContentEpisodeRatingRequest(ContentType.WEBTOON, episodeId, score);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        ArgumentCaptor<WebtoonEpisodeRating> ratingCaptor = ArgumentCaptor.forClass(WebtoonEpisodeRating.class);

        //when
        ratingService.createWebtoonRating(userId, contentEpisodeRatingRequest);

        // then
        verify(webtoonEpisodeRatingRepository).save(ratingCaptor.capture());
        WebtoonEpisodeRating rating = ratingCaptor.getValue();
        
        // then
        assertEquals(5.0, rating.getWebtoonEpisode().getAverageRating());
        assertEquals(5.0, rating.getWebtoonEpisode().getWebtoon().getTotalAverageRating());
        assertEquals(1, rating.getWebtoonEpisode().getRatingCount());
        
    }

    @Test
    @DisplayName("이미 등록된 평점이 있을 때 새로운 평점을 입력하면 해당 웹소설 에피소드의 내 평점을 수정한다.")
    void shouldUpdateWebnovelRating_whenWebnovelEpisodeRatingExist() {
        // given
        int oldScore = 10;
        int newScore = 8;
        Long userId = 1L;
        Long episodeId = 10L;
        double averageRating = 5.0;
        long ratingCount = 10L;

        Double changeRating = ((averageRating * ratingCount) - oldScore + newScore) / ratingCount;

        User user = User.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .deleted(false)
                .build();
        Webnovel webnovel = Webnovel.builder().id(3L).title("고양이가 사라진 마을").totalRatingCount(ratingCount).deleted(false).build();

        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).episodeTitle("고양이").webnovel(webnovel).averageRating(averageRating).ratingCount(ratingCount).build();

        ContentEpisodeRatingRequest contentEpisodeRatingRequest = new ContentEpisodeRatingRequest(ContentType.WEBNOVEL, episodeId, newScore);



        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));

        when(webnovelEpisodeRatingRepository.findScoreByWebnovelEpisodeAndUser(episodeId, userId)).thenReturn(oldScore);


        //when
        ratingService.updateWebnovelRating(userId, contentEpisodeRatingRequest);

        // then
        assertEquals(changeRating, webnovelEpisode.getAverageRating());
        assertEquals(changeRating, webnovel.getTotalAverageRating());


    }

    @Test
    @DisplayName("이미 등록된 평점이 있을 때 새로운 평점을 입력하면 해당 웹툰 에피소드의 내 평점을 수정한다.")
    void shouldUpdateWebtoonRating_whenWebtoonEpisodeRatingExist() {
        // given
        int oldScore = 10;
        int newScore = 8;
        Long userId = 1L;
        Long episodeId = 10L;
        double averageRating = 5.0;
        long ratingCount = 10L;

        Double changeRating = ((averageRating * ratingCount) - oldScore + newScore) / ratingCount;

        User user = User.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .deleted(false)
                .build();

        Webtoon webtoon = Webtoon.builder().id(3L).title("해와 달의 유치원").totalAverageRating(averageRating).totalRatingCount(ratingCount).deleted(false).build();

        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).episodeTitle("모두의 빛").webtoon(webtoon).averageRating(averageRating).ratingCount(ratingCount).build();

        ContentEpisodeRatingRequest contentEpisodeRatingRequest = new ContentEpisodeRatingRequest(ContentType.WEBTOON, episodeId, newScore);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        when(webtoonEpisodeRatingRepository.findScoreByWebtoonEpisodeAndUser(episodeId, userId)).thenReturn(oldScore);


        //when
        ratingService.updateWebtoonRating(userId, contentEpisodeRatingRequest);

        // then
        assertEquals(changeRating, webtoonEpisode.getAverageRating());
        assertEquals(changeRating, webtoon.getTotalAverageRating());


    }

    @Test
    @DisplayName("해당 웹소설 에피소드에 저장된 평점이 없을 경우 예외가 발생한다.")
    void shouldThrowException_whenWebnovelEpisodeRatingNotExist() {
        // given
        int newScore = 8;
        Long userId = 1L;
        Long episodeId = 10L;
        
        User user = User.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .deleted(false)
                .build();
        Webnovel webnovel = Webnovel.builder().id(3L).title("고양이가 사라진 마을").deleted(false).build();

        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).episodeTitle("고양이").webnovel(webnovel).build();

        ContentEpisodeRatingRequest contentEpisodeRatingRequest = new ContentEpisodeRatingRequest(ContentType.WEBNOVEL, episodeId, newScore);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));
        when(webnovelEpisodeRatingRepository.findByWebnovelEpisodeAndUser(episodeId, userId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            ratingService.updateWebnovelRating(userId, contentEpisodeRatingRequest);
        });

        // then
        assertEquals("해당 에피소드에 저장된 평점이 없습니다.", exception.getMessage());
        assertEquals(ErrorCode.EPISODE_RATING_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));


    }

    @Test
    @DisplayName("해당 웹툰 에피소드에 저장된 평점이 없을 경우 예외가 발생한다.")
    void shouldThrowException_whenWebtoonEpisodeRatingNotExist() {
        // given
        int newScore = 8;
        Long userId = 1L;
        Long episodeId = 10L;

        User user = User.builder()
                .id(userId)
                .email("test@mail.com")
                .password("password")
                .nickname("nickname")
                .deleted(false)
                .build();

        Webtoon webtoon = Webtoon.builder().id(3L).title("해와 달의 유치원").deleted(false).build();

        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).episodeTitle("모두의 빛").webtoon(webtoon).build();

        ContentEpisodeRatingRequest contentEpisodeRatingRequest = new ContentEpisodeRatingRequest(ContentType.WEBTOON, episodeId, newScore);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        when(webtoonEpisodeRatingRepository.findByWebtoonEpisodeAndUser(episodeId, userId)).thenReturn(Optional.empty());


        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            ratingService.updateWebtoonRating(userId, contentEpisodeRatingRequest);
        });

        // then
        assertEquals("해당 에피소드에 저장된 평점이 없습니다.", exception.getMessage());
        assertEquals(ErrorCode.EPISODE_RATING_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }


}