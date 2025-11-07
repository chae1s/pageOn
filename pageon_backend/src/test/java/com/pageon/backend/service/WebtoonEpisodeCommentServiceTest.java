package com.pageon.backend.service;

import com.pageon.backend.dto.request.ContentEpisodeCommentRequest;
import com.pageon.backend.dto.response.EpisodeCommentResponse;
import com.pageon.backend.dto.response.MyCommentResponse;
import com.pageon.backend.entity.*;
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
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").isDeleted(true).build();

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

    @Test
    @DisplayName("콘텐츠의 댓글 리스트를 정상적으로 반환한다.")
    void shouldReturnCommentList_whenValidEpisodeIdProvided() {
        // given
        Long userId1 = 1L;
        User user1 = User.builder().id(userId1).email("test1@mail.com").nickname("로그인작성자").deleted(false).build();

        Long userId2 = 1L;
        User user2 = User.builder().id(userId2).email("test2@mail.com").nickname("비로그인작성자").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2);

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").deleted(false).build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();

        WebtoonEpisodeComment comment1 = WebtoonEpisodeComment.builder().id(commentId1).user(user1).webtoonEpisode(webtoonEpisode).text(text).build();
        WebtoonEpisodeComment comment2 = WebtoonEpisodeComment.builder().id(commentId2).user(user2).webtoonEpisode(webtoonEpisode).text(text).build();

        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        when(webtoonEpisodeCommentRepository.findAllByWebtoonEpisode_IdAndIsDeletedFalse(eq(episodeId), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comment1, comment2), pageable, 2));

        //when
        Page<EpisodeCommentResponse> result = webtoonEpisodeCommentService.getCommentsByEpisodeId(userId1, episodeId, pageable, "popular");

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

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "likeCount"));

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").deleted(false).build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();

        WebtoonEpisodeComment comment1 = WebtoonEpisodeComment.builder().id(commentId1).user(user).webtoonEpisode(webtoonEpisode).text(text).build();
        WebtoonEpisodeComment comment2 = WebtoonEpisodeComment.builder().id(commentId2).user(user).webtoonEpisode(webtoonEpisode).text(text).build();


        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        when(webtoonEpisodeCommentRepository.findAllByWebtoonEpisode_IdAndIsDeletedFalse(eq(episodeId), any(Pageable.class))).thenReturn(Page.empty());

        //when
        Page<EpisodeCommentResponse> result = webtoonEpisodeCommentService.getCommentsByEpisodeId(userId, episodeId, pageable, "popular");

        // then
        assertEquals(0, result.getContent().size());


    }

    @Test
    @DisplayName("삭제된 에피소드의 댓글을 조회하면 예외가 발생한다.")
    void shouldThrowException_whenCommentSearchEpisodeIsDeleted() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@mail.com").nickname("테스트").deleted(false).build();

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "likeCount"));

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").deleted(false).build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").isDeleted(true).build();


        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            webtoonEpisodeCommentService.getCommentsByEpisodeId(userId, episodeId, pageable, "popular");
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

        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "likeCount"));

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").deleted(false).build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();

        WebtoonEpisodeComment comment1 = WebtoonEpisodeComment.builder().id(commentId1).user(user).webtoonEpisode(webtoonEpisode).text(text).likeCount(10L).build();
        WebtoonEpisodeComment comment2 = WebtoonEpisodeComment.builder().id(commentId2).user(user).webtoonEpisode(webtoonEpisode).text(text).likeCount(100L).build();


        when(webtoonEpisodeRepository.findByIdWithWebtoon(episodeId)).thenReturn(Optional.of(webtoonEpisode));

        when(webtoonEpisodeCommentRepository.findAllByWebtoonEpisode_IdAndIsDeletedFalse(eq(episodeId), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comment1, comment2), pageable, 2));

        //when
        Page<EpisodeCommentResponse> result = webtoonEpisodeCommentService.getCommentsByEpisodeId(userId, episodeId, pageable, "popular");

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

        Long webtoonId1 = 10L;
        Long webtoonId2 = 11L;

        Long episodeId1 = 100L;
        Long episodeId2 = 101L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;
        Long commentId3 = 220L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        Webtoon webtoon1 = Webtoon.builder().id(webtoonId1).title("테스트 웹툰1").deleted(false).build();
        Webtoon webtoon2 = Webtoon.builder().id(webtoonId2).title("테스트 웹툰2").deleted(false).build();

        WebtoonEpisode webtoonEpisode1 = WebtoonEpisode.builder().id(episodeId1).webtoon(webtoon1).episodeTitle("테스트 웹툰 에피소드").build();
        WebtoonEpisode webtoonEpisode2 = WebtoonEpisode.builder().id(episodeId2).webtoon(webtoon2).episodeTitle("테스트 웹툰 에피소드").build();

        WebtoonEpisodeComment comment1 = WebtoonEpisodeComment.builder().id(commentId1).user(user1).webtoonEpisode(webtoonEpisode1).text(text).build();
        WebtoonEpisodeComment comment2 = WebtoonEpisodeComment.builder().id(commentId2).user(user1).webtoonEpisode(webtoonEpisode2).text(text).build();
        WebtoonEpisodeComment comment3 = WebtoonEpisodeComment.builder().id(commentId3).user(user2).webtoonEpisode(webtoonEpisode1).text(text).build();


        when(webtoonEpisodeCommentRepository.findAllByUser_IdAndIsDeletedFalse(eq(userId1), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comment1, comment2), pageable, 2));

        //when
        Page<MyCommentResponse> result = webtoonEpisodeCommentService.getCommentsByUserId(userId1, pageable);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals(commentId1, result.getContent().get(0).getId());
        assertEquals("테스트 웹툰1", result.getContent().get(0).getContentTitle());


    }


    @Test
    @DisplayName("삭제된 댓글은 내 댓글 리스트에서 제외된다.")
    void shouldExcludeDeletedComments_whenFetchingMyComments() {
        // given
        Long userId = 1L;
        User user = User.builder().id(userId).email("test1@mail.com").nickname("닉네임").deleted(false).build();


        Long webtoonId = 10L;
        Long episodeId = 100L;

        Long commentId1 = 200L;
        Long commentId2 = 210L;
        Long commentId3 = 220L;

        String text = "좋은 작품입니다.";

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        Webtoon webtoon = Webtoon.builder().id(webtoonId).title("테스트 웹툰").deleted(false).build();
        WebtoonEpisode webtoonEpisode = WebtoonEpisode.builder().id(episodeId).webtoon(webtoon).episodeTitle("테스트 웹툰 에피소드").build();

        WebtoonEpisodeComment comment1 = WebtoonEpisodeComment.builder().id(commentId1).user(user).webtoonEpisode(webtoonEpisode).text(text).isDeleted(false).build();
        WebtoonEpisodeComment comment2 = WebtoonEpisodeComment.builder().id(commentId2).user(user).webtoonEpisode(webtoonEpisode).text(text).isDeleted(false).build();
        WebtoonEpisodeComment comment3 = WebtoonEpisodeComment.builder().id(commentId3).user(user).webtoonEpisode(webtoonEpisode).text(text).isDeleted(true).build();


        when(webtoonEpisodeCommentRepository.findAllByUser_IdAndIsDeletedFalse(eq(userId), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comment1, comment2), pageable, 2));

        //when
        Page<MyCommentResponse> result = webtoonEpisodeCommentService.getCommentsByUserId(userId, pageable);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals(commentId1, result.getContent().get(0).getId());
    }

}