import React, { useEffect, useState, useRef } from "react";
import axios from "axios";
import { useLocation, useNavigate, useNavigationType, useParams } from "react-router-dom";
import { WebtoonEpisodeDetail, PurchaseTargetEpisode } from "../../types/Episodes";
import * as S from "./Viewer.styles"
import * as C from "./Comment.styles";
import fullStarIcon from "../../assets/fullStarIcon.png"
import halfFullStarIcon from "../../assets/halfFullStarIcon.png"
import emptyStarIcon from "../../assets/emptyStarIcon.png"
import api from "../../api/axiosInstance";
import { BestComment } from "../../types/Comments";
import { formatDate } from "../../utils/formatData";
import PurchaseModal, { PurchaseModalMode } from "../../components/Modals/PurchaseModal";

function WebtoonViewer() {
    const { episodeId , contentId } = useParams<{episodeId: string; contentId: string}>();
    
    const navigationType = useNavigationType();
    const navigate = useNavigate();

    const [showTitleSection, setShowTitleSection] = useState(true);
    const lastScrollY = useRef(0);

    const [ episodeData, setEpisodeData ] = useState<WebtoonEpisodeDetail | null>(null);

    const [ bestComment, setBestComment ] = useState<BestComment>();

    const [ purchasePrompt, setPurchasePrompt ] = useState<{episode: PurchaseTargetEpisode; mode: PurchaseModalMode; allowRent: boolean} | null>(null);

    useEffect(() => {
        async function fetchData(preserveScroll: boolean = false) {
            if (!episodeId) return;

            const savedY = window.scrollY;
            try {
                const response = await api.get(`/episodes/webtoon/${episodeId}`);
                setEpisodeData(response.data);
                setSelectedScore(response.data.userScore ?? 0);
                setBestComment(response.data.bestComment);

                if (navigationType !== 'POP') {
                    if (preserveScroll) {
                        window.scrollTo(0, savedY);
                    }  else {
                        window.scrollTo(0, 0);
                    }
                    
                } else {
                    
                    const scrollPosition = sessionStorage.getItem("scrollPosition");
                    
                    if (scrollPosition) {
                        window.scrollTo(0, parseInt(scrollPosition, 10));
                        sessionStorage.removeItem("scrollPosition");
                    } else {
                        window.scrollTo(0, 0);
                    }
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

    function isLoggedIn() {
        return !!localStorage.getItem('accessToken');
    }

    const checkLogin = (): boolean => {
        if (!isLoggedIn()) {
            alert('로그인이 필요합니다.');
            navigate("/users/login");
            return false; 
        }
        return true; 
    };

    const openPurchasePrompt = (episode: PurchaseTargetEpisode, mode: PurchaseModalMode, allowRent: boolean = false) => {
        setPurchasePrompt({ episode, mode, allowRent });
    };

    const closePurchasePrompt = () => setPurchasePrompt(null);

    const processTransaction = async (purchaseType: 'OWN' | 'RENT') => {
        if (!purchasePrompt || !checkLogin()) {
            return;
        }
        const { episode } = purchasePrompt;

        try {
            await api.post(`/webtoons/episodes/${episode.id}/subscribe?purchaseType=${purchaseType}`);
            closePurchasePrompt();
            navigate(`/webtoons/${contentId}/viewer/${episode.id}`);
        } catch (error) {
            console.error("에피소드 구매 실패 : ", error);
        }
    };

    const handleNavigateEpisode = async (e:React.MouseEvent, targetEpisodeId: number | null | undefined, direction: 'PREV' | 'NEXT') => {
        e.preventDefault();

        if (!targetEpisodeId) return;

        if (!checkLogin()) return;

        try {
            const response = await api.get(`/webtoons/episodes/${targetEpisodeId}/subscribe`);

            if (response.data) {
                navigate(`/webtoons/${contentId}/viewer/${targetEpisodeId}`);
                return;
            }

            const currentEpisodeNum = episodeData?.episodeNum || 0;

            const nextNum = direction === 'NEXT' ? currentEpisodeNum + 1 : currentEpisodeNum - 1;

            const targetEpisode: PurchaseTargetEpisode = {
                id: targetEpisodeId,
                episodeNum: nextNum,
                purchasePrice: episodeData?.purchasePrice,
                rentalPrice: episodeData?.rentalPrice
            };

            openPurchasePrompt(targetEpisode, 'SELECT', true);

        } catch (error) {
            console.log("에피소드 정보 확인 중 오류 발생: ", error);
        }
    }

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
        const currentUserScore = episodeData?.userScore ?? 0;
        if (selectedScore === currentUserScore) {
            setIsRatingOpen(false);
            window.scrollTo(0, savedY);
            return;
        }
        try {
            await api.post("/rating", {
                contentType: "WEBTOON",
                episodeId: episodeData?.id,
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
        const currentUserScore = episodeData?.userScore ?? 0;
        if (selectedScore === currentUserScore) {
            setIsRatingOpen(false);
            window.scrollTo(0, savedY);
            return;
        }
        try {
            await api.patch("/rating", {
                contentType: "WEBTOON",
                episodeId: episodeData?.id,
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

    const handleGoToComments = (e:React.MouseEvent) => {
        e.preventDefault();

        const scrollPosition = window.scrollY;
        const commentUrl = `/webtoons/${contentId}/viewer/${episodeId}/comments`;
        
        sessionStorage.setItem("scrollPosition", scrollPosition.toString());

        navigate(commentUrl);
    }
    
    
    if (!episodeId || !contentId) {
        return null;
    }
    

    return (
        <S.Viewer>
            <S.EpisodeTitleSection isVisible={showTitleSection}>
                <S.EpisodeTitleContainer>
                    <S.WebnovelTitle to={`/webtoons/${contentId}`}>{episodeData?.episodeNum}화 {episodeData?.episodeTitle}</S.WebnovelTitle>
                    <S.EpisodeLinkContainer>
                        <S.EpisodeLink 
                            as="button"
                            type="button"
                            disabled={episodeData?.prevEpisodeId === null}
                            $disabled={episodeData?.prevEpisodeId === null} 
                            onClick={(e) => handleNavigateEpisode(e, episodeData?.prevEpisodeId, 'PREV')}
                        >
                            이전화
                        </S.EpisodeLink>
                        <S.EpisodeLink 
                            as="button"
                            type="button"
                            disabled={episodeData?.nextEpisodeId === null}
                            $disabled={episodeData?.nextEpisodeId === null} 
                            onClick={(e) => handleNavigateEpisode(e, episodeData?.nextEpisodeId, 'NEXT')}
                        >
                            다음화
                        </S.EpisodeLink>
                    </S.EpisodeLinkContainer>
                </S.EpisodeTitleContainer>
            </S.EpisodeTitleSection>
            <S.ViewerBodySection onClick={() => setShowTitleSection(prev => !prev)}>
                <S.EpisodeContentContainer>
                    <S.EpisodeViewerContents>
                        <S.ContentWrapper>
                            <S.EpisodeContent>
                                {episodeData?.images.map((image) => (
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
                    <S.ViewerAverageRatingScore>{Number(episodeData?.averageRating ?? 0).toFixed(1)}</S.ViewerAverageRatingScore>
                    <S.ViewerRatingCount>{episodeData?.ratingCount ?? 0}</S.ViewerRatingCount>
                </S.ViewerRatingScore>
                <S.ViewerRatingCreateBtn onClick={() => { setSelectedScore((episodeData?.userScore ?? 0) as number); setIsRatingOpen(true); }}>
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
                            <S.RatingConfirmBtn onClick={(episodeData?.userScore !== null && episodeData?.userScore !== 0) ? handleUpdateRating : handleConfirmRating} disabled={selectedScore === null}>
                                {episodeData?.userScore !== null && episodeData?.userScore !== 0 ? "수정" : "확인"}
                            </S.RatingConfirmBtn>
                        </S.RatingModalActions>
                    </S.RatingModal>
                </S.RatingModalOverlay>
            )}
            <S.ViewerCommentSection>
                <C.CommentList>
                    <C.CommentHeader>
                        <C.CommentCount>
                            댓글 {bestComment?.totalCount}개
                        </C.CommentCount>
                        <C.CommentListBtn onClick={handleGoToComments}>
                            댓글 보기
                        </C.CommentListBtn>
                    </C.CommentHeader>
                    <C.CommentListLi>
                    {(!bestComment || bestComment.id == null) ? (
                        <C.CommentListEmptyText>베스트 댓글이 없습니다.</C.CommentListEmptyText>
                    ) : (
                        <>
                            <C.CommentInfo>
                                <C.CommentBestInfo>
                                    <C.CommentBestIcon>
                                        BEST
                                    </C.CommentBestIcon>
                                    <C.CommentBestUserInfo>
                                        <div>{bestComment.nickname}</div>
                                    </C.CommentBestUserInfo>
                                </C.CommentBestInfo>
                            </C.CommentInfo>
                            <C.CommentContentWrap>
                                <C.CommentContent>{bestComment.text}</C.CommentContent>
                            </C.CommentContentWrap>
                            <C.CommentDateBtn>
                                <div>{formatDate(bestComment.createdAt)}</div>
                            </C.CommentDateBtn>
                        </>
                    )}
                    </C.CommentListLi>
                </C.CommentList>
            </S.ViewerCommentSection>

            <S.ViewerNextEpisodeBtnSection>
                <S.ViewerNextEpisodeBtnContainer>
                    <S.ViewerNextEpisodeBtn 
                        as="button"
                        type="button"
                        disabled={episodeData?.nextEpisodeId === null}
                        $disabled={episodeData?.nextEpisodeId === null} 
                        onClick={(e) => handleNavigateEpisode(e, episodeData?.nextEpisodeId, 'NEXT')}
                    >
                        다음화 보기
                    </S.ViewerNextEpisodeBtn>
                </S.ViewerNextEpisodeBtnContainer>
            </S.ViewerNextEpisodeBtnSection>
            <PurchaseModal 
                isOpen={!!purchasePrompt}
                onClose={closePurchasePrompt}
                onConfirm={processTransaction}
                contentTitle={episodeData?.title || ""}
                episode={purchasePrompt?.episode || null}
                mode={purchasePrompt?.mode || 'SELECT'}
                allowRent={purchasePrompt?.allowRent || false}
                isWebnovelType={false}
            />
        </S.Viewer>
    )

}

export default WebtoonViewer;