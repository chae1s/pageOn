package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.dto.response.UserKeywordResponse;
import com.pageon.backend.dto.response.UserWebnovelResponse;
import com.pageon.backend.dto.response.UserWebtoonResponse;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebtoonRepository;
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
@DisplayName("UserWebtoonService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserWebtoonServiceTest {
    @InjectMocks
    private UserWebtoonService userWebtoonService;
    @Mock
    private WebtoonRepository webtoonRepository;
    @Mock
    private KeywordService keywordService;

    @BeforeEach
    void setUp() {
        webtoonRepository.deleteAll();
    }

    @Test
    @DisplayName("DB에서 webtoonId로 가져온 웹툰 정보를 return")
    void getWebtoonById_shouldReturnWebtoon() {
        // given
        Creator creator = Creator.builder()
                .id(1L)
                .penName("필명")
                .contentType(ContentType.WEBTOON)
                .agreedToAiPolicy(true)
                .aiPolicyAgreedAt(LocalDateTime.now())
                .build();

        List<Keyword> kewords = createKeywords("하나,둘,셋,넷");

        Webtoon webtoon = Webtoon.builder()
                .id(1L)
                .title("테스트")
                .description("테스트")
                .creator(creator)
                .keywords(kewords)
                .serialDay(DayOfWeek.MONDAY)
                .status(SeriesStatus.ONGOING)
                .isDeleted(false)
                .build();

        List<UserKeywordResponse> userKeywordResponses = createUserKeywords(kewords);

        doReturn(userKeywordResponses).when(keywordService).getKeywordsExceptCategory(kewords);
        when(webtoonRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(webtoon));
        //when
        UserWebtoonResponse response = userWebtoonService.getWebtoonById(1L);

        // then
        assertEquals(webtoon.getId(), response.getId());
        assertEquals(webtoon.getTitle(), response.getTitle());
        assertEquals(webtoon.getCreator().getPenName(), response.getPenName());

    }

    @Test
    @DisplayName("DB에 존재하지 않는 웹툰일 경우 CustomException 발생")
    void getWebnovelById_whenInvalidWebnovelId_shouldThrowCustomException() {
        // given
        when(webtoonRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        //when
        CustomException exception =  assertThrows(CustomException.class, () -> {
            userWebtoonService.getWebtoonById(1L);
        });

        // then
        assertEquals("존재하지 않는 웹툰입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.WEBTOON_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));
    }

    @Test
    @DisplayName("웹툰 목록을 조회할 때 isDeleted가 false 인 것만 담긴다.")
    void getWebnovels_shouldWebnovelListIsDeletedFalse() {
        // given
        List<Webtoon> webtoons = new ArrayList<>();

        Creator creator = Creator.builder()
                .id(1L)
                .penName("필명")
                .contentType(ContentType.WEBTOON)
                .agreedToAiPolicy(true)
                .aiPolicyAgreedAt(LocalDateTime.now())
                .build();

        List<Keyword> kewords = createKeywords("하나,둘,셋,넷");

        Webtoon webtoon1 = Webtoon.builder()
                .id(1L)
                .title("테스트")
                .description("테스트")
                .creator(creator)
                .keywords(kewords)
                .serialDay(DayOfWeek.MONDAY)
                .status(SeriesStatus.ONGOING)
                .isDeleted(false)
                .build();

        Webtoon webtoon2 = Webtoon.builder()
                .id(2L)
                .title("테스트2")
                .description("테스트2")
                .creator(creator)
                .keywords(kewords)
                .serialDay(DayOfWeek.MONDAY)
                .status(SeriesStatus.ONGOING)
                .isDeleted(false)
                .build();

        webtoons.add(webtoon1);
        webtoons.add(webtoon2);
        List<UserKeywordResponse> userKeywordResponses = createUserKeywords(kewords);

        doReturn(userKeywordResponses).when(keywordService).getKeywordsExceptCategory(kewords);
        when(webtoonRepository.findByIsDeletedFalse()).thenReturn(webtoons);
        //when
        List<UserContentListResponse> listResponses = userWebtoonService.getWebtoons();

        // then
        assertEquals(webtoons.size(), listResponses.size());
        assertEquals(webtoons.get(0).getTitle(), listResponses.get(0).getTitle());

    }

    private List<Keyword> createKeywords(String s) {
        Category category = Category.builder()
                .id(1L)
                .name("카테고리")
                .build();

        List<Keyword> keywords = new ArrayList<>();
        String[] words = s.replaceAll("\\s", "").split(",");
        for (int i = 0; i < words.length; i++) {
            Keyword keyword = Keyword.builder()
                    .id(i + 1L)
                    .category(category)
                    .name(words[i])
                    .build();

            keywords.add(keyword);
        }

        return keywords;
    }

    private List<UserKeywordResponse> createUserKeywords(List<Keyword> keywords) {
        List<UserKeywordResponse> userKeywords = new ArrayList<>();
        for (Keyword keyword : keywords) {
            if (keyword.getCategory().getId().equals(6L)) {
                userKeywords.add(UserKeywordResponse.fromEntity(keyword));
            }
        }
        return userKeywords;
    }

}