package com.pageon.backend.service;

import com.pageon.backend.dto.response.UserContentListResponse;
import com.pageon.backend.dto.response.UserKeywordResponse;
import com.pageon.backend.dto.response.UserWebnovelResponse;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebnovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserWebnovelService {

    private final WebnovelRepository webnovelRepository;
    private final KeywordService keywordService;

    public UserWebnovelResponse getWebnovelById(Long webnovelId) {
        Webnovel webnovel = webnovelRepository.findByIdAndIsDeletedFalse(webnovelId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
        );

        List<UserKeywordResponse> keywords = keywordService.getKeywordsExceptCategory(webnovel.getKeywords());

        return UserWebnovelResponse.fromEntity(webnovel, keywords);
    }

    public List<UserContentListResponse> getWebnovels() {
        List<Webnovel> webnovels = webnovelRepository.findByIsDeletedFalse();
        List<UserContentListResponse> webnovelListResponses = new ArrayList<>();

        for (Webnovel webnovel : webnovels) {
            List<UserKeywordResponse> keywords = keywordService.getKeywordsExceptCategory(webnovel.getKeywords());
            webnovelListResponses.add(UserContentListResponse.fromWebnovel(webnovel, keywords, 0, 0));
        }

        return webnovelListResponses;
    }
}
