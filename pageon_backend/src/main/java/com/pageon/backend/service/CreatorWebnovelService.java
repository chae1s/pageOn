package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.DeleteStatus;
import com.pageon.backend.dto.request.ContentCreateRequest;
import com.pageon.backend.dto.request.ContentDeleteRequest;
import com.pageon.backend.dto.request.ContentUpdateRequest;
import com.pageon.backend.dto.response.CreatorContentListResponse;
import com.pageon.backend.dto.response.CreatorContentResponse;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import com.pageon.backend.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreatorWebnovelService implements CreatorContentService {

    private final WebnovelRepository webnovelRepository;
    private final FileUploadService fileUploadService;
    private final CommonService commonService;
    private final KeywordService keywordService;
    private final ContentDeleteRepository contentDeleteRepository;


    @Override
    @Transactional
    public void createContent(PrincipalUser principalUser, ContentCreateRequest contentCreateRequest) {
        User user = commonService.findUserByEmail(principalUser.getUsername());

        Creator creator = commonService.findCreatorByUser(user);

        if (creator.getContentType() != ContentType.WEBNOVEL)
            throw new CustomException(ErrorCode.NOT_CREATOR_OF_WEBNOVEL);

        Webnovel webnovel = Webnovel.builder()
                .title(contentCreateRequest.getTitle())
                .description(contentCreateRequest.getDescription())
                .creator(creator)
                .keywords(keywordService.separateKeywords(contentCreateRequest.getKeywords()))
                .serialDay(DayOfWeek.valueOf(contentCreateRequest.getSerialDay()))
                .build();

        webnovelRepository.save(webnovel);

        String s3Url = fileUploadService.upload(contentCreateRequest.getCoverFile(), String.format("webnovels/%d", webnovel.getId()));

        webnovel.updateCover(s3Url);
    }

    // 내가 작성한 웹소설의 정보를 가져오는 메소드
    @Override
    public CreatorContentResponse getContentById(PrincipalUser principalUser, Long webnovelId) {
        // 로그인한 유저에게서 가져온 creator 정보
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        // 웹소설에서 가져온 creator 정보
        Webnovel webnovel = webnovelRepository.findById(webnovelId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
        );

        if (!webnovel.getCreator().getId().equals(creator.getId()))
            throw new CustomException(ErrorCode.CREATOR_UNAUTHORIZED_ACCESS);

        return CreatorContentResponse.fromWebnovel(webnovel, keywordService.getKeywords(webnovel.getKeywords()));
    }

    // 내가 작성한 웹소설 리스트를 가져오는 메소드
    @Override
    public List<CreatorContentListResponse> getMyContents(PrincipalUser principalUser) {
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        List<Webnovel> webnovels = webnovelRepository.findByCreator(creator);


        return webnovels.stream()
                .map(CreatorContentListResponse::fromWebnovel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long updateContent(PrincipalUser principalUser, Long webnovelId, ContentUpdateRequest contentUpdateRequest) {
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        Webnovel webnovel = webnovelRepository.findById(webnovelId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
        );

        if (contentUpdateRequest.getTitle() != null || contentUpdateRequest.getDescription() != null || contentUpdateRequest.getSerialDay() != null) {
            webnovel.updateWebnovelInfo(contentUpdateRequest);
        }

        if (contentUpdateRequest.getKeywords() != null) {
            List<Keyword> keywords = keywordService.separateKeywords(contentUpdateRequest.getKeywords());

            webnovel.updateKeywords(keywords);
        }

        if (contentUpdateRequest.getCoverFile() != null) {
            // 기존 파일 삭제
            fileUploadService.deleteFile(webnovel.getCover());
            String newS3Url = fileUploadService.upload(contentUpdateRequest.getCoverFile(), String.format("webnovels/%d", webnovel.getId()));

            webnovel.updateCover(newS3Url);
        }

        if (contentUpdateRequest.getStatus() != null) {
            webnovel.updateStatus(contentUpdateRequest.getStatus());
        }

        return webnovelId;
    }


    @Transactional
    public void deleteRequestContent(PrincipalUser principalUser, Long webnovelId, ContentDeleteRequest contentDeleteRequest) {
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        Webnovel webnovel = webnovelRepository.findById(webnovelId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBNOVEL_NOT_FOUND)
        );

        if (!webnovel.getCreator().getId().equals(creator.getId()))
            throw new CustomException(ErrorCode.CREATOR_UNAUTHORIZED_ACCESS);

        ContentDelete contentDelete = ContentDelete.builder()
                .contentType(ContentType.WEBNOVEL)
                .contentId(webnovel.getId())
                .creator(creator)
                .reason(contentDeleteRequest.getReason())
                .deleteStatus(DeleteStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        contentDeleteRepository.save(contentDelete);


    }
}
