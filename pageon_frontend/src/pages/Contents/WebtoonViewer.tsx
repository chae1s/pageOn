import React, { useEffect, useState, useRef } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import { WebtoonEpisodeDetail } from "../../types/Episodes";
import * as S from "./Viewer.styles"
import CommentList from "../../components/Comments/CommentList";
import fullStarIcon from "../../assets/fullStarIcon.png"
import halfFullStarIcon from "../../assets/halfFullStarIcon.png"
import emptyStarIcon from "../../assets/emptyStarIcon.png"
import api from "../../api/axiosInstance";

function WebtoonViewer() {
    const { episodeId , contentId } = useParams();
    const [ episodeData, setEpisodeData ] = useState<WebtoonEpisodeDetail>({
        id: 0,
        title: "",
        episodeNum: 0,
        images: [],
        prevEpisodeId: null,
        nextEpisodeId: null
    });

    const [showTitleSection, setShowTitleSection] = useState(true);
    const lastScrollY = useRef(0);

    useEffect(() => {
        async function fetchData() {
            if (!episodeId) return;

            try {
                const response = await axios.get(`/api/episodes/webtoon/${episodeId}`);
                setEpisodeData(response.data);

                console.log(response.data);
                window.scrollTo(0, 0);
            } catch (err) {
                alert("에피소드 정보를 불러오지 못했습니다.");
                return;
            }
        }

        fetchData();
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
        try {
            await api.post("/rating", {
                contentType: "WEBTOON",
                episodeId: episodeData.id,
                score: selectedScore
            });
        } catch (error) {
            console.error("평점 등록 실패: ", error);
        }
        setIsRatingOpen(false);
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
        },
        {
          id: 2,
          bookTitle: "작품 제목 2",
          bookCover: "https://d2ge55k9wic00e.cloudfront.net/webnovels/1/webnovel1.png",
          content: "스토리가 신선해서 좋았어요.",
          episodeNum: 3,
          nickname: "닉네임2",
          date: "2024-05-28",
          likes: 5
        },
        {
          id: 3,
          bookTitle: "작품 제목 3",
          bookCover: "https://via.placeholder.com/60x80?text=작품+3",
          content: "그림체가 마음에 들어요.",
          episodeNum: 7,
          nickname: "닉네임3",
          date: "2024-05-20",
          likes: 8
        }
      ]);

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
                    <S.ViewerAverageRatingScore>10.0</S.ViewerAverageRatingScore>
                    <S.ViewerRatingCount>(100)</S.ViewerRatingCount>
                </S.ViewerRatingScore>
                <S.ViewerRatingCreateBtn onClick={() => setIsRatingOpen(true)}>
                    별점주기
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
                            <S.RatingConfirmBtn onClick={handleConfirmRating} disabled={selectedScore === null}>확인</S.RatingConfirmBtn>
                        </S.RatingModalActions>
                    </S.RatingModal>
                </S.RatingModalOverlay>
            )}
            <S.ViewerCommentSection>
                <CommentList comments={comments} mypage={false}/>
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