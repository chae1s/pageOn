package com.pageon.backend.service;

import com.pageon.backend.dto.response.CreatorKeywordResponse;
import com.pageon.backend.entity.Category;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.repository.CategoryRepository;
import com.pageon.backend.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
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

}
