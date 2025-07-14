package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.dto.request.ContentCreateRequest;
import com.pageon.backend.dto.request.ContentDeleteRequest;
import com.pageon.backend.dto.request.ContentUpdateRequest;
import com.pageon.backend.dto.response.CreatorContentListResponse;
import com.pageon.backend.dto.response.CreatorContentResponse;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.Webtoon;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.WebtoonRepository;
import com.pageon.backend.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatorWebtoonService implements CreatorContentService{

    private final WebtoonRepository webtoonRepository;
    private final KeywordService keywordService;
    private final FileUploadService fileUploadService;
    private final CommonService commonService;

    @Override
    @Transactional
    public void createContent(PrincipalUser principalUser, ContentCreateRequest contentCreateRequest) {
        User user = commonService.findUserByEmail(principalUser.getUsername());

        Creator creator = commonService.findCreatorByUser(user);

        if (creator.getContentType() != ContentType.WEBTOON)
            throw new CustomException(ErrorCode.NOT_CREATOR_OF_WEBTOON);

        Webtoon webtoon = Webtoon.builder()
                .title(contentCreateRequest.getTitle())
                .description(contentCreateRequest.getDescription())
                .creator(creator)
                .keywords(keywordService.separateKeywords(contentCreateRequest.getKeywords()))
                .serialDay(DayOfWeek.valueOf(contentCreateRequest.getSerialDay()))
                .build();

        webtoonRepository.save(webtoon);

        String s3Url = fileUploadService.upload(contentCreateRequest.getCoverFile(), String.format("webtoons/%d", webtoon.getId()));

        webtoon.updateCover(s3Url);
    }

    @Override
    public CreatorContentResponse getContentById(PrincipalUser principalUser, Long contentId) {
        return null;
    }

    @Override
    public List<CreatorContentListResponse> getMyContents(PrincipalUser principalUser) {
        return List.of();
    }

    @Override
    public Long updateContent(PrincipalUser principalUser, Long contentId, ContentUpdateRequest contentUpdateRequest) {
        return 0L;
    }

    @Override
    public void deleteRequestContent(PrincipalUser principalUser, Long contentId, ContentDeleteRequest contentDeleteRequest) {

    }
}
