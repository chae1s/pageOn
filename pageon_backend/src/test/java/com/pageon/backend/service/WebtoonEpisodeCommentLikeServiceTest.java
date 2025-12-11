package com.pageon.backend.service;

import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebtoonEpisodeCommentLikeRepository;
import com.pageon.backend.repository.WebtoonEpisodeCommentRepository;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("WebtoonEpisodeCommentLikeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class WebtoonEpisodeCommentLikeServiceTest {
    @InjectMocks
    private WebtoonEpisodeCommentLikeService webtoonEpisodeCommentLikeService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebtoonEpisodeCommentRepository webtoonEpisodeCommentRepository;
    @Mock
    private WebtoonEpisodeCommentLikeRepository webtoonEpisodeCommentLikeRepository;

    @BeforeEach
    void setUp() {
        webtoonEpisodeCommentLikeRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글에 좋아요를 누르면 정상적으로 등록된다.")
    void shouldAddLike_whenUserLikesCommentForTheFirstTime() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();
        WebtoonEpisodeComment comment = WebtoonEpisodeComment.builder().id(commentId).user(user).webtoonEpisode(webtoonEpisode).text(text).likeCount(10L).build();

        when(userRepository.getReferenceById(eq(userId))).thenReturn(user);
        when(webtoonEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(webtoonEpisodeCommentLikeRepository.existsByUser_IdAndWebtoonEpisodeComment_Id(userId, commentId)).thenReturn(false);

        ArgumentCaptor<WebtoonEpisodeCommentLike> likeCaptor = ArgumentCaptor.forClass(WebtoonEpisodeCommentLike.class);


        //when
        webtoonEpisodeCommentLikeService.createCommentLike(userId, commentId);

        // then
        verify(webtoonEpisodeCommentLikeRepository).save(likeCaptor.capture());
        WebtoonEpisodeCommentLike like = likeCaptor.getValue();

        assertEquals(11, comment.getLikeCount());
        verify(webtoonEpisodeCommentLikeRepository, times(1)).save(any(WebtoonEpisodeCommentLike.class));

    }

    @Test
    @DisplayName("존재하지 않는 댓글에 좋아요를 누르면 예외가 발생한다.")
    void shouldThrowException_whenLikingNonexistentComment() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();


        Long commentId = 200L;


        when(userRepository.getReferenceById(eq(userId))).thenReturn(user);
        when(webtoonEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentLikeService.createCommentLike(userId, commentId);
        });

        // then
        assertEquals("존재하지 않는 댓글입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("이미 좋아요한 댓글에 다시 좋아요를 누르면 예외가 발생한다.")
    void shouldThrowException_whenUserLikesCommentAgain() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();
        WebtoonEpisodeComment comment = WebtoonEpisodeComment.builder().id(commentId).user(user).webtoonEpisode(webtoonEpisode).text(text).likeCount(10L).build();

        when(userRepository.getReferenceById(eq(userId))).thenReturn(user);
        when(webtoonEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(webtoonEpisodeCommentLikeRepository.existsByUser_IdAndWebtoonEpisodeComment_Id(userId, commentId)).thenReturn(true);

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentLikeService.createCommentLike(userId, commentId);
        });

        // then
        assertEquals("이미 좋아요한 댓글입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_ALREADY_LIKED, ErrorCode.valueOf(exception.getErrorCode()));


    }

    @Test
    @DisplayName("삭제된 댓글에 좋아요를 누르면 예외가 발생한다.")
    void shouldThrowException_whenLikingDeletedComment() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();
        WebtoonEpisodeComment comment = WebtoonEpisodeComment.builder().id(commentId).user(user).webtoonEpisode(webtoonEpisode).text(text).likeCount(10L).deletedAt(LocalDateTime.now()).build();

        when(userRepository.getReferenceById(eq(userId))).thenReturn(user);
        when(webtoonEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentLikeService.createCommentLike(userId, commentId);
        });

        // then
        assertEquals("이미 삭제된 댓글입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_ALREADY_DELETED, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("댓글 좋아요 취소 시 정상적으로 삭제된다.")
    void shouldDeleteLike_whenUserUnlikesComment() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();
        WebtoonEpisodeComment comment = WebtoonEpisodeComment.builder().id(commentId).user(user).webtoonEpisode(webtoonEpisode).text(text).likeCount(10L).build();

        WebtoonEpisodeCommentLike commentLike = WebtoonEpisodeCommentLike.builder().user(user).webtoonEpisodeComment(comment).build();

        when(webtoonEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(webtoonEpisodeCommentLikeRepository.findByUser_IdAndWebtoonEpisodeComment_Id(userId, commentId)).thenReturn(Optional.of(commentLike));

        //when
        webtoonEpisodeCommentLikeService.deleteCommentLike(userId, commentId);

        // then
        verify(webtoonEpisodeCommentLikeRepository, times(1)).delete(commentLike);
        assertEquals(9, comment.getLikeCount());

    }

    @Test
    @DisplayName("존재하지 않는 댓글의 좋아요를 삭제하려 하면 예외가 발생한다.")
    void shouldThrowException_whenUnlikingNonexistentComment() {
        // given
        Long userId = 1L;

        Long commentId = 200L;

        when(webtoonEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentLikeService.deleteCommentLike(userId, commentId);
        });

        // then
        assertEquals("존재하지 않는 댓글입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));


    }

    @Test
    @DisplayName("삭제된 댓글의 좋아요를 삭제하려 하면 예외가 발생한다.")
    void shouldThrowException_whenUnlikingDeletedComment() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();
        WebtoonEpisodeComment comment = WebtoonEpisodeComment.builder().id(commentId).user(user).webtoonEpisode(webtoonEpisode).text(text).likeCount(10L).deletedAt(LocalDateTime.now()).build();

        when(webtoonEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentLikeService.deleteCommentLike(userId, commentId);
        });

        // then
        assertEquals("이미 삭제된 댓글입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_ALREADY_DELETED, ErrorCode.valueOf(exception.getErrorCode()));


    }

    @Test
    @DisplayName("좋아요하지 않은 댓글의 좋아요를 취소하려 하면 예외가 발생한다.")
    void shouldThrowException_whenUnlikingCommentNotLikedYet() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();
        WebtoonEpisodeComment comment = WebtoonEpisodeComment.builder().id(commentId).user(user).webtoonEpisode(webtoonEpisode).text(text).likeCount(10L).build();

        WebtoonEpisodeCommentLike commentLike = WebtoonEpisodeCommentLike.builder().user(user).webtoonEpisodeComment(comment).build();

        when(webtoonEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(webtoonEpisodeCommentLikeRepository.findByUser_IdAndWebtoonEpisodeComment_Id(userId, commentId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentLikeService.deleteCommentLike(userId, commentId);
        });

        // then
        assertEquals("사용자가 좋아요를 하지 않은 댓글입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_LIKE_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

}