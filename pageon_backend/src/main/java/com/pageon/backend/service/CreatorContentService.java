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
import com.pageon.backend.security.PrincipalUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

interface CreatorContentService {

    void createContent(PrincipalUser principalUser, ContentCreateRequest contentCreateRequest);

    // 내가 작성한 content 정보를 가져오는 메소드
    CreatorContentResponse getContentById(PrincipalUser principalUser, Long contentId);

    // 내가 작성한 content 리스트를 가져오는 메소드
    List<CreatorContentListResponse> getMyContents(PrincipalUser principalUser);

    Long updateContent(PrincipalUser principalUser, Long contentId, ContentUpdateRequest contentUpdateRequest);

    void deleteRequestContent(PrincipalUser principalUser, Long contentId, ContentDeleteRequest contentDeleteRequest);
}
