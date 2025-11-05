package com.pageon.backend.service;

import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.Webtoon;
import com.pageon.backend.entity.WebtoonEpisode;
import com.pageon.backend.entity.WebtoonEpisodeComment;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebtoonEpisodeCommentRepository;
import com.pageon.backend.repository.WebtoonEpisodeRepository;
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
@DisplayName("WebtoonEpisodeCommentService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class WebtoonEpisodeCommentServiceTest {
    @InjectMocks
    private WebtoonEpisodeCommentService webtoonEpisodeCommentService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebtoonEpisodeRepository webtoonEpisodeRepository;
    @Mock
    private WebtoonEpisodeCommentRepository webtoonEpisodeCommentRepository;

    @BeforeEach
    void setUp() {
        webtoonEpisodeCommentRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 작성 시 정상적으로 저장된다.")
    void shouldSaveComment_whenValidRequestProvided() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        String text = "좋은 작품입니다.";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").deleted(false).build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();

        ContentEpisodeCommentRequest request = new ContentEpisodeCommentRequest(text, false);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        ArgumentCaptor<WebtoonEpisodeComment> captor = ArgumentCaptor.forClass(WebtoonEpisodeComment.class);

        //when
        webtoonEpisodeCommentService.createComment(userId, episodeId, request);

        // then
        verify(webtoonEpisodeCommentRepository).save(captor.capture());
        WebtoonEpisodeComment comment = captor.getValue();

        assertEquals(text, comment.getText());
        assertEquals(episodeId, comment.getWebtoonEpisode().getId());

    }

    @Test
    @DisplayName("존재하지 않는 에피소드에 댓글을 작성하면 예외가 발생한다.")
    void shouldThrowException_whenEpisodeDoesNotExist() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        String text = "좋은 작품입니다.";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").deleted(false).build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();

        ContentEpisodeCommentRequest request = new ContentEpisodeCommentRequest(text, false);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentService.createComment(userId, episodeId, request);
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

        Long webtoonId = 10L;
        Long episodeId = 100L;

        String text = "";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").deleted(false).build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();

        ContentEpisodeCommentRequest request = new ContentEpisodeCommentRequest(text, false);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentService.createComment(userId, episodeId, request);
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

        Long webtoonId = 10L;
        Long episodeId = 100L;

        String text = "좋은 작품입니다.";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").deleted(false).build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").deleted(true).build();

        ContentEpisodeCommentRequest request = new ContentEpisodeCommentRequest(text, false);

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentService.createComment(userId, episodeId, request);
        });

        // then
        assertEquals("삭제된 에피소드입니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.EPISODE_IS_DELETED, ErrorCode.valueOf(exception.getErrorCode()));


    }

}