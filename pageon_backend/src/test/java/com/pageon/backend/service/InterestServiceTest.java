package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.OAuthProvider;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("Interest 단위 테스트")
@ExtendWith(MockitoExtension.class)
class InterestServiceTest {
    @InjectMocks
    private InterestService interestService;
    @Mock
    private InterestRepository interestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebnovelRepository webnovelRepository;
    @Mock
    private WebtoonRepository webtoonRepository;
    
    @BeforeEach
    void setUp() {
        interestRepository.deleteAll();
    }
    
    @Test
    @DisplayName("웹소설 콘텐츠에 관심 등록 시 contentType이 WEBNOVEL로 저장된다.")
    void shouldRegisterLike_whenContentTypeIsWebnovel() {
        // given
        Long userId = 1L;
        ContentType contentType = ContentType.WEBNOVEL;
        Long webnovelId = 1L;
        
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

        Webnovel webnovel = Webnovel.builder()
                .id(webnovelId)
                .title("테스트")
                .description("테스트")
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();


        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelRepository.findById(webnovelId)).thenReturn(Optional.of(webnovel));

        ArgumentCaptor<Interest> likeCaptor = ArgumentCaptor.forClass(Interest.class);

        //when
        interestService.registerInterest(userId, contentType, webnovelId);
        
        // then
        verify(interestRepository).save(likeCaptor.capture());
        Interest like = likeCaptor.getValue();

        assertEquals(ContentType.WEBNOVEL, like.getContentType());
        assertEquals(webnovelId, like.getContentId());
        
    }

    @Test
    @DisplayName("웹툰 콘텐츠에 관심 등록 시 contentType이 WEBTOON로 저장된다.")
    void shouldRegisterLike_whenContentTypeIsWebtoon() {
        // given
        Long userId = 1L;
        ContentType contentType = ContentType.WEBTOON;
        Long webtoonId = 1L;

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

        Webtoon webtoon = Webtoon.builder()
                .id(webtoonId)
                .title("테스트")
                .description("테스트")
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();


        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonRepository.findById(webtoonId)).thenReturn(Optional.of(webtoon));

        ArgumentCaptor<Interest> likeCaptor = ArgumentCaptor.forClass(Interest.class);

        //when
        interestService.registerInterest(userId, contentType, webtoonId);

        // then
        verify(interestRepository).save(likeCaptor.capture());
        Interest like = likeCaptor.getValue();

        assertEquals(ContentType.WEBTOON, like.getContentType());
        assertEquals(webtoonId, like.getContentId());

    }

    
    @Test
    @DisplayName("존재하지 않는 웹소설 ID로 좋아요 등록 시 예외가 발생한다.")
    void shouldThrowException_whenWebnovelIdDoesNotExist() {
        // given
        Long userId = 1L;
        ContentType contentType = ContentType.WEBNOVEL;
        Long webnovelId = 2L;

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


        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelRepository.findById(webnovelId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            interestService.registerInterest(userId, contentType, webnovelId);
        });
        
        // then
        assertEquals("존재하지 않는 웹소설입니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.WEBNOVEL_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("존재하지 않는 웹툰 ID로 좋아요 등록 시 예외가 발생한다.")
    void shouldThrowException_whenWebtoonIdDoesNotExist() {
        // given
        Long userId = 1L;
        ContentType contentType = ContentType.WEBTOON;
        Long webtoonId = 2L;

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


        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonRepository.findById(webtoonId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            interestService.registerInterest(userId, contentType, webtoonId);
        });

        // then
        assertEquals("존재하지 않는 웹툰입니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.WEBTOON_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("콘텐츠 타입이 webtoon이나 webnovel이 아닐 경우 예외가 발생한다.")
    void shouldThrowException_whenContentTypeDoesNotWebnovelOrWebtoon() {
        // given
        Long userId = 1L;
        ContentType contentType = ContentType.valueOf(null);
        Long contentId = 1L;

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


        when(userRepository.getReferenceById(userId)).thenReturn(user);

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            interestService.registerInterest(userId, contentType, contentId);
        });

        // then
        assertEquals("지원하지 않는 콘텐츠 타입입니다. webnovel 또는 webtoon만 가능합니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.INVALID_CONTENT_TYPE, ErrorCode.valueOf(exception.getErrorCode()));
    }

    @Test
    @DisplayName("콘텐츠의 관심 삭제 버튼 클릭 시 DB에 존재하면 삭제한다.")
    void shouldDeleteInterest_whenUserIdContentIdAndContentTypeExist() {
        // given
        Long userId = 1L;
        ContentType contentType = ContentType.WEBNOVEL;
        Long contentId = 1L;

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

        Interest interest = Interest.builder()
                .id(1L)
                .contentId(contentId)
                .contentType(contentType)
                .user(user)
                .build();

        when(interestRepository.findByUser_IdAndContentTypeAndContentId(userId, contentType, contentId)).thenReturn(Optional.of(interest));

        //when
        interestService.deleteInterest(userId, contentType, contentId);

        // then
        verify(interestRepository, times(1)).delete(interest);

    }
    
    @Test
    @DisplayName("콘텐츠의 관심 삭제 버튼 클릭 시 DB에 존재하지 않으면 Exception 발생")
    void shouldThrowException_whenInterestDoesNotExist() {
        // given
        Long userId = 1L;
        ContentType contentType = ContentType.WEBNOVEL;
        Long contentId = 1L;

        when(interestRepository.findByUser_IdAndContentTypeAndContentId(userId, contentType, contentId)).thenReturn(Optional.empty());
        
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            interestService.deleteInterest(userId, contentType, contentId);
        });
        
        // then
        assertEquals("해당 사용자와 콘텐츠의 관심 정보가 존재하지 않습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.INTEREST_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));
        
    }
    
    @Test
    @DisplayName("로그인한 사용자의 관심 목록을 페이징으로 조회한다.")
    void shouldReturnInterestedContents_forLoggedInUser() {
        // given
        Long userId = 1L;

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

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

        Interest interestWebnovel = Interest.builder()
                .user(user)
                .contentType(ContentType.WEBNOVEL)
                .contentId(1L)
                .build();

        Interest interestWebtoon = Interest.builder()
                .user(user)
                .contentType(ContentType.WEBTOON)
                .contentId(3L)
                .build();

        when(userRepository.findByIdAndDeleted(userId, false)).thenReturn(Optional.of(user));
        when(interestRepository.findAllByUser_Id(eq(userId), eq(pageable))).thenReturn(new PageImpl<>(List.of(interestWebnovel, interestWebtoon), pageable, 2));

        //when
        Page<ContentSimpleResponse> result = interestService.getInterestedContents(userId, null, pageable);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals(interestWebnovel.getId(), result.getContent().get(0).getId());


    }

    @Test
    @DisplayName("contentType이 주어지면 해당 타입만 필터링하여 반환한다.")
    void shouldFilterByContentType_whenTypeProvided() {
        // given
        Long userId = 1L;

        Pageable pageable = PageRequest.of(0, 10);

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

        Interest interestWebnovel = Interest.builder()
                .user(user)
                .contentType(ContentType.WEBNOVEL)
                .contentId(1L)
                .build();


        when(userRepository.getReferenceById(userId)).thenReturn(user);

        when(interestRepository.findAllByUser_IdAndContentType(userId, ContentType.WEBNOVEL, pageable)).thenReturn(new PageImpl<>(List.of(interestWebnovel), pageable, 1));

        //when
        Page<ContentSimpleResponse> result = interestService.getInterestedContents(userId, ContentType.WEBNOVEL, pageable);

        // then
        assertEquals(1, result.getContent().size());
        assertEquals(ContentType.WEBNOVEL, result.getContent().get(0).getContentType());

    }

    @Test
    @DisplayName("로그인한 사용자의 관심 목록이 없으면 null 반환")
    void shouldReturnNull_whenNoInterestsExist() {
        // given
        Long userId = 1L;

        Pageable pageable = PageRequest.of(0, 10);

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

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(interestRepository.findAllByUser_Id(userId, pageable)).thenReturn(Page.empty(pageable));

        //when
        Page<ContentSimpleResponse> result = interestService.getInterestedContents(userId, null, pageable);

        // then
        assertNull(result);

    }

}