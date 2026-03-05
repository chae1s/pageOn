package com.pageon.backend.service.provider;

import com.pageon.backend.common.enums.PurchaseType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.CommentResponse;
import com.pageon.backend.dto.response.EpisodeListResponse;
import com.pageon.backend.dto.response.EpisodeResponse;
import com.pageon.backend.entity.Content;
import com.pageon.backend.entity.Interest;
import com.pageon.backend.entity.User;
import com.pageon.backend.entity.base.EpisodeCommentBase;
import com.pageon.backend.repository.ContentRepository;
import com.pageon.backend.repository.InterestRepository;
import com.pageon.backend.service.EpisodePurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AllContentProvider implements ContentProvider {
    private final ContentRepository contentRepository;
    private final InterestRepository interestRepository;

    @Override
    public boolean supports(String contentType) {
        return "all".equals(contentType);
    }

    @Override
    public Optional<? extends Content> findById(Long contentId) {
        return Optional.empty();
    }

    @Override
    public List<EpisodeResponse.Summary> findEpisodes(Long userId, Long contentId) {
        return List.of();
    }

    @Override
    public Page<? extends Content> findByKeyword(String keyword, Pageable pageable) {
        return null;
    }

    @Override
    public Page<? extends Content> findByTitleOrPenName(String query, Pageable pageable) {
        return contentRepository.searchByTitleOrPenName(query, pageable);
    }

    @Override
    public Page<? extends Content> findNewArrivals(LocalDateTime since, Pageable pageable) {
        return null;
    }

    @Override
    public Page<? extends Content> findByStatusCompleted(Pageable pageable) {
        return contentRepository.findTopRatedCompleted(pageable);
    }

    @Override
    public Page<? extends Content> findBySerialDay(SerialDay serialDay, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Interest> findByInterest(Long userId, Pageable pageable) {
        return interestRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Object findEpisodeDetail(Long userId, Long episodeId) {
        return null;
    }

    @Override
    public void rateEpisode(User user, Long episodeId, Integer score) {

    }

    @Override
    public void updateEpisodeRating(Long userId, Long episodeId, Integer newScore) {

    }

    @Override
    public void saveComment(User user, Long episodeId, String text, Boolean isSpoiler) {

    }

    @Override
    public void updateComment(Long userId, Long commentId, String text, Boolean isSpoiler) {

    }

    @Override
    public void deleteComment(Long userId, Long commentId) {

    }

    @Override
    public Page<? extends EpisodeCommentBase> findComments(Long episodeId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<? extends EpisodeCommentBase> findMyComments(Long userId, Pageable pageable) {
        return null;
    }

    @Override
    public CommentResponse.Best findBestComment(Long episodeId) {
        return null;
    }

    @Override
    public Set<Long> getLikedCommentIds(Long userId, List<Long> commentIds) {
        return Set.of();
    }

    @Override
    public Boolean hasLiked(Long userId, Long commentId) {
        return null;
    }

    @Override
    public void saveLike(User user, Long commentId) {

    }

    @Override
    public void deleteLike(Long userId, Long commentId) {

    }

    @Override
    public EpisodePurchaseService.EpisodeInfo getEpisodeInfo(Long episodeId, PurchaseType purchaseType) {
        return null;
    }
}
