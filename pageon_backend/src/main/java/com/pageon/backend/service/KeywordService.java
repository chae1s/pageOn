package com.pageon.backend.service;

import com.pageon.backend.dto.response.CreatorKeywordResponse;
import com.pageon.backend.entity.Category;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.repository.CategoryRepository;
import com.pageon.backend.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final CategoryRepository categoryRepository;

    public Set<Keyword> separateKeywords(String line) {
        String[] words = line.replaceAll("\\s", "").split(",");
        Set<Keyword> keywords = new LinkedHashSet<>();
        Category category = categoryRepository.findById(6L).orElseThrow(() -> new RuntimeException());
        for (String word : words) {

            Keyword keyword = keywordRepository.findByName(word).orElseGet(
                    () -> {
                        Keyword newKeyword = new Keyword(category, word);
                        keywordRepository.save(newKeyword);

                        return newKeyword;
                    }
            );

            keywords.add(keyword);

        }

        return keywords;
    }

    public List<CreatorKeywordResponse> getKeywords(Set<Keyword> keywords) {
        List<CreatorKeywordResponse> creatorKeywordResponses = new ArrayList<>();
        for (Keyword keyword : keywords) {
            creatorKeywordResponses.add(CreatorKeywordResponse.fromEntity(keyword));
        }

        return creatorKeywordResponses;
    }
}
