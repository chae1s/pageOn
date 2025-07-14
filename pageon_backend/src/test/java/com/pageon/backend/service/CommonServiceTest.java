package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.User;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.CreatorRepository;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.PrincipalUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("CommonService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CommonServiceTest {
    @InjectMocks
    private CommonService commonService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PrincipalUser mockPrincipalUser;
    @Mock
    private CreatorRepository creatorRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("DB에서 로그인한 유저의 정보를 찾게 된다면 그 user를 return")
    void findUserByEmail_whenUserFound_returnUser() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .nickname("test")
                .isDeleted(false)
                .isPhoneVerified(false)
                .build();

        when(userRepository.findByEmailAndIsDeletedFalse(mockPrincipalUser.getUsername())).thenReturn(Optional.of(user));

        //when
        User savedUser = commonService.findUserByEmail(mockPrincipalUser.getUsername());

        // then
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getNickname(), savedUser.getNickname());

    }

    @Test
    @DisplayName("DB에서 로그인한 유저의 정보를 찾을 수 없을 경우 CustomException 발생")
    void findUserByEmail_whenUserNotFound_shouldThrowCustomException() {
        // given

        when(userRepository.findByEmailAndIsDeletedFalse(mockPrincipalUser.getUsername())).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            commonService.findUserByEmail(mockPrincipalUser.getUsername());
        });

        // then
        assertEquals("존재하지 않는 사용자입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.USER_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));
    }

    @Test
    @DisplayName("DB에서 User의 정보로 Creator의 정보를 찾을 수 있다면 Creator를 리턴")
    void findCreatorByUser_whenCreatorFound_returnCreator() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .nickname("test")
                .isDeleted(false)
                .isPhoneVerified(false)
                .build();

        Creator creator = Creator.builder()
                .id(1L)
                .user(user)
                .penName("필명")
                .isActive(true)
                .agreedToAiPolicy(true)
                .contentType(ContentType.WEBNOVEL)
                .build();

        when(creatorRepository.findByUser(user)).thenReturn(Optional.of(creator));

        //when
        Creator savedCreator = commonService.findCreatorByUser(user);

        // then
        assertEquals(savedCreator.getUser().getId(), user.getId());
        assertEquals(savedCreator.getPenName(), creator.getPenName());

    }

    @Test
    @DisplayName("DB에서 User의 정보로 creator의 정보를 찾을 수 없다면 CustomException 발생")
    void findCreatorByUser_whenCreatorNotFound_shouldThrowCustomException() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .nickname("test")
                .isDeleted(false)
                .isPhoneVerified(false)
                .build();

        when(creatorRepository.findByUser(user)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            commonService.findCreatorByUser(user);
        });


        // then
        assertEquals("존재하지 않는 작가입니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.CREATER_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

}