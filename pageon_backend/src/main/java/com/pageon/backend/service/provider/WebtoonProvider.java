package com.pageon.backend.service.provider;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.PurchaseType;
import com.pageon.backend.common.enums.SerialDay;
import com.pageon.backend.dto.response.CommentResponse;
import com.pageon.backend.dto.response.EpisodeResponse;
import com.pageon.backend.entity.*;
import com.pageon.backend.entity.base.EpisodeCommentBase;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import com.pageon.backend.service.EpisodePurchaseService;
import com.pageon.backend.service.WebtoonImageService;
import com.pageon.backend.service.handler.EpisodeActionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebtoonProvider implements ContentProvider {
    private final WebtoonRepository webtoonRepository;
    private final InterestRepository interestRepository;
    private final WebtoonEpisodeRepository webtoonEpisodeRepository;
    private final EpisodePurchaseRepository episodePurchaseRepository;
    private final EpisodeActionHandler actionHandler;
    private final WebtoonEpisodeRatingRepository webtoonEpisodeRatingRepository;
    private final WebtoonImageService webtoonImageService;
    private final WebtoonEpisodeCommentRepository webtoonEpisodeCommentRepository;
    private final WebtoonEpisodeCommentLikeRepository webtoonEpisodeCommentLikeRepository;

    @Override
    public boolean supports(String contentType) {
        return "webtoons".equals(contentType);
    }

    @Override
    public Optional<? extends Content> findById(Long contentId) {
        return webtoonRepository.findWithCreatorById(contentId);
    }

    @Override
    public List<EpisodeResponse.Summary> findEpisodes(Long userId, Long contentId) {

        log.info("Fetching episode list for content: {}. (User: {})", contentId, userId);
        List<WebtoonEpisode> episodes = webtoonEpisodeRepository.findByWebtoonId(contentId);
        if (userId == null) {
            return episodes.stream()
                    .map(e -> EpisodeResponse.Summary.fromEntity(e, null)).toList();
        } else {
            return episodes.stream().map(e -> {
                EpisodePurchase episodePurchase = episodePurchaseRepository.findByUser_IdAndContentIdAndEpisodeId(userId, contentId, e.getId()).orElse(null);
                return EpisodeResponse.Summary.fromEntity(
                        e,
                        (episodePurchase != null) ? EpisodeResponse.Purchase.fromEntity(episodePurchase) : null
                );
            }).toList();
        }
    }

    @Override
    public Page<? extends Content> findByKeyword(String keyword, Pageable pageable) {
        return webtoonRepository.findAllByKeyword(keyword, pageable);
    }

    @Override
    public Page<? extends Content> findByTitleOrPenName(String query, Pageable pageable) {
        return webtoonRepository.searchByTitleOrPenName(query, pageable);
    }

    @Override
    public Page<? extends Content> findNewArrivals(LocalDateTime since, Pageable pageable) {
        return webtoonRepository.findAllNewArrivals(since, pageable);
    }

    @Override
    public Page<? extends Content> findByStatusCompleted(Pageable pageable) {
        return webtoonRepository.findTopRatedCompleted(pageable);
    }

    @Override
    public Page<? extends Content> findBySerialDay(SerialDay serialDay, Pageable pageable) {
        return webtoonRepository.findOngoingBySerialDay(serialDay, pageable);
    }

    @Override
    public Page<Interest> findByInterest(Long userId, Pageable pageable) {
        return interestRepository.findWebtoonsByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Object findEpisodeDetail(Long userId, Long episodeId) {

        WebtoonEpisode episode = webtoonEpisodeRepository.findWithWebtoonById(episodeId).orElseThrow(
                () -> new CustomException(ErrorCode.EPISODE_NOT_FOUND)
        );

        actionHandler.handleViewEffects(userId, episode.getWebtoon(), episodeId, ContentType.WEBNOVEL);

        List<EpisodeResponse.EpisodeImage> images = webtoonImageService.getWebtoonImages(episodeId);
        Long prevId = webtoonEpisodeRepository.findPrevEpisodeId(episode.getWebtoon().getId(), episode.getEpisodeNum());
        Long nextId = webtoonEpisodeRepository.findNextEpisodeId(episode.getWebtoon().getId(), episode.getEpisodeNum());

        Integer userScore = webtoonEpisodeRatingRepository.findScoreByWebtoonEpisodeAndUser(episodeId, userId);

        return EpisodeResponse.WebtoonDetail.fromEntity(
                episode, episode.getWebtoon().getTitle(), images, prevId, nextId, userScore, findBestComment(episodeId)
        );
    }

    @Override
    public void rateEpisode(User user, Long episodeId, Integer score) {
        WebtoonEpisode episode = getWebtoonEpisode(episodeId);

        WebtoonEpisodeRating rating = WebtoonEpisodeRating.builder()
                .user(user)
                .webtoonEpisode(episode)
                .score(score)
                .build();

        webtoonEpisodeRatingRepository.save(rating);

        actionHandler.handleSaveRate(user.getId(), episode.getWebtoon(), ContentType.WEBTOON, episode, score);

    }

    @Override
    public void updateEpisodeRating(Long userId, Long episodeId, Integer newScore) {
        WebtoonEpisode episode = getWebtoonEpisode(episodeId);

        WebtoonEpisodeRating rating = webtoonEpisodeRatingRepository.findByWebtoonEpisodeAndUser(episodeId, userId).orElseThrow(
                () -> new CustomException(ErrorCode.EPISODE_RATING_NOT_FOUND)
        );
        Integer oldScore = rating.getScore();

        actionHandler.handleUpdateRate(episode.getWebtoon(), episode, rating, newScore, oldScore);
    }

    private WebtoonEpisode getWebtoonEpisode(Long episodeId) {
        WebtoonEpisode webtoonEpisode = webtoonEpisodeRepository.findWithWebtoonById(episodeId).orElseThrow(
                () -> new CustomException(ErrorCode.EPISODE_NOT_FOUND)
        );

        if (webtoonEpisode.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.EPISODE_IS_DELETED);
        }

        return webtoonEpisode;
    }

    @Override
    public void saveComment(User user, Long episodeId, String text, Boolean isSpoiler) {

        WebtoonEpisode episode = getWebtoonEpisode(episodeId);

        WebtoonEpisodeComment comment = WebtoonEpisodeComment.builder()
                .user(user)
                .webtoonEpisode(episode)
                .text(text)
                .isSpoiler(isSpoiler)
                .build();

        webtoonEpisodeCommentRepository.save(comment);

        actionHandler.handleSaveComment(user.getId(), episode.getWebtoon(), ContentType.WEBTOON);
    }

    @Override
    public void updateComment(Long userId, Long commentId, String text, Boolean isSpoiler) {
        WebtoonEpisodeComment comment = webtoonEpisodeCommentRepository.findByIdAndUser_Id(userId, commentId).orElseThrow(
                () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );

        comment.updateComment(text, isSpoiler);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        WebtoonEpisodeComment comment = webtoonEpisodeCommentRepository.findByIdAndUser_Id(userId, commentId).orElseThrow(
                () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );

        if (comment.getDeletedAt() != null) throw new CustomException(ErrorCode.COMMENT_ALREADY_DELETED);

        comment.deleteComment(LocalDateTime.now());
    }

    @Override
    public Page<? extends EpisodeCommentBase> findComments(Long episodeId, Pageable pageable) {
        return webtoonEpisodeCommentRepository.findAllByWebtoonEpisode_IdAndDeletedAtNull(episodeId, pageable);
    }

    @Override
    public Page<? extends EpisodeCommentBase> findMyComments(Long userId, Pageable pageable) {
        return webtoonEpisodeCommentRepository.findAllByUser_IdAndDeletedAtNull(userId, pageable);
    }

    @Override
    public CommentResponse.Best findBestComment(Long episodeId) {
        WebtoonEpisodeComment comment = webtoonEpisodeCommentRepository.findFirstByWebtoonEpisode_IdAndDeletedAtIsNullOrderByLikeCountDescCreatedAtDesc(episodeId).orElse(null);

        if (comment == null) {
            return CommentResponse.Best.fromEntity(null, 0L);
        }

        Long totalCount = webtoonEpisodeCommentRepository.countByWebtoonEpisode_IdAndDeletedAtIsNull(episodeId);

        if (comment.getLikeCount() == 0) {
            comment = null;
        }

        return CommentResponse.Best.fromEntity(comment, totalCount);
    }

    @Override
    public Set<Long> getLikedCommentIds(Long userId, List<Long> commentIds) {
        List<Long> likedComments = webtoonEpisodeCommentLikeRepository.findLikedCommentIdsByUserIdAndCommentIds(userId, commentIds);

        return new HashSet<>(likedComments);
    }

    @Override
    public Boolean hasLiked(Long userId, Long commentId) {
        return webtoonEpisodeCommentLikeRepository.existsByUser_IdAndWebtoonEpisodeComment_Id(userId, commentId);
    }

    @Override
    public void saveLike(User user, Long commentId) {
        WebtoonEpisodeComment comment = getEpisodeComment(commentId);

        WebtoonEpisodeCommentLike like = WebtoonEpisodeCommentLike.builder()
                .user(user)
                .webtoonEpisodeComment(comment)
                .build();

        webtoonEpisodeCommentLikeRepository.save(like);
        comment.updateLikeCount();
    }

    @Override
    public void deleteLike(Long userId, Long commentId) {
        WebtoonEpisodeComment comment = getEpisodeComment(commentId);
        WebtoonEpisodeCommentLike like = webtoonEpisodeCommentLikeRepository.findByUser_IdAndWebtoonEpisodeComment_Id(userId, commentId).orElseThrow(
                () -> new CustomException(ErrorCode.COMMENT_LIKE_NOT_FOUND)
        );

        webtoonEpisodeCommentLikeRepository.delete(like);

        comment.deleteLikeCount();
    }

    private WebtoonEpisodeComment getEpisodeComment(Long commentId) {
        WebtoonEpisodeComment comment = webtoonEpisodeCommentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );

        if (comment.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.COMMENT_ALREADY_DELETED);
        }

        return comment;
    }

    @Override
    public EpisodePurchaseService.EpisodeInfo getEpisodeInfo(Long episodeId, PurchaseType purchaseType) {
        WebtoonEpisode episode = getWebtoonEpisode(episodeId);

        if (episode.getParerntContent().getDeletedAt() != null) {
            throw new CustomException(ErrorCode.CONTENT_IS_DELETED);
        }

        Integer price = (purchaseType == PurchaseType.OWN) ? episode.getPurchasePrice() : episode.getRentalPrice();

        return new EpisodePurchaseService.EpisodeInfo(
                episode.getParerntContent().getId(),
                episode.getParerntContent().getTitle(),
                price,
                episode
        );
    }
}
