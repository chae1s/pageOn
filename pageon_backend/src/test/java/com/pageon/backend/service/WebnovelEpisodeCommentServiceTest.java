package com.pageon.backend.service;

import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.Webnovel;
import com.pageon.backend.entity.WebnovelEpisode;
import com.pageon.backend.entity.WebnovelEpisodeComment;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebnovelEpisodeCommentRepository;
import com.pageon.backend.repository.WebnovelEpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("WebnovelEpisodeCommentService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class WebnovelEpisodeCommentServiceTest {
    @InjectMocks
    private WebnovelEpisodeCommentService webnovelEpisodeCommentService;
    @Mock
    private WebnovelEpisodeCommentRepository webnovelEpisodeCommentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebnovelEpisodeRepository webnovelEpisodeRepository;

    @BeforeEach
    void setUp() {
        webnovelEpisodeCommentRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 작성 시 정상적으로 저장된다.")
    void shouldSaveComment_whenValidRequestProvided() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        String text = "좋은 작품입니다.";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();

        ContentEpisodeCommentRequest request = new ContentEpisodeCommentRequest(text, false);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));

        ArgumentCaptor<WebnovelEpisodeComment> captor = ArgumentCaptor.forClass(WebnovelEpisodeComment.class);

        //when
        webnovelEpisodeCommentService.createComment(userId, episodeId, request);

        // then
        verify(webnovelEpisodeCommentRepository).save(captor.capture());
        WebnovelEpisodeComment comment = captor.getValue();

        assertEquals(text, comment.getText());
        assertEquals(episodeId, comment.getWebnovelEpisode().getId());

    }

    @Test
    @DisplayName("존재하지 않는 에피소드에 댓글을 작성하면 예외가 발생한다.")
    void shouldThrowException_whenEpisodeDoesNotExist() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        String text = "좋은 작품입니다.";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();

        ContentEpisodeCommentRequest request = new ContentEpisodeCommentRequest(text, false);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentService.createComment(userId, episodeId, request);
        });

        // then
        assertEquals("해당 에피소드를 찾을 수 없습니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.EPISODE_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));


    }

    @Test
    @DisplayName("댓글 내용이 비어 있으면 예외가 발생한다.")
    void shouldThrowException_whenEpisodeCommentIsBlank() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        String text = "";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();

        ContentEpisodeCommentRequest request = new ContentEpisodeCommentRequest(text, false);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentService.createComment(userId, episodeId, request);
        });

        // then
        assertEquals("댓글 내용이 존재하지 않습니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_TEXT_IS_BLANK, ErrorCode.valueOf(exception.getErrorCode()));


    }

    @Test
    @DisplayName("삭제된 에피소드에 댓글을 작성하면 예외가 발생한다.")
    void shouldThrowException_whenEpisodeIsDeleted() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        String text = "좋은 작품입니다.";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").deleted(true).build();

        ContentEpisodeCommentRequest request = new ContentEpisodeCommentRequest(text, false);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentService.createComment(userId, episodeId, request);
        });

        // then
        assertEquals("삭제된 에피소드입니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.EPISODE_IS_DELETED, ErrorCode.valueOf(exception.getErrorCode()));


    }

}