package com.pageon.backend.service;

import com.pageon.backend.dto.response.ContentResponse;
import com.pageon.backend.dto.response.CreatorKeywordResponse;
import com.pageon.backend.dto.response.UserKeywordResponse;
import com.pageon.backend.entity.Category;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.repository.CategoryRepository;
import com.pageon.backend.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final CategoryRepository categoryRepository;

    public List<Keyword> separateKeywords(String line) {
        String[] words = line.replaceAll("\\s", "").split(",");
        LinkedHashMap<String, Keyword> keywordMap = new LinkedHashMap<>();
        Category category = categoryRepository.findById(6L).orElseThrow(() -> new RuntimeException());
        for (String word : words) {
            if (!keywordMap.containsKey(word)) {
                Keyword keyword = keywordRepository.findByName(word).orElseGet(
                        () -> {
                            Keyword newKeyword = new Keyword(category, word);
                            keywordRepository.save(newKeyword);

                            return newKeyword;
                        }
                );

                keywordMap.put(word, keyword);
            }

        }
        return new ArrayList<>(keywordMap.values());
    }

    public List<CreatorKeywordResponse> getKeywords(List<Keyword> keywords) {
        List<CreatorKeywordResponse> creatorKeywordResponses = new ArrayList<>();
        for (Keyword keyword : keywords) {
            creatorKeywordResponses.add(CreatorKeywordResponse.fromEntity(keyword));
        }

        return creatorKeywordResponses;
    }

    public List<UserKeywordResponse> getKeywordsExceptCategory(List<Keyword> keywords) {
        List<UserKeywordResponse> userKeywordResponses = new ArrayList<>();
        for (Keyword keyword : keywords) {
            if (!keyword.getCategory().getId().equals(6L)) {
                userKeywordResponses.add(UserKeywordResponse.fromEntity(keyword));
            }
        }
        return userKeywordResponses;
    }

}
