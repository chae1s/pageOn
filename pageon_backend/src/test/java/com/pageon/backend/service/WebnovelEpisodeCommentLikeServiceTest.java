package com.pageon.backend.service;

import com.pageon.backend.entity.*;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.repository.WebnovelEpisodeCommentLikeRepository;
import com.pageon.backend.repository.WebnovelEpisodeCommentRepository;
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
@DisplayName("WebnovelEpisodeCommentLikeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class WebnovelEpisodeCommentLikeServiceTest {
    @InjectMocks
    private WebnovelEpisodeCommentLikeService webnovelEpisodeCommentLikeService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WebnovelEpisodeCommentRepository webnovelEpisodeCommentRepository;
    @Mock
    private WebnovelEpisodeCommentLikeRepository webnovelEpisodeCommentLikeRepository;
    
    @BeforeEach
    void setUp() {
        webnovelEpisodeCommentLikeRepository.deleteAll();
    }
    
    @Test
    @DisplayName("댓글에 좋아요를 누르면 정상적으로 등록된다.")
    void shouldAddLike_whenUserLikesCommentForTheFirstTime() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).user(user).webnovelEpisode(webnovelEpisode).text(text).likeCount(10L).build();

        when(userRepository.getReferenceById(eq(userId))).thenReturn(user);
        when(webnovelEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(webnovelEpisodeCommentLikeRepository.existsByUser_IdAndWebnovelEpisodeComment_Id(userId, commentId)).thenReturn(false);

        ArgumentCaptor<WebnovelEpisodeCommentLike> likeCaptor = ArgumentCaptor.forClass(WebnovelEpisodeCommentLike.class);

        
        //when
        webnovelEpisodeCommentLikeService.createCommentLike(userId, commentId);
        
        // then
        verify(webnovelEpisodeCommentLikeRepository).save(likeCaptor.capture());
        WebnovelEpisodeCommentLike like = likeCaptor.getValue();

        assertEquals(11, comment.getLikeCount());
        verify(webnovelEpisodeCommentLikeRepository, times(1)).save(any(WebnovelEpisodeCommentLike.class));
        
    }
    
    @Test
    @DisplayName("존재하지 않는 댓글에 좋아요를 누르면 예외가 발생한다.")
    void shouldThrowException_whenLikingNonexistentComment() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();


        Long commentId = 200L;


        when(userRepository.getReferenceById(eq(userId))).thenReturn(user);
        when(webnovelEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentLikeService.createCommentLike(userId, commentId);
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

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).user(user).webnovelEpisode(webnovelEpisode).text(text).likeCount(10L).build();

        when(userRepository.getReferenceById(eq(userId))).thenReturn(user);
        when(webnovelEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(webnovelEpisodeCommentLikeRepository.existsByUser_IdAndWebnovelEpisodeComment_Id(userId, commentId)).thenReturn(true);

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentLikeService.createCommentLike(userId, commentId);
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

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).user(user).webnovelEpisode(webnovelEpisode).text(text).likeCount(10L).deletedAt(LocalDateTime.now()).build();

        when(userRepository.getReferenceById(eq(userId))).thenReturn(user);
        when(webnovelEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentLikeService.createCommentLike(userId, commentId);
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

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).user(user).webnovelEpisode(webnovelEpisode).text(text).likeCount(10L).build();

        WebnovelEpisodeCommentLike commentLike = WebnovelEpisodeCommentLike.builder().user(user).webnovelEpisodeComment(comment).build();

        when(webnovelEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(webnovelEpisodeCommentLikeRepository.findByUser_IdAndWebnovelEpisodeComment_Id(userId, commentId)).thenReturn(Optional.of(commentLike));
        
        //when
        webnovelEpisodeCommentLikeService.deleteCommentLike(userId, commentId);
        
        // then
        verify(webnovelEpisodeCommentLikeRepository, times(1)).delete(commentLike);
        assertEquals(9, comment.getLikeCount());
        
    }
    
    @Test
    @DisplayName("존재하지 않는 댓글의 좋아요를 삭제하려 하면 예외가 발생한다.")
    void shouldThrowException_whenUnlikingNonexistentComment() {
        // given
        Long userId = 1L;

        Long commentId = 200L;

        when(webnovelEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentLikeService.deleteCommentLike(userId, commentId);
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

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).user(user).webnovelEpisode(webnovelEpisode).text(text).likeCount(10L).deletedAt(LocalDateTime.now()).build();
        
        when(webnovelEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentLikeService.deleteCommentLike(userId, commentId);
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

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String text = "좋은 작품입니다.";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).user(user).webnovelEpisode(webnovelEpisode).text(text).likeCount(10L).build();

        WebnovelEpisodeCommentLike commentLike = WebnovelEpisodeCommentLike.builder().user(user).webnovelEpisodeComment(comment).build();

        when(webnovelEpisodeCommentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(webnovelEpisodeCommentLikeRepository.findByUser_IdAndWebnovelEpisodeComment_Id(userId, commentId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentLikeService.deleteCommentLike(userId, commentId);
        });

        // then
        assertEquals("사용자가 좋아요를 하지 않은 댓글입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_LIKE_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }


}