package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.dto.response.ContentSimpleResponse;
import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.dto.response.UserKeywordResponse;
import com.pageon.backend.dto.response.UserWebnovelResponse;
import com.pageon.backend.entity.Category;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebnovelRepository;
import com.pageon.backend.security.PrincipalUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("UserWebnovelService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserWebnovelServiceTest {
    @InjectMocks
    private UserWebnovelService userWebnovelService;
    @Mock
    private WebnovelRepository webnovelRepository;
    @Mock
    private KeywordService keywordService;
    private PrincipalUser mockPrincipalUser;

    @BeforeEach
    void setUp() {
        webnovelRepository.deleteAll();
        mockPrincipalUser = mock(PrincipalUser.class);
    }

    @Test
    @DisplayName("DB에서 webnovelId로 가져온 웹소설 정보를 return")
    void getWebnovelById_shouldReturnWebnovel() {
        // given
        Creator creator = Creator.builder()
                .id(1L)
                .penName("필명")
                .contentType(ContentType.WEBNOVEL)
                .agreedToAiPolicy(true)
                .aiPolicyAgreedAt(LocalDateTime.now())
                .build();

        List<Keyword> kewords = createKeywords("하나,둘,셋,넷");

        Webnovel webnovel = Webnovel.builder()
                .id(1L)
                .title("테스트")
                .description("테스트")
                .creator(creator)
                .keywords(kewords)
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();
        List<UserKeywordResponse> userKeywordResponses = createUserKeywords(kewords);

        doReturn(userKeywordResponses).when(keywordService).getKeywordsExceptCategory(kewords);
        when(webnovelRepository.findByIdAndDeleted(1L, false)).thenReturn(Optional.of(webnovel));
        //when
        UserWebnovelResponse response = userWebnovelService.getWebnovelById(1L, mockPrincipalUser);

        // then
        assertEquals(webnovel.getId(), response.getId());
        assertEquals(webnovel.getTitle(), response.getTitle());
        assertEquals(webnovel.getCreator().getPenName(), response.getAuthor());

    }

    @Test
    @DisplayName("DB에 존재하지 않는 웹소설일 경우 CustomException 발생")
    void getWebnovelById_whenInvalidWebnovelId_shouldThrowCustomException() {
        // given
        when(webnovelRepository.findByIdAndDeleted(1L, false)).thenReturn(Optional.empty());

        //when
        CustomException exception =  assertThrows(CustomException.class, () -> {
            userWebnovelService.getWebnovelById(1L, mockPrincipalUser);
        });

        // then
        assertEquals("존재하지 않는 웹소설입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.WEBNOVEL_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));
    }

    @Test
    @DisplayName("웹소설 목록을 조회할 때 isDeleted가 false 인 것만 담긴다.")
    void getWebnovels_shouldWebnovelListIsDeletedFalse() {
        // given
        List<Webnovel> webnovels = new ArrayList<>();

        Creator creator = Creator.builder()
                .id(1L)
                .penName("필명")
                .contentType(ContentType.WEBNOVEL)
                .agreedToAiPolicy(true)
                .aiPolicyAgreedAt(LocalDateTime.now())
                .build();

        List<Keyword> kewords = createKeywords("하나,둘,셋,넷");

        Webnovel webnovel1 = Webnovel.builder()
                .id(1L)
                .title("테스트")
                .description("테스트")
                .creator(creator)
                .keywords(kewords)
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();

        Webnovel webnovel2 = Webnovel.builder()
                .id(2L)
                .title("테스트2")
                .description("테스트2")
                .creator(creator)
                .keywords(kewords)
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();

        webnovels.add(webnovel1);
        webnovels.add(webnovel2);
        List<UserKeywordResponse> userKeywordResponses = createUserKeywords(kewords);

        doReturn(userKeywordResponses).when(keywordService).getKeywordsExceptCategory(kewords);
        when(webnovelRepository.findByDeleted(false)).thenReturn(webnovels);
        //when
        List<UserContentListResponse> listResponses = userWebnovelService.getWebnovels();

        // then
        assertEquals(webnovels.size(), listResponses.size());
        assertEquals(webnovels.get(0).getTitle(), listResponses.get(0).getTitle());

    }
    
    @Test
    @DisplayName("주어진 요일의 조회수 상위 18개의 작품을 return")
    void getWebnovelsByDay_WhenGivenDay_shouldReturnTop18Webnovels() {
        // given
        String serialDay = "MONDAY";
        List<Webnovel> webnovels = createMockWebnovels();
        Pageable pageable = PageRequest.of(0, 18);
        when(webnovelRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable)).thenReturn(webnovels.subList(0, 18));

        //when
        List<ContentSimpleResponse> result = userWebnovelService.getWebnovelsByDay(serialDay);
        
        // then
        assertEquals(18, result.size());
        assertEquals("소설1", result.get(0).getTitle());
        assertEquals("소설18", result.get(17).getTitle());
        
    }


    @Test
    @DisplayName("주어진 요일에 해당하는 작품이 없을 경우 빈 리스트를 반환")
    void getWebnovelsByDay_whenNoWebnovelsExistForGivenDay_shouldReturnEmptyList() {
        // given
        String serialDay = "FRIDAY";
        List<Webnovel> webnovels = createMockWebnovels();
        Pageable pageable = PageRequest.of(0, 18);
        when(webnovelRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable)).thenReturn(Collections.emptyList());
        //when
        List<ContentSimpleResponse> result = userWebnovelService.getWebnovelsByDay(serialDay);

        // then
        assertEquals(0, result.size());

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

    private List<Webnovel> createMockWebnovels() {
        Creator creator = Creator.builder()
                .id(1L)
                .penName("필명")
                .contentType(ContentType.WEBNOVEL)
                .agreedToAiPolicy(true)
                .aiPolicyAgreedAt(LocalDateTime.now())
                .build();
        
        return List.of(
                new Webnovel(1L, "소설1", creator, SerialDay.MONDAY, 15000L),
                new Webnovel(2L, "소설2", creator, SerialDay.MONDAY, 14000L),
                new Webnovel(3L, "소설3", creator, SerialDay.MONDAY, 13500L),
                new Webnovel(4L, "소설4", creator, SerialDay.MONDAY, 13000L),
                new Webnovel(5L, "소설5", creator, SerialDay.MONDAY, 12800L),
                new Webnovel(6L, "소설6", creator, SerialDay.MONDAY, 12500L),
                new Webnovel(7L, "소설7", creator, SerialDay.MONDAY, 12000L),
                new Webnovel(8L, "소설8", creator, SerialDay.MONDAY, 11800L),
                new Webnovel(9L, "소설9", creator, SerialDay.MONDAY, 11500L),
                new Webnovel(10L, "소설10", creator, SerialDay.MONDAY, 11200L),
                new Webnovel(11L, "소설11", creator, SerialDay.MONDAY, 11000L),
                new Webnovel(12L, "소설12", creator, SerialDay.MONDAY, 10800L),
                new Webnovel(13L, "소설13", creator, SerialDay.MONDAY, 10500L),
                new Webnovel(14L, "소설14", creator, SerialDay.MONDAY, 10200L),
                new Webnovel(15L, "소설15", creator, SerialDay.MONDAY, 10000L),
                new Webnovel(16L, "소설16", creator, SerialDay.MONDAY, 9800L),
                new Webnovel(17L, "소설17", creator, SerialDay.MONDAY, 9500L),
                new Webnovel(18L, "소설18", creator, SerialDay.MONDAY, 9200L),

                // 18개 이상을 확인하기 위해 추가
                new Webnovel(19L, "소설19", creator, SerialDay.MONDAY, 9000L),
                new Webnovel(20L, "소설20", creator, SerialDay.MONDAY, 8800L),
                // 다른 요일
                new Webnovel(19L, "소설21", creator, SerialDay.TUESDAY, 9000L),
                new Webnovel(20L, "소설22", creator, SerialDay.TUESDAY, 8800L)

        );

    }

}
