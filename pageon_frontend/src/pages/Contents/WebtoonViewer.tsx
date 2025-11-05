import React, { useEffect, useState, useRef } from "react";
import axios from "axios";
import { useLocation, useParams } from "react-router-dom";
import { WebtoonEpisodeDetail } from "../../types/Episodes";
import * as S from "./Viewer.styles"
import CommentList from "../../components/Comments/CommentList";
import fullStarIcon from "../../assets/fullStarIcon.png"
import halfFullStarIcon from "../../assets/halfFullStarIcon.png"
import emptyStarIcon from "../../assets/emptyStarIcon.png"
import api from "../../api/axiosInstance";
import BestComment from "../../components/Comments/BestComment";

function WebtoonViewer() {
    const { episodeId , contentId } = useParams();

    const location = useLocation();
    const restoreScrollY = location.state?.restoreScrollY;

    const [ episodeData, setEpisodeData ] = useState<WebtoonEpisodeDetail>({
        id: 0,
        title: "",
        episodeNum: 0,
        averageRating: 0.0,
        ratingCount: 0,
        images: [],
        prevEpisodeId: null,
        nextEpisodeId: null,
        userScore: null
    });

    const [showTitleSection, setShowTitleSection] = useState(true);
    const lastScrollY = useRef(0);

    useEffect(() => {
        async function fetchData(preserveScroll: boolean = false) {
            if (!episodeId) return;

            const savedY = window.scrollY;
            try {
                const response = await api.get(`/episodes/webtoon/${episodeId}`);
                setEpisodeData(response.data);
                setSelectedScore(response.data.userScore ?? 0);

                if (preserveScroll) {
                    window.scrollTo(0, savedY);
                } else {
                    window.scrollTo(0, 0);
                }
            } catch (err) {
                alert("에피소드 정보를 불러오지 못했습니다.");
                return;
            }
        }

        // 에피소드 이동 시 선택 점수 초기화
        setSelectedScore(0);
        fetchData(false);
    }, [episodeId]);


    useEffect(() => {
        const handleScroll = () => {
            const currentScrollY = window.scrollY;
            const isScrollingDown = currentScrollY > lastScrollY.current;

            if (currentScrollY <= 0) {
                setShowTitleSection(true);
            } else if (isScrollingDown) {
                setShowTitleSection(false);
            } else {
                setShowTitleSection(true);
            }

            lastScrollY.current = currentScrollY;
        };

        window.addEventListener("scroll", handleScroll, { passive: true });
        return () => {
            window.removeEventListener("scroll", handleScroll);
        };
    }, []);

    const [isRatingOpen, setIsRatingOpen] = useState(false);
    const [selectedScore, setSelectedScore] = useState<number | null>(null);

    const getDisplayScore = () => (selectedScore ?? 0);

    const getStarIcon = (starIndex: number, score: number) => {
        const fullThreshold = starIndex * 2;
        const halfThreshold = fullThreshold - 1;
        if (score >= fullThreshold) return fullStarIcon;
        if (score >= halfThreshold) return halfFullStarIcon;
        return emptyStarIcon;
    };

    const computeHalfOrFull = (e: React.MouseEvent<HTMLDivElement>): 1 | 2 => {
        const width = (e.currentTarget as HTMLDivElement).clientWidth;
        const offsetX = (e.nativeEvent as MouseEvent).offsetX;
        return offsetX < width / 2 ? 1 : 2;
    };

    const handleStarClick = (idx: number) => (e: React.MouseEvent<HTMLDivElement>) => {
        const half = computeHalfOrFull(e);
        const score = half === 1 ? idx * 2 - 1 : idx * 2;
        setSelectedScore(score);
    };

    const handleConfirmRating = async () => {
        const savedY = window.scrollY;
        const currentUserScore = episodeData.userScore ?? 0;
        if (selectedScore === currentUserScore) {
            setIsRatingOpen(false);
            window.scrollTo(0, savedY);
            return;
        }
        try {
            await api.post("/rating", {
                contentType: "WEBTOON",
                episodeId: episodeData.id,
                score: selectedScore
            });
            // 최신 평점 반영 및 스크롤 유지
            if (episodeId) {
                const response = await api.get(`/episodes/webtoon/${episodeId}`);
                setEpisodeData(response.data);
            }
        } catch (error) {
            console.error("평점 등록 실패: ", error);
        }
        setIsRatingOpen(false);
        window.scrollTo(0, savedY);
    };

    const handleUpdateRating = async () => {
        const savedY = window.scrollY;
        const currentUserScore = episodeData.userScore ?? 0;
        if (selectedScore === currentUserScore) {
            setIsRatingOpen(false);
            window.scrollTo(0, savedY);
            return;
        }
        try {
            await api.patch("/rating", {
                contentType: "WEBTOON",
                episodeId: episodeData.id,
                score: selectedScore
            });
            if (episodeId) {
                const response = await api.get(`/episodes/webtoon/${episodeId}`);
                setEpisodeData(response.data);
                setSelectedScore(response.data.userScore ?? 0);
            }
        } catch (error) {
            console.error("평점 수정 실패: ", error);
        }
        setIsRatingOpen(false);
        window.scrollTo(0, savedY);
    };

    const [comments] = useState([
        {
          id: 1,
          bookTitle: "작품 제목 1",
          bookCover: "https://d2ge55k9wic00e.cloudfront.net/webnovels/1/webnovel1.png",
          content: "정말 재미있게 읽었습니다! 다음 편도 기대돼요.",
          episodeNum: 12,
          nickname: "닉네임1",
          date: "2024-06-01",
          likes: 12
        }
    ]);

    if (!episodeId || !contentId) {
        return null;
    }
    

    return (
        <S.Viewer>
            <S.EpisodeTitleSection isVisible={showTitleSection}>
                <S.EpisodeTitleContainer>
                    <S.WebnovelTitle to={`/webtoons/${contentId}`}>{episodeData.episodeNum}화 {episodeData.title}</S.WebnovelTitle>
                    <S.EpisodeLinkContainer>
                        <S.EpisodeLink to={`/webtoons/${contentId}/viewer/${episodeData.prevEpisodeId}`} $disabled={episodeData.prevEpisodeId === null} aria-disabled={episodeData.prevEpisodeId === null}>이전화</S.EpisodeLink>
                        <S.EpisodeLink to={`/webtoons/${contentId}/viewer/${episodeData.nextEpisodeId}`} $disabled={episodeData.nextEpisodeId === null} aria-disabled={episodeData.nextEpisodeId === null}>다음화</S.EpisodeLink>
                    </S.EpisodeLinkContainer>
                </S.EpisodeTitleContainer>
            </S.EpisodeTitleSection>
            <S.ViewerBodySection onClick={() => setShowTitleSection(prev => !prev)}>
                <S.EpisodeContentContainer>
                    <S.EpisodeViewerContents>
                        <S.ContentWrapper>
                            <S.EpisodeContent>
                                {episodeData.images.map((image) => (
                                    <S.EpisodeThumbnailImage key={image.id} src={image.imageUrl} alt={image.sequence.toString()} />
                                ))}
                            </S.EpisodeContent>
                        </S.ContentWrapper>
                    </S.EpisodeViewerContents>
                    <S.EpisodeContentFooter>
                    <S.WraningMsg>
                    ※ 본 저작물의 권리는 저작권자에게 있습니다. 저작물을 복사, 복제, 수정, 배포할 경우 형사상 처벌 및 민사상 책임을 질 수 있습니다.
                    </S.WraningMsg>
                </S.EpisodeContentFooter>
                </S.EpisodeContentContainer>
                
            </S.ViewerBodySection>
            <S.ViewerRatingSection>
                <S.ViewerRatingScore>
                    <S.RatingFullStarIcon src={fullStarIcon} />
                    <S.ViewerAverageRatingScore>{Number(episodeData.averageRating ?? 0).toFixed(1)}</S.ViewerAverageRatingScore>
                    <S.ViewerRatingCount>{episodeData.ratingCount ?? 0}</S.ViewerRatingCount>
                </S.ViewerRatingScore>
                <S.ViewerRatingCreateBtn onClick={() => { setSelectedScore((episodeData.userScore ?? 0) as number); setIsRatingOpen(true); }}>
                    별점 주기
                </S.ViewerRatingCreateBtn>
            </S.ViewerRatingSection>
            {isRatingOpen && (
                <S.RatingModalOverlay onClick={() => { setSelectedScore(0); setIsRatingOpen(false); }}>
                    <S.RatingModal onClick={(e) => e.stopPropagation()}>
                        <S.RatingModalTitle>에피소드 별점 남기기</S.RatingModalTitle>
                        <S.RatingStars>
                            {[1,2,3,4,5].map((i) => (
                                <S.RatingStarWrapper
                                    key={i}
                                    onClick={handleStarClick(i)}
                                >
                                    <S.RatingStarImage src={getStarIcon(i, getDisplayScore())} />
                                </S.RatingStarWrapper>
                            ))}
                        </S.RatingStars>
                        <S.RatingScoreText>{getDisplayScore()}</S.RatingScoreText>
                        <S.RatingModalActions>
                            <S.RatingCancelBtn onClick={() => { setSelectedScore(0); setIsRatingOpen(false); }}>취소</S.RatingCancelBtn>
                            <S.RatingConfirmBtn onClick={(episodeData.userScore !== null && episodeData.userScore !== 0) ? handleUpdateRating : handleConfirmRating} disabled={selectedScore === null}>
                                {episodeData.userScore !== null && episodeData.userScore !== 0 ? "수정" : "확인"}
                            </S.RatingConfirmBtn>
                        </S.RatingModalActions>
                    </S.RatingModal>
                </S.RatingModalOverlay>
            )}
            <S.ViewerCommentSection>
                <BestComment comment = {comments[0]!} contentType="webtoons" contentId={contentId} episodeId={episodeId}/>
            </S.ViewerCommentSection>

            <S.ViewerNextEpisodeBtnSection>
                <S.ViewerNextEpisodeBtnContainer>
                    <S.ViewerNextEpisodeBtn to={`/webtoons/${contentId}/viewer/${episodeData.nextEpisodeId}`}>다음화 보기</S.ViewerNextEpisodeBtn>
                </S.ViewerNextEpisodeBtnContainer>
            </S.ViewerNextEpisodeBtnSection>
        </S.Viewer>
    )

}

export default WebtoonViewer;