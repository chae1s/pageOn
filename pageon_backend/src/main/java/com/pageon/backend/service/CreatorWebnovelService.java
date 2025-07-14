package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.dto.request.WebnovelCreateRequest;
import com.pageon.backend.dto.request.WebnovelUpdateRequest;
import com.pageon.backend.dto.response.CreatorWebnovelListResponse;
import com.pageon.backend.dto.response.CreatorWebnovelResponse;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Keyword;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import com.pageon.backend.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreatorWebnovelService {

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

        webnovel.updateCover(s3Url);
    }

    // 내가 작성한 웹소설의 정보를 가져오는 메소드
    public CreatorWebnovelResponse getWebnovelById(PrincipalUser principalUser, Long webnovelId) {
        // 로그인한 유저에게서 가져온 creator 정보
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        // 웹소설에서 가져온 creator 정보
        Webnovel webnovel = webnovelRepository.findById(webnovelId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
        );

        if (!webnovel.getCreator().getId().equals(creator.getId()))
            throw new CustomException(ErrorCode.CREATOR_UNAUTHORIZED_ACCESS);

        return CreatorWebnovelResponse.fromEntity(webnovel, keywordService.getKeywords(webnovel.getKeywords()));
    }

    // 내가 작성한 웹소설 리스트를 가져오는 메소드
    public List<CreatorWebnovelListResponse> getMyWebnovels(PrincipalUser principalUser) {
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        List<Webnovel> webnovels = webnovelRepository.findByCreator(creator);


        return webnovels.stream()
                .map(CreatorWebnovelListResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long updateWebnovel(PrincipalUser principalUser, Long webnovelId, WebnovelUpdateRequest webnovelUpdateRequest) {
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        Webnovel webnovel = webnovelRepository.findById(webnovelId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
        );

        if (webnovelUpdateRequest.getTitle() != null || webnovelUpdateRequest.getDescription() != null || webnovelUpdateRequest.getSerialDay() != null) {
            webnovel.updateWebnovelInfo(webnovelUpdateRequest);
        }

        if (webnovelUpdateRequest.getKeywords() != null) {
            List<Keyword> keywords = keywordService.separateKeywords(webnovelUpdateRequest.getKeywords());

            webnovel.updateKeywords(keywords);
        }

        if (webnovelUpdateRequest.getCoverFile() != null) {
            // 기존 파일 삭제
            fileUploadService.deleteFile(webnovel.getCover());
            String newS3Url = fileUploadService.upload(webnovelUpdateRequest.getCoverFile(), String.format("webnovels/%d", webnovel.getId()));

            webnovel.updateCover(newS3Url);
        }

        if (webnovelUpdateRequest.getStatus() != null) {
            webnovel.updateStatus(webnovelUpdateRequest.getStatus());
        }

        return webnovelId;
    }


}
