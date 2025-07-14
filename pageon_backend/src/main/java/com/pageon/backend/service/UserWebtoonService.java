package com.pageon.backend.service;

import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.dto.response.UserKeywordResponse;
import com.pageon.backend.dto.response.UserWebtoonResponse;
import com.pageon.backend.entity.Webtoon;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserWebtoonService {

    private final WebtoonRepository webtoonRepository;
    private final KeywordService keywordService;

    public UserWebtoonResponse getWebtoonById(Long webtoonId) {
        Webtoon webtoon = webtoonRepository.findByIdAndIsDeletedFalse(webtoonId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBTOON_NOT_FOUND)
        );
        List<UserKeywordResponse> keywords = keywordService.getKeywordsExceptCategory(webtoon.getKeywords());

        return UserWebtoonResponse.fromEntity(webtoon, keywords);
    }

    public List<UserContentListResponse> getWebtoons() {
        List<Webtoon> webtoons = webtoonRepository.findByIsDeletedFalse();
        List<UserContentListResponse> webnovelListResponses = new ArrayList<>();

        for (Webtoon webtoon : webtoons) {
            List<UserKeywordResponse> keywords = keywordService.getKeywordsExceptCategory(webtoon.getKeywords());
            webnovelListResponses.add(UserContentListResponse.fromWebtoon(webtoon, keywords, 0, 0));
        }

        return webnovelListResponses;
    }
}
