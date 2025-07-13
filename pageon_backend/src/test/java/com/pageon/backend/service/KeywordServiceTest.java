package com.pageon.backend.service;

import com.pageon.backend.entity.Category;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.repository.CategoryRepository;
import com.pageon.backend.repository.KeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("KeywordService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {
    @InjectMocks
    private KeywordService keywordService;
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private CategoryRepository categoryRepository;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder().id(6L).name("카테고리").build();

        when(categoryRepository.findById(6L)).thenReturn(Optional.of(mockCategory));
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "감성,드라마,로맨스",
            "스릴러,공포,추리",
            "판타지,이세계,마법"
    })
    @DisplayName("받아온 문자열을 split으로 분리 후 keyword가 DB에 있으면 Set에 추가")
    void separateKeywords_whenKeywordInDB_shouldAddSetList(String keyword) {
        // given
        String[] words = keyword.replaceAll("\\s", "").split(",");

        for (String word : words) {
            Keyword realKeyword = Keyword.builder()
                    .category(mockCategory)
                    .name(word)
                    .build();

            when(keywordRepository.findByName(word)).thenReturn(Optional.of(realKeyword));
        }
        //when
        Set<Keyword> resultKeywords = keywordService.separateKeywords(keyword);
        
        // then
        assertEquals(words.length, resultKeywords.size());

        
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "감성,드라마,로맨스",
            "스릴러,공포,추리",
            "판타지,이세계,마법"
    })
    @DisplayName("받아온 문자열을 split으로 분리 후 keyword가 DB에 없으면면 DB에 추가")
    void separateKeywords_whenKeywordNotInDB_shouldCreateDB(String keyword) {
        // given
        String[] words = keyword.replaceAll("\\s", "").split(",");

        for (String word : words) {

            when(keywordRepository.findByName(word)).thenReturn(Optional.empty());
        }
        //when
        Set<Keyword> resultKeywords = keywordService.separateKeywords(keyword);

        // then
        assertEquals(words.length, resultKeywords.size());
        assertEquals(6L, resultKeywords.iterator().next().getCategory().getId());


    }



}