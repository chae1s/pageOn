package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.DayOfWeek;
import com.pageon.backend.common.enums.RoleType;
import com.pageon.backend.dto.request.WebnovelCreateRequest;
import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import com.pageon.backend.security.PrincipalUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("WebnovelService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class WebnovelServiceTest {
    @InjectMocks
    private WebnovelService webnovelService;
    @Mock
    private WebnovelRepository webnovelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CreatorRepository creatorRepository;
    @Mock
    private PrincipalUser mockPrincipalUser;
    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private CommonService commonService;
    @Mock
    private KeywordService keywordService;

    @BeforeEach
    void setUp() {
        webnovelRepository.deleteAll();
        Role roleUser = new Role("ROLE_USER");
        Role roleCreator = new Role("ROLE_CREATOR");

        when(roleRepository.findByRoleType(RoleType.ROLE_USER)).thenReturn(Optional.of(roleUser));
        when(roleRepository.findByRoleType(RoleType.ROLE_CREATOR)).thenReturn(Optional.of(roleCreator));
    }
    
    // 웹소설 저장
    @Test
    @DisplayName("로그인한 유저가 creator이고 content type이 webnovel일 때 제목, 설명, 웹소설, 키워드, 커버, 연재 요일 작성 시 생성 ")
    void createWebnovel_withValidCreatorAndCorrectInput_shouldCreateWebnovel() {
        // given
        User user = createUser();

        when(commonService.findUserByEmail(mockPrincipalUser.getUsername())).thenReturn(user);

        Creator creator = createCreator(user, ContentType.WEBNOVEL);

        when(commonService.findCreatorByUser(user)).thenReturn(creator);

        MockMultipartFile mockFile =  new MockMultipartFile("file", "file".getBytes());

        WebnovelCreateRequest request = new WebnovelCreateRequest("웹소설 제목", "웹소설 설명", "하나,둘,셋,넷", mockFile, "MONDAY");
        Set<Keyword> keywords = new LinkedHashSet<>();

        doReturn(keywords).when(keywordService).separateKeywords("하나,둘,셋,넷");
        
        // 파일 업로드 했다고 가정
        doReturn("/webnovels/cover.png").when(fileUploadService).upload(any(), anyString());
        
        ArgumentCaptor<Webnovel> captor = ArgumentCaptor.forClass(Webnovel.class);
        
        //when
        webnovelService.createWebnovel(mockPrincipalUser, request);
        
        // then
        verify(webnovelRepository).save(captor.capture());
        Webnovel webnovel = captor.getValue();
        
        assertEquals("웹소설 제목", webnovel.getTitle());
        assertEquals("웹소설 설명", webnovel.getDescription());
        assertEquals("/webnovels/cover.png", webnovel.getCover());
        assertEquals(DayOfWeek.MONDAY, webnovel.getSerialDay());
    }


    
    @Test
    @DisplayName("웹소설을 작성하려는 Creator의 contentType이 webnovel이 아니면 CustomException 발생")
    void createWebnovel_withNotMatchContentType_shouldThrowCustomException() {
        // given
        User user = createUser();

        when(userRepository.findByEmailAndIsDeletedFalse(mockPrincipalUser.getUsername())).thenReturn(Optional.of(user));

        Creator creator = createCreator(user, ContentType.WEBTOON);
        when(creatorRepository.findByUser(user)).thenReturn(Optional.of(creator));
        
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelService.createWebnovel(mockPrincipalUser, new WebnovelCreateRequest());
        });
        
        // then
        assertEquals("웹툰 업로드 권한이 없습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.NOT_CREATOR_OF_WEBTOON, ErrorCode.valueOf(exception.getErrorCode()));
        
    }

    
    @Test
    @DisplayName("웹소설 작성 후 cover file이 s3에 업로드가 되지 않았으면 CustomException 발생")
    void createWebnovel_whenCoverUploadFails_shouldThrowCustomException() {
        // given
        User user = createUser();

        when(userRepository.findByEmailAndIsDeletedFalse(mockPrincipalUser.getUsername())).thenReturn(Optional.of(user));

        Creator creator = createCreator(user, ContentType.WEBNOVEL);

        when(creatorRepository.findByUser(user)).thenReturn(Optional.of(creator));

        MockMultipartFile mockFile =  new MockMultipartFile("file", "file".getBytes());

        WebnovelCreateRequest request = new WebnovelCreateRequest("웹소설 제목", "웹소설 설명", "하나,둘,셋,넷", mockFile, "MONDAY");

        doThrow(new CustomException(ErrorCode.S3_UPLOAD_FAILED)).when(fileUploadService).upload(any(MockMultipartFile.class), any(String.class));
        
        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelService.createWebnovel(mockPrincipalUser, request);
        });
        
        // then
        assertEquals("S3 업로드 중 오류가 발생했습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.S3_UPLOAD_FAILED, ErrorCode.valueOf(exception.getErrorCode()));
    }
    
    

    // role에 creator가 포함되어 있는 유저를 return
    private User createUser() {

        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .nickname("테스트")
                .userRoles(new ArrayList<>())
                .isDeleted(false)
                .isPhoneVerified(true)
                .build();

        Role roleUser = roleRepository.findByRoleType(RoleType.ROLE_USER).orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));

        UserRole userRole = UserRole.builder()
                .role(roleUser)
                .user(user)
                .build();

        user.getUserRoles().add(userRole);


        Role roleCreator = roleRepository.findByRoleType(RoleType.ROLE_CREATOR).orElseThrow(() -> new CustomException(ErrorCode.ROLE_NOT_FOUND));

        UserRole createrRole = UserRole.builder()
                .role(roleCreator)
                .user(user)
                .build();

        user.getUserRoles().add(createrRole);


        return user;
    }

    // user가 포함되어 있는 creator를 return
    private Creator createCreator(User user, ContentType contentType) {
        Creator creator = Creator.builder()
                .id(1L)
                .penName("필명")
                .user(user)
                .contentType(contentType)
                .agreedToAiPolicy(true)
                .aiPolicyAgreedAt(LocalDateTime.now())
                .build();

        return creator;
    }

    private Set<Keyword> separateKeywords(String line) {
        String[] words = line.split(",");
        Set<Keyword> keywords = new HashSet<>();
        Category category = categoryRepository.findById(6L).orElseThrow(() -> new RuntimeException());
        for (int i = 0; i < words.length; i++) {
            Optional<Keyword> optionalKeyword = keywordRepository.findByName(words[i]);

            if (optionalKeyword.isPresent()) {
                keywords.add(optionalKeyword.get());
            } else {
                Keyword keyword = new Keyword(category, words[i]);

                keywordRepository.save(keyword);
            }
            
        }

        return keywords;
    }

}