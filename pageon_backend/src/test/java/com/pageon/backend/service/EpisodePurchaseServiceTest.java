package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.PurchaseType;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("EpisodePurchaseService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class EpisodePurchaseServiceTest {
    @InjectMocks
    private EpisodePurchaseService episodePurchaseService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebtoonRepository webtoonRepository;
    @Mock
    private WebnovelRepository webnovelRepository;
    @Mock
    private WebtoonEpisodeRepository webtoonEpisodeRepository;
    @Mock
    private WebnovelEpisodeRepository webnovelEpisodeRepository;
    @Mock
    private EpisodePurchaseRepository episodePurchaseRepository;

    @BeforeEach
    void setUp() {
        episodePurchaseRepository.deleteAll();
    }
    
    @Test
    @DisplayName("에피소드를 구매하면 정상적으로 구매 내역이 저장된다.")
    void shouldSavePurchaseRecord_whenUserBuysEpisode() {
        // given
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;

        ContentType contentType = ContentType.WEBNOVEL;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(1000).build();
        Webnovel webnovel = Webnovel.builder().id(contentId).title("test title").build();
        WebnovelEpisode episode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("test episode title").purchasePrice(100).build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(episode));
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.empty());

        ArgumentCaptor<EpisodePurchase> purchaseCaptor = ArgumentCaptor.forClass(EpisodePurchase.class);
        
        //when
        episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.OWN);
        
        // then
        verify(episodePurchaseRepository).save(purchaseCaptor.capture());
        EpisodePurchase episodePurchase = purchaseCaptor.getValue();


        assertEquals(PurchaseType.OWN, episodePurchase.getPurchaseType());
        assertNull(episodePurchase.getExpiredAt());
        
        
    }

    @Test
    @DisplayName("이미 구매한 에피소드를 다시 구매하려 하면 예외가 발생한다.")
    void shouldThrowException_whenBuyingAlreadyPurchasedEpisode() {
        // given
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;
        Long purchaseId = 200L;

        ContentType contentType = ContentType.WEBNOVEL;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(1000).build();
        Webnovel webnovel = Webnovel.builder().id(contentId).title("test title").build();
        WebnovelEpisode episode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("test episode title").purchasePrice(100).build();

        EpisodePurchase episodePurchase = EpisodePurchase.builder().id(purchaseId).contentType(contentType).contentId(contentId).episodeId(episodeId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(episode));
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.of(episodePurchase));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.OWN);
        });

        // then
        assertEquals("이미 구매한 에피소드입니다.", exception.getMessage());
        assertEquals(ErrorCode.EPISODE_ALREADY_PURCHASE, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("포인트가 부족하면 에피소드 구매 시도 시 예외가 발생한다.")
    void shouldThrowException_whenUserHasInsufficientPointToBuy_() {
        // given
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;

        ContentType contentType = ContentType.WEBNOVEL;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(10).build();
        Webnovel webnovel = Webnovel.builder().id(contentId).title("test title").build();
        WebnovelEpisode episode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("test episode title").purchasePrice(100).build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(episode));
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.OWN);
        });

        // then
        assertEquals("포인트가 부족합니다.", exception.getMessage());
        assertEquals(ErrorCode.INSUFFICIENT_POINTS, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("존재하지 않는 콘텐츠를 구매하려 하면 예외가 발생한다.")
    void shouldThrowException_whenBuyingNonexistentContent() {
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;

        ContentType contentType = ContentType.WEBNOVEL;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(10).build();

        WebnovelEpisode episode = WebnovelEpisode.builder().id(episodeId).webnovel(null).episodeTitle("test episode title").purchasePrice(100).build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(episode));
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.OWN);
        });

        // then
        assertEquals("존재하지 않는 웹소설입니다.", exception.getMessage());
        assertEquals(ErrorCode.WEBNOVEL_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("삭제된 콘텐츠의 에피소드를 구매하려 하면 예외가 발생한다.")
    void shouldThrowException_whenBuyingEpisodeOfDeletedContent() {
        // given
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;

        ContentType contentType = ContentType.WEBNOVEL;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(10).build();
        Webnovel webnovel = Webnovel.builder().id(contentId).title("test title").deletedAt(LocalDateTime.now()).build();
        WebnovelEpisode episode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("test episode title").purchasePrice(100).build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(episode));
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.OWN);
        });

        // then
        assertEquals("삭제된 콘텐츠입니다.", exception.getMessage());
        assertEquals(ErrorCode.CONTENT_IS_DELETED, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("존재하지 않는 에피소드를 구매하려 하면 예외가 발생한다.")
    void shouldThrowException_whenBuyingNonexistentEpisode() {
        // given
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;

        ContentType contentType = ContentType.WEBNOVEL;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(10).build();
        Webnovel webnovel = Webnovel.builder().id(contentId).title("test title").deletedAt(LocalDateTime.now()).build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webnovelEpisodeRepository.findById(episodeId)).thenReturn(Optional.empty());
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.OWN);
        });

        // then
        assertEquals("해당 에피소드를 찾을 수 없습니다.", exception.getMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("삭제된 에피소드를 구매하려 하면 예외가 발생한다.")
    void shouldThrowException_whenBuyingDeletedEpisode() {
        // given
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;

        ContentType contentType = ContentType.WEBNOVEL;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(10).build();
        Webnovel webnovel = Webnovel.builder().id(contentId).title("test title").build();
        WebnovelEpisode episode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("test episode title").purchasePrice(100).deletedAt(LocalDateTime.now()).build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(episode));
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.OWN);
        });

        // then
        assertEquals("삭제된 에피소드입니다.", exception.getMessage());
        assertEquals(ErrorCode.EPISODE_IS_DELETED, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("에피소드를 대여하면 정상적으로 대여 내역이 저장된다.")
    void shouldSaveRentalRecord_whenUserRentsEpisode() {
        // given
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;
        Long purchaseId = 200L;
        ContentType contentType = ContentType.WEBTOON;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(10).build();
        Webtoon webtoon = Webtoon.builder().id(contentId).title("test title").build();
        WebtoonEpisode episode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("test episode title").purchasePrice(100).build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(episode));
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.empty());

        ArgumentCaptor<EpisodePurchase> purchaseCaptor = ArgumentCaptor.forClass(EpisodePurchase.class);

        //when
        episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.RENT);

        // then
        verify(episodePurchaseRepository).save(purchaseCaptor.capture());
        EpisodePurchase purchaseCaptorValue = purchaseCaptor.getValue();


        assertEquals(PurchaseType.RENT, purchaseCaptorValue.getPurchaseType());
        assertNotNull(purchaseCaptorValue.getExpiredAt());


    }

    @Test
    @DisplayName("이미 대여한 에피소드를 다시 대여하려 하면 예외가 발생한다.")
    void shouldThrowException_whenRentingAlreadyRentedEpisode() {
        // given
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;
        Long purchaseId = 200L;
        ContentType contentType = ContentType.WEBTOON;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(10).build();
        Webtoon webtoon = Webtoon.builder().id(contentId).title("test title").build();
        WebtoonEpisode episode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("test episode title").purchasePrice(100).build();


        LocalDateTime expiredAt = LocalDateTime.now().plusDays(1);

        EpisodePurchase episodePurchase = EpisodePurchase.builder().id(purchaseId).contentType(contentType).contentId(contentId).episodeId(episodeId).expiredAt(expiredAt).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(episode));
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.of(episodePurchase));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.RENT);
        });

        // then
        assertEquals("이미 대여한 에피소드입니다.", exception.getMessage());
        assertEquals(ErrorCode.EPISODE_ALREADY_RENTAL, ErrorCode.valueOf(exception.getMessage()));

    }

    @Test
    @DisplayName("대여 기간이 만료된 에피소드는 다시 대여할 수 있다.")
    void shouldAllowRerent_whenRentalPeriodExpired() {
        // given
        Long userId = 1L;
        Long contentId = 10L;
        Long episodeId = 100L;
        Long purchaseId = 200L;
        ContentType contentType = ContentType.WEBTOON;

        User user = User.builder().id(userId).email("test@mail.com").pointBalance(10).build();
        Webtoon webtoon = Webtoon.builder().id(contentId).title("test title").build();
        WebtoonEpisode episode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("test episode title").purchasePrice(100).build();

        LocalDateTime expiredAt = LocalDateTime.now().minusDays(1);

        EpisodePurchase episodePurchase = EpisodePurchase.builder().id(purchaseId).contentType(contentType).contentId(contentId).episodeId(episodeId).expiredAt(expiredAt).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(episode));
        when(episodePurchaseRepository.findByUser_IdAndContentTypeAndEpisodeId(userId, contentType, episodeId)).thenReturn(Optional.of(episodePurchase));



        //when
        episodePurchaseService.createPurchaseHistory(userId, contentType, episodeId, PurchaseType.RENT);

        // then
        assertEquals(PurchaseType.RENT, episodePurchase.getPurchaseType());
        assertNotEquals(expiredAt, episodePurchase.getExpiredAt());


    }

}