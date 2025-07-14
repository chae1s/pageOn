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
import com.pageon.backend.repository.ContentDeleteRepository;
import com.pageon.backend.repository.WebtoonRepository;
import com.pageon.backend.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreatorWebtoonService implements CreatorContentService{

    private final WebtoonRepository webtoonRepository;
    private final KeywordService keywordService;
    private final FileUploadService fileUploadService;
    private final CommonService commonService;
    private final ContentDeleteRepository  contentDeleteRepository;

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
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        Webtoon webtoon = webtoonRepository.findById(contentId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBTOON_NOT_FOUND)
        );

        if (!webtoon.getCreator().getId().equals(creator.getId()))
            throw new CustomException(ErrorCode.CREATOR_UNAUTHORIZED_ACCESS);

        return CreatorContentResponse.fromWebtoon(webtoon, keywordService.getKeywords(webtoon.getKeywords()));
    }

    @Override
    public List<CreatorContentListResponse> getMyContents(PrincipalUser principalUser) {
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        List<Webtoon> webtoons = webtoonRepository.findByCreator(creator);

        return webtoons.stream()
                .map(CreatorContentListResponse::fromWebtoon)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long updateContent(PrincipalUser principalUser, Long contentId, ContentUpdateRequest contentUpdateRequest) {
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        Webtoon webtoon = webtoonRepository.findById(contentId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBTOON_NOT_FOUND)
        );

        if (!webtoon.getCreator().getId().equals(creator.getId()))
            throw new CustomException(ErrorCode.CREATOR_UNAUTHORIZED_ACCESS);


        if (contentUpdateRequest.getTitle() != null || contentUpdateRequest.getDescription() != null || contentUpdateRequest.getSerialDay() != null) {
            webtoon.updateWebtoonInfo(contentUpdateRequest);
        }

        if (contentUpdateRequest.getKeywords() != null) {
            List<Keyword> keywords = keywordService.separateKeywords(contentUpdateRequest.getKeywords());

            webtoon.updateKeywords(keywords);
        }

        if (contentUpdateRequest.getCoverFile() != null) {
            // 기존 파일 삭제
            fileUploadService.deleteFile(webtoon.getCover());
            String newS3Url = fileUploadService.upload(contentUpdateRequest.getCoverFile(), String.format("webtoons/%d", webtoon.getId()));

            webtoon.updateCover(newS3Url);
        }

        if (contentUpdateRequest.getStatus() != null) {
            webtoon.updateStatus(contentUpdateRequest.getStatus());
        }

        return webtoon.getId();
    }

    @Override
    @Transactional
    public void deleteRequestContent(PrincipalUser principalUser, Long contentId, ContentDeleteRequest contentDeleteRequest) {
        User user = commonService.findUserByEmail(principalUser.getUsername());
        Creator creator = commonService.findCreatorByUser(user);

        Webtoon webtoon = webtoonRepository.findById(contentId).orElseThrow(
                () -> new CustomException(ErrorCode.WEBTOON_NOT_FOUND)
        );

        if (!webtoon.getCreator().getId().equals(creator.getId()))
            throw new CustomException(ErrorCode.CREATOR_UNAUTHORIZED_ACCESS);

        ContentDelete contentDelete = ContentDelete.builder()
                .contentType(ContentType.WEBTOON)
                .contentId(webtoon.getId())
                .creator(creator)
                .reason(contentDeleteRequest.getReason())
                .deleteStatus(DeleteStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        contentDeleteRepository.save(contentDelete);

    }
}
