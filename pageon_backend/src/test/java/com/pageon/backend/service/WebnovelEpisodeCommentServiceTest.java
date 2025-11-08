package com.pageon.backend.service;

import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.dto.response.EpisodeCommentResponse;
import com.pageon.backend.dto.response.MyCommentResponse;
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
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").isDeleted(true).build();

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
    
    @Test
    @DisplayName("콘텐츠의 댓글 리스트를 정상적으로 반환한다.")
    void shouldReturnCommentList_whenValidEpisodeIdProvided() {
        // given
        Long userId1 = 1L;
        User user1 = User.builder().id(userId1).email("test1@mail.com").nickname("로그인작성자").deleted(false).build();

        Long userId2 = 1L;
        User user2 = User.builder().id(userId2).email("test2@mail.com").nickname("비로그인작성자").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2);

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();

        WebnovelEpisodeComment comment1 = WebnovelEpisodeComment.builder().id(commentId1).user(user1).webnovelEpisode(webnovelEpisode).text(text).build();
        WebnovelEpisodeComment comment2 = WebnovelEpisodeComment.builder().id(commentId2).user(user2).webnovelEpisode(webnovelEpisode).text(text).build();

        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));

        when(webnovelEpisodeCommentRepository.findAllByWebnovelEpisode_IdAndDeletedAtNull(eq(episodeId), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comment1, comment2), pageable, 2));
        
        //when
        Page<EpisodeCommentResponse> result = webnovelEpisodeCommentService.getCommentsByEpisodeId(userId1, episodeId, pageable, "popular");
        
        // then
        assertEquals(2, result.getContent().size());
        assertEquals(commentId1, result.getContent().get(0).getId());
        assertEquals("로그인작성자", result.getContent().get(0).getNickname());
        
    }

    @Test
    @DisplayName("댓글이 존재하지 않으면 빈 리스트를 반환한다.")
    void shouldReturnEmptyList_whenNoCommentsExist() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "likeCount"));

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();

        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));

        when(webnovelEpisodeCommentRepository.findAllByWebnovelEpisode_IdAndDeletedAtNull(eq(episodeId), any(Pageable.class))).thenReturn(Page.empty());

        //when
        Page<EpisodeCommentResponse> result = webnovelEpisodeCommentService.getCommentsByEpisodeId(userId, episodeId, pageable, "popular");

        // then
        assertEquals(0, result.getContent().size());


    }

    @Test
    @DisplayName("삭제된 에피소드의 댓글을 조회하면 예외가 발생한다.")
    void shouldThrowException_whenCommentSearchEpisodeIsDeleted() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "likeCount"));

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").isDeleted(true).build();


        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentService.getCommentsByEpisodeId(userId, episodeId, pageable, "popular");
        });

        // then
        assertEquals("삭제된 에피소드입니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.EPISODE_IS_DELETED, ErrorCode.valueOf(exception.getErrorCode()));


    }

    @Test
    @DisplayName("댓글 리스트는 좋아요 기준 내림차순으로 정렬되어 반환된다.")
    void shouldReturnCommentsSortedByLikeCountDesc() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "likeCount"));

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();

        WebnovelEpisodeComment comment1 = WebnovelEpisodeComment.builder().id(commentId1).user(user).webnovelEpisode(webnovelEpisode).text(text).likeCount(10L).build();
        WebnovelEpisodeComment comment2 = WebnovelEpisodeComment.builder().id(commentId2).user(user).webnovelEpisode(webnovelEpisode).text(text).likeCount(100L).build();


        when(webnovelEpisodeRepository.findByIdWithWebnovel(episodeId)).thenReturn(Optional.of(webnovelEpisode));

        when(webnovelEpisodeCommentRepository.findAllByWebnovelEpisode_IdAndDeletedAtNull(eq(episodeId), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comment1, comment2), pageable, 2));

        //when
        Page<EpisodeCommentResponse> result = webnovelEpisodeCommentService.getCommentsByEpisodeId(userId, episodeId, pageable, "popular");

        // then
        assertEquals(2, result.getContent().size());
        assertEquals(commentId1, result.getContent().get(0).getId());
        assertEquals(commentId2, result.getContent().get(1).getId());

    }

    @Test
    @DisplayName("내가 작성한 댓글 리스트 조회 시 각 댓글에 콘텐츠의 정보가 포함된다.")
    void shouldReturnMyComments_whenUserHasComments() {
        // given
        Long userId1 = 1L;
        User user1 = User.builder().id(userId1).email("test1@mail.com").nickname("로그인작성자").deleted(false).build();

        Long userId2 = 1L;
        User user2 = User.builder().id(userId2).email("test2@mail.com").nickname("비로그인작성자").deleted(false).build();

        Long webnovelId1 = 10L;
        Long webnovelId2 = 11L;

        Long episodeId1 = 100L;
        Long episodeId2 = 101L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;
        Long commentId3 = 220L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        Webnovel webnovel1 = Webnovel.builder().id(webnovelId1).title("테스트 웹소설1").deleted(false).build();
        Webnovel webnovel2 = Webnovel.builder().id(webnovelId2).title("테스트 웹소설2").deleted(false).build();

        WebnovelEpisode webnovelEpisode1 = WebnovelEpisode.builder().id(episodeId1).webnovel(webnovel1).episodeTitle("테스트 웹소설 에피소드").build();
        WebnovelEpisode webnovelEpisode2 = WebnovelEpisode.builder().id(episodeId2).webnovel(webnovel2).episodeTitle("테스트 웹소설 에피소드").build();

        WebnovelEpisodeComment comment1 = WebnovelEpisodeComment.builder().id(commentId1).user(user1).webnovelEpisode(webnovelEpisode1).text(text).build();
        WebnovelEpisodeComment comment2 = WebnovelEpisodeComment.builder().id(commentId2).user(user1).webnovelEpisode(webnovelEpisode2).text(text).build();
        WebnovelEpisodeComment comment3 = WebnovelEpisodeComment.builder().id(commentId3).user(user2).webnovelEpisode(webnovelEpisode1).text(text).build();


        when(webnovelEpisodeCommentRepository.findAllByUser_IdAndDeletedAtNull(eq(userId1), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comment1, comment2), pageable, 2));

        //when
        Page<MyCommentResponse> result = webnovelEpisodeCommentService.getCommentsByUserId(userId1, pageable);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals(commentId1, result.getContent().get(0).getId());
        assertEquals("테스트 웹소설1", result.getContent().get(0).getContentTitle());


    }


    @Test
    @DisplayName("삭제된 댓글은 내 댓글 리스트에서 제외된다.")
    void shouldExcludeDeletedComments_whenFetchingMyComments() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test1@mail.com").nickname("닉네임").deleted(false).build();


        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;
        Long commentId3 = 220L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").build();


        WebnovelEpisodeComment comment1 = WebnovelEpisodeComment.builder().id(commentId1).user(user).webnovelEpisode(webnovelEpisode).text(text).build();
        WebnovelEpisodeComment comment2 = WebnovelEpisodeComment.builder().id(commentId2).user(user).webnovelEpisode(webnovelEpisode).text(text).build();
        WebnovelEpisodeComment comment3 = WebnovelEpisodeComment.builder().id(commentId3).user(user).webnovelEpisode(webnovelEpisode).text(text).deletedAt(LocalDateTime.now()).build();


        when(webnovelEpisodeCommentRepository.findAllByUser_IdAndDeletedAtNull(eq(userId), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comment1, comment2), pageable, 2));

        //when
        Page<MyCommentResponse> result = webnovelEpisodeCommentService.getCommentsByUserId(userId, pageable);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals(commentId1, result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("댓글 내용을 수정하면 정상적으로 변경된다.")
    void shouldUpdateCommentText_whenValidRequestProvided() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String oldText = "기존 댓글 내용";
        String newText = "수정 댓글 내용";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").isDeleted(true).build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).text(oldText).user(user).webnovelEpisode(webnovelEpisode).build();

        ContentEpisodeCommentRequest commentRequest = new ContentEpisodeCommentRequest(newText, false);

        when(webnovelEpisodeCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        webnovelEpisodeCommentService.updateComment(userId, commentId, commentRequest);

        // then
        assertEquals(newText, comment.getText());
    }

    @Test
    @DisplayName("존재하지 않는 댓글을 수정하면 예외가 발생한다.")
    void shouldThrowException_whenCommentDoesNotExist() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String oldText = "기존 댓글 내용";
        String newText = "수정 댓글 내용";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").isDeleted(true).build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).text(oldText).user(user).webnovelEpisode(webnovelEpisode).build();

        ContentEpisodeCommentRequest commentRequest = new ContentEpisodeCommentRequest(newText, false);

        when(webnovelEpisodeCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentService.updateComment(userId, commentId, commentRequest);
        });

        // then
        assertEquals("존재하지 않는 댓글입니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("본인 댓글이 아닌 댓글을 수정하려고 하면 예외가 발생한다.")
    void shouldThrowException_whenUserIsNotOwnerOfComment() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long writerId = 2L;
        User writer = User.builder().id(writerId).email("writer@mail.com").nickname("댓글작성자").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String oldText = "기존 댓글 내용";
        String newText = "수정 댓글 내용";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").isDeleted(true).build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).text(oldText).user(writer).webnovelEpisode(webnovelEpisode).build();

        ContentEpisodeCommentRequest commentRequest = new ContentEpisodeCommentRequest(newText, false);

        when(webnovelEpisodeCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentService.updateComment(userId, commentId, commentRequest);
        });


        // then
        assertEquals("본인 댓글만 수정할 수 있습니다.", exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_FORBIDDEN, ErrorCode.valueOf(exception.getErrorCode()));

    }

    @Test
    @DisplayName("수정한 댓글 내용이 비어 있으면 예외가 발생한다.")
    void shouldThrowException_whenContentIsBlank() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webnovelId = 10L;
        Long episodeId = 100L;

        Long commentId = 200L;

        String oldText = "기존 댓글 내용";
        String newText = "";

        Webnovel webnovel = Webnovel.builder().id(webnovelId).title("테스트 웹소설").deleted(false).build();
        WebnovelEpisode webnovelEpisode = WebnovelEpisode.builder().id(episodeId).webnovel(webnovel).episodeTitle("테스트 웹소설 에피소드").isDeleted(true).build();
        WebnovelEpisodeComment comment = WebnovelEpisodeComment.builder().id(commentId).text(oldText).user(user).webnovelEpisode(webnovelEpisode).build();

        ContentEpisodeCommentRequest commentRequest = new ContentEpisodeCommentRequest(newText, false);

        when(webnovelEpisodeCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webnovelEpisodeCommentService.updateComment(userId, commentId, commentRequest);
        });

        // then
        assertEquals("댓글 내용이 존재하지 않습니다.",  exception.getErrorMessage());
        assertEquals(ErrorCode.COMMENT_TEXT_IS_BLANK, ErrorCode.valueOf(exception.getErrorCode()));

    }
    
    @Test
    @DisplayName("본인 댓글 삭제 시 정상적으로 삭제된다.")
    void shouldSoftDeleteComment_whenUserIsOwner() {
        // given


        //when


        // then


    }

    @Test
    @DisplayName("존재하지 않는 댓글을 삭제하면 예외가 발생한다. ")
    void shouldThrowException_whenCommentDoesNotExistDuringDelete() {
        // given


        //when


        // then


    }

    @Test
    @DisplayName("이미 삭제된 댓글을 다시 삭제하려 하면 예외가 발생한다.")
    void shouldThrowException_whenCommentAlreadyDeleted() {
        // given


        //when


        // then


    }

    @Test
    @DisplayName("본인 댓글이 아닌 댓글을 삭제하려 하면 예외가 발생한다.")
    void shouldThrowException_whenUserIsNotOwnerOfCommentDuringDelete() {
        // given


        //when


        // then


    }

    @Test
    @DisplayName("댓글 삭제 후 DeletedAt 값이 변경된다.")
    void shouldSetDeletedNotNull_whenCommentDeleted() {
        // given


        //when


        // then


    }


}