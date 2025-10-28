package com.pageon.backend.dto.response;

import com.pageon.backend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithKeywordsResponse {

    private Long id;
    private String name;
    private List<KeywordResponse> keywords;

    public static CategoryWithKeywordsResponse fromEntity(Category category) {
        CategoryWithKeywordsResponse categoryWithKeywordsResponse = new CategoryWithKeywordsResponse();
        categoryWithKeywordsResponse.setId(category.getId());
        categoryWithKeywordsResponse.setName(category.getName());
        categoryWithKeywordsResponse.setKeywords(category.getKeywords().stream()
                .map(KeywordResponse::fromEntity)
                .toList()
        );

        return categoryWithKeywordsResponse;
    }


}
