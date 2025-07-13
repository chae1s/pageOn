package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.dto.request.WebnovelCreateRequest;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import com.pageon.backend.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebnovelService {

    private final WebnovelRepository webnovelRepository;
    private final FileUploadService fileUploadService;
    private final CommonService commonService;
    private final KeywordService keywordService;


    @Transactional
    public void createWebnovel(PrincipalUser principalUser, WebnovelCreateRequest webnovelCreateRequest) {
        User user = commonService.findUserByEmail(principalUser.getUsername());

        Creator creator = commonService.findCreatorByUser(user);

        if (creator.getContentType() != ContentType.WEBNOVEL)
            throw new CustomException(ErrorCode.NOT_CREATOR_OF_WEBTOON);

        Webnovel webnovel = Webnovel.builder()
                .title(webnovelCreateRequest.getTitle())
                .description(webnovelCreateRequest.getDescription())
                .creator(creator)
                .keywords(keywordService.separateKeywords(webnovelCreateRequest.getKeywords()))
                .serialDay(DayOfWeek.valueOf(webnovelCreateRequest.getSerialDay()))
                .build();

        webnovelRepository.save(webnovel);

        String s3Url = fileUploadService.upload(webnovelCreateRequest.getCoverFile(), String.format("webnovels/%d", webnovel.getId()));

        webnovel.uploadCover(s3Url);
    }
}
