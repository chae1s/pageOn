package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.common.enums.SeriesStatus;
import com.pageon.backend.dto.response.*;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebtoonRepository;
import com.pageon.backend.security.PrincipalUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
@DisplayName("UserWebtoonService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserWebtoonServiceTest {
    @InjectMocks
    private UserWebtoonService userWebtoonService;
    @Mock
    private WebtoonRepository webtoonRepository;
    @Mock
    private KeywordService keywordService;

    private PrincipalUser mockPrincipalUser;

    @BeforeEach
    void setUp() {
        webtoonRepository.deleteAll();
        mockPrincipalUser = mock(PrincipalUser.class);
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
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();

        List<UserKeywordResponse> userKeywordResponses = createUserKeywords(kewords);

        doReturn(userKeywordResponses).when(keywordService).getKeywordsExceptCategory(kewords);
        when(webtoonRepository.findByIdAndDeleted(1L, false)).thenReturn(Optional.of(webtoon));
        //when
        UserWebtoonResponse response = userWebtoonService.getWebtoonById(1L, mockPrincipalUser);

        // then
        assertEquals(webtoon.getId(), response.getId());
        assertEquals(webtoon.getTitle(), response.getTitle());
        assertEquals(webtoon.getCreator().getPenName(), response.getAuthor());

    }

    @Test
    @DisplayName("DB에 존재하지 않는 웹툰일 경우 CustomException 발생")
    void getWebnovelById_whenInvalidWebnovelId_shouldThrowCustomException() {
        // given
        when(webtoonRepository.findByIdAndDeleted(1L, false)).thenReturn(Optional.empty());

        //when
        CustomException exception =  assertThrows(CustomException.class, () -> {
            userWebtoonService.getWebtoonById(1L, mockPrincipalUser);
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
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();

        Webtoon webtoon2 = Webtoon.builder()
                .id(2L)
                .title("테스트2")
                .description("테스트2")
                .creator(creator)
                .keywords(kewords)
                .serialDay(SerialDay.MONDAY)
                .status(SeriesStatus.ONGOING)
                .deleted(false)
                .build();

        webtoons.add(webtoon1);
        webtoons.add(webtoon2);
        List<UserKeywordResponse> userKeywordResponses = createUserKeywords(kewords);

        doReturn(userKeywordResponses).when(keywordService).getKeywordsExceptCategory(kewords);
        when(webtoonRepository.findByDeleted(false)).thenReturn(webtoons);
        //when
        List<UserContentListResponse> listResponses = userWebtoonService.getWebtoons();

        // then
        assertEquals(webtoons.size(), listResponses.size());
        assertEquals(webtoons.get(0).getTitle(), listResponses.get(0).getTitle());

    }

    @Test
    @DisplayName("주어진 요일의 조회수 상위 18개의 작품을 return")
    void getWebnovelsByDay_WhenGivenDay_shouldReturnTop18Webnovels() {
        // given
        String serialDay = "MONDAY";
        List<Webtoon> webtoons = createMockWebtoons();
        Pageable pageable = PageRequest.of(0, 18);
        when(webtoonRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable)).thenReturn(webtoons.subList(0, 18));

        //when
        List<ContentSimpleResponse> result = userWebtoonService.getWebtoonsByDay(serialDay);

        // then
        assertEquals(18, result.size());
        assertEquals("웹툰1", result.get(0).getTitle());
        assertEquals("웹툰18", result.get(17).getTitle());

    }


    @Test
    @DisplayName("주어진 요일에 해당하는 작품이 없을 경우 빈 리스트를 반환")
    void getWebnovelsByDay_whenNoWebnovelsExistForGivenDay_shouldReturnEmptyList() {
        // given
        String serialDay = "FRIDAY";
        List<Webtoon> webtoons = createMockWebtoons();
        Pageable pageable = PageRequest.of(0, 18);
        when(webtoonRepository.findDailyRanking(SerialDay.valueOf(serialDay), pageable)).thenReturn(Collections.emptyList());
        //when
        List<ContentSimpleResponse> result = userWebtoonService.getWebtoonsByDay(serialDay);

        // then
        assertEquals(0, result.size());

    }

    @Test
    @DisplayName("검색어를 입력하면 해당 검색어가 포함된 콘텐츠 리스트를 반환한다.")
    void shouldReturnWebtoons_whenKeywordProvided() {
        // given
        String query = "고양이";

        String sort = "popular";

        Pageable pageable = PageRequest.of(0, 10);


        Webtoon webtoon1 = Webtoon.builder().id(1L).title("고양이가 사라진 마을").deleted(false).build();
        Webtoon webtoon2 = Webtoon.builder().id(2L).title("고양이").deleted(false).build();
        Webtoon webtoon3 = Webtoon.builder().id(3L).title("감정을 읽는 소녀").deleted(false).build();

        when(webtoonRepository.findByTitleOrPenNameContaining(query, pageable)).thenReturn(new PageImpl<>(List.of(webtoon1, webtoon2), pageable, 2));

        //when
        Page<ContentSearchResponse> results = userWebtoonService.getWebtoonsByTitleOrCreator(query, sort, pageable);


        // then
        assertEquals(2, results.getContent().size());
        assertTrue(results.getContent().stream()
                .allMatch(w -> w.getTitle().equals("고양")));

    }

    @Test
    @DisplayName("검색 결과가 없으면 빈 리스트를 반환한다.")
    void shouldReturnEmptyList_whenNoSearchResultsExist() {
        // given
        String query = "고양이";

        String sort = "popular";
        Pageable pageable = PageRequest.of(0, 10);

        when(webtoonRepository.findByTitleOrPenNameContaining(query, pageable)).thenReturn(Page.empty());

        //when
        Page<ContentSearchResponse> results = userWebtoonService.getWebtoonsByTitleOrCreator(query, sort, pageable);

        // then
        assertEquals(0, results.getContent().size());

    }

    @Test
    @DisplayName("검색어를 입력하지 않으면 빈 리스트를 반환한다.")
    void shouldReturnEmptyList_whenKeywordIsBlank() {
        // given
        String query = "";

        String sort = "popular";
        Pageable pageable = PageRequest.of(0, 10);

        when(webtoonRepository.findByTitleOrPenNameContaining(query, pageable)).thenReturn(Page.empty());

        //when
        Page<ContentSearchResponse> results = userWebtoonService.getWebtoonsByTitleOrCreator(query, sort, pageable);

        // then
        assertEquals(0, results.getContent().size());


    }

    @Test
    @DisplayName("삭제된 콘텐츠는 검색 결과에 포함되지 않는다.")
    void shouldExcludeDeletedContents_whenSearchingKeyword() {
        // given
        String query = "고양이";

        String sort = "popular";
        Pageable pageable = PageRequest.of(0, 10);


        Webtoon webtoon1 = Webtoon.builder().id(1L).title("고양이는 귀여워").deleted(false).build();
        Webtoon webtoon2 = Webtoon.builder().id(2L).title("고양이").deleted(true).build();
        Webtoon webtoon3 = Webtoon.builder().id(3L).title("감정을 읽는 소녀").deleted(false).build();

        when(webtoonRepository.findByTitleOrPenNameContaining(query, pageable)).thenReturn(new PageImpl<>(List.of(webtoon1), pageable, 1));

        //when
        Page<ContentSearchResponse> results = userWebtoonService.getWebtoonsByTitleOrCreator(query, sort, pageable);

        // then
        assertEquals(1, results.getContent().size());

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

    private List<Webtoon> createMockWebtoons() {
        Creator creator = Creator.builder()
                .id(1L)
                .penName("필명")
                .contentType(ContentType.WEBNOVEL)
                .agreedToAiPolicy(true)
                .aiPolicyAgreedAt(LocalDateTime.now())
                .build();

        return List.of(
                new Webtoon(1L, "웹툰1", creator, SerialDay.MONDAY, 15000L),
                new Webtoon(2L, "웹툰2", creator, SerialDay.MONDAY, 14000L),
                new Webtoon(3L, "웹툰3", creator, SerialDay.MONDAY, 13500L),
                new Webtoon(4L, "웹툰4", creator, SerialDay.MONDAY, 13000L),
                new Webtoon(5L, "웹툰5", creator, SerialDay.MONDAY, 12800L),
                new Webtoon(6L, "웹툰6", creator, SerialDay.MONDAY, 12500L),
                new Webtoon(7L, "웹툰7", creator, SerialDay.MONDAY, 12000L),
                new Webtoon(8L, "웹툰8", creator, SerialDay.MONDAY, 11800L),
                new Webtoon(9L, "웹툰9", creator, SerialDay.MONDAY, 11500L),
                new Webtoon(10L, "웹툰10", creator, SerialDay.MONDAY, 11200L),
                new Webtoon(11L, "웹툰11", creator, SerialDay.MONDAY, 11000L),
                new Webtoon(12L, "웹툰12", creator, SerialDay.MONDAY, 10800L),
                new Webtoon(13L, "웹툰13", creator, SerialDay.MONDAY, 10500L),
                new Webtoon(14L, "웹툰14", creator, SerialDay.MONDAY, 10200L),
                new Webtoon(15L, "웹툰15", creator, SerialDay.MONDAY, 10000L),
                new Webtoon(16L, "웹툰16", creator, SerialDay.MONDAY, 9800L),
                new Webtoon(17L, "웹툰17", creator, SerialDay.MONDAY, 9500L),
                new Webtoon(18L, "웹툰18", creator, SerialDay.MONDAY, 9200L),

                // 18개 이상을 확인하기 위해 추가
                new Webtoon(19L, "웹툰19", creator, SerialDay.MONDAY, 9000L),
                new Webtoon(20L, "웹툰20", creator, SerialDay.MONDAY, 8800L),
                // 다른 요일
                new Webtoon(19L, "웹툰21", creator, SerialDay.TUESDAY, 9000L),
                new Webtoon(20L, "웹툰22", creator, SerialDay.TUESDAY, 8800L)

        );

    }

}