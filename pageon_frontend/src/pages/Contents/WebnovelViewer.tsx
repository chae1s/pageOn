import React, { useEffect, useState, useRef } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import { WebnovelEpisodeDetail } from "../../types/Episodes";
import * as S from "./Viewer.styles"
import CommentList from "../../components/Comments/CommentList";

function WebnovelViewer() {
    const { episodeId , contentId } = useParams();
    const [ episodeData, setEpisodeData ] = useState<WebnovelEpisodeDetail>({
        id: 0,
        title: "",
        episodeNum: 0,
        episodeTitle: "",
        content: "",
        prevEpisodeId: null,
        nextEpisodeId: null
    });
    const [showTitleSection, setShowTitleSection] = useState(true);
    const lastScrollY = useRef(0);

    useEffect(() => {
        async function fetchData() {
            if (!episodeId) return;

            try {
                const response = await axios.get(`/api/episodes/webnovel/${episodeId}`);
                setEpisodeData(response.data);

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
                    <S.WebnovelTitle to={`/webnovels/${contentId}`}>{episodeData.title}</S.WebnovelTitle>
                    <S.EpisodeLinkContainer>
                        <S.EpisodeLink to={`/webnovels/${contentId}/viewer/${episodeData.prevEpisodeId}`} $disabled={episodeData.prevEpisodeId === null} aria-disabled={episodeData.prevEpisodeId === null}>이전화</S.EpisodeLink>
                        <S.EpisodeLink to={`/webnovels/${contentId}/viewer/${episodeData.nextEpisodeId}`} $disabled={episodeData.nextEpisodeId === null} aria-disabled={episodeData.nextEpisodeId === null}>다음화</S.EpisodeLink>
                    </S.EpisodeLinkContainer>
                </S.EpisodeTitleContainer>
            </S.EpisodeTitleSection>
            <S.ViewerBodySection onClick={() => setShowTitleSection(prev => !prev)}>
                <S.EpisodeContentContainer>
                    <S.EpisodeViewerContents>
                        <S.ContentWrapper>
                            <S.EpisodeContentHeader>
                                <S.EpisodeNum>{episodeData.episodeNum}화</S.EpisodeNum>
                                <S.EpisodeTitle>{episodeData.episodeTitle}</S.EpisodeTitle>
                            </S.EpisodeContentHeader>
                            <S.EpisodeContent>
                                {episodeData.content}
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
            <S.ViewerCommentSection>
                <CommentList comments={comments} mypage={false}/>
            </S.ViewerCommentSection>

            <S.ViewerNextEpisodeBtnSection>
                <S.ViewerNextEpisodeBtnContainer>
                    <S.ViewerNextEpisodeBtn to={`/webnovels/${contentId}/viewer/${episodeData.nextEpisodeId}`} $disabled={episodeData.nextEpisodeId === null} aria-disabled={episodeData.nextEpisodeId === null}>다음화 보기</S.ViewerNextEpisodeBtn>
                </S.ViewerNextEpisodeBtnContainer>
            </S.ViewerNextEpisodeBtnSection>
        </S.Viewer>
    )

}

export default WebnovelViewer;