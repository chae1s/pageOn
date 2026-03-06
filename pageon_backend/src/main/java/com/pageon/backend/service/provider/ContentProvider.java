package com.pageon.backend.service.provider;

import com.pageon.backend.common.enums.PurchaseType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.request.EpisodeRatingRequest;
import com.pageon.backend.dto.response.CommentResponse;
import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.dto.response.EpisodeResponse;
import com.pageon.backend.entity.Content;
import com.pageon.backend.entity.Interest;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.base.EpisodeCommentBase;
import com.pageon.backend.service.EpisodePurchaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ContentProvider {
    boolean supports(String contentType);

    Optional<? extends Content> findById(Long contentId);
    List<EpisodeResponse.Summary> findEpisodes(Long userId, Long contentId);

    // 검색 및 목록 조회
    Page<? extends Content> findByKeyword(String keyword, Pageable pageable);
    Page<? extends Content> findByTitleOrPenName(String query, Pageable pageable);
    Page<? extends Content> findNewArrivals(LocalDateTime since, Pageable pageable);

    Page<? extends Content> findByStatusCompleted(Pageable pageable);
    Page<? extends Content> findBySerialDay(SerialDay serialDay, Pageable pageable);

    Page<Interest> findByInterest(Long userId, Pageable pageable);

    Object findEpisodeDetail(Long userId, Long episodeId);

    // 에피소드 평점
    void rateEpisode(User user, Long episodeId, Integer score);

    void updateEpisodeRating(Long userId, Long episodeId, Integer newScore);

    // 에피소드 댓글
    void saveComment(User user, Long episodeId, String text, Boolean isSpoiler);
    void updateComment(Long userId, Long commentId, String text, Boolean isSpoiler);
    void deleteComment(Long userId, Long commentId);

    Page<? extends EpisodeCommentBase> findComments(Long episodeId, Pageable pageable);
    Page<? extends EpisodeCommentBase> findMyComments(Long userId, Pageable pageable);

    CommentResponse.Best findBestComment(Long episodeId);
    Set<Long> getLikedCommentIds(Long userId, List<Long> commentIds);

    Boolean hasLiked(Long userId, Long commentId);
    void saveLike(User user, Long commentId);
    void deleteLike(Long userId, Long commentId);

    // 에피소드 구매
    EpisodePurchaseService.EpisodeInfo getEpisodeInfo(Long episodeId, PurchaseType purchaseType);
}
