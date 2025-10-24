package com.pageon.backend.service;

import com.pageon.backend.dto.response.CategoryWithKeywordsResponse;
import com.pageon.backend.entity.Category;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("CategoryService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("이름이 'uncategorized'인 카테고리를 제외한 나머지 카테고리들을 출력한다.")
    void shouldExcludeUnassignedCategory_whenFetchingCategories() {
        // given

        Keyword genre1 = Keyword.builder().id(1L).name("SF").build();
        Keyword genre2 = Keyword.builder().id(2L).name("GAME").build();
        Keyword theme1 = Keyword.builder().id(3L).name("AI").build();

        Category category1 = Category.builder()
                .id(1L)
                .name("genre")
                .keywords(List.of(genre1, genre2))
                .build();
        Category category2 = Category.builder()
                .id(2L)
                .name("theme")
                .keywords(List.of(theme1))
                .build();

        List<Category> categories = List.of(category1, category2);

        when(categoryRepository.findAllWithKeywordsExcludingUncategorized()).thenReturn(categories);
        
        //when
        List<CategoryWithKeywordsResponse> result = categoryService.getAllCategoriesWithKeywords();
        
        // then
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(c -> c.getName().equals("uncategorized")));
        assertEquals(2, result.get(0).getKeywords().size());
        assertEquals("SF", result.get(0).getKeywords().get(0).getName());
    }

}