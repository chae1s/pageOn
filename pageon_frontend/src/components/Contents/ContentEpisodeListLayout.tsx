import React, { useEffect, useState } from 'react';
import { ContentType, ContentStatus, ContentDetail } from '../../types/Content';
import * as S from "../Styles/ContentDetail.styles";
import { EpisodeSummary } from '../../types/Episodes';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import { expirationCheck, expirationDate } from '../../utils/rentalEpisodeFormat';

interface Props {
    type: string,
    contentId: number,
    episodes: EpisodeSummary[];
}

// 임시 로그인 여부 함수
function isLoggedIn() {
    return !!localStorage.getItem('accessToken');
}

function ContentEpisodeListLayout( {type, contentId, episodes}: Props ) {

    const [sort, setSort] = useState<string>("recent") // first | recent
    const [showAll, setShowAll] = useState<boolean>(false);
    const navigate = useNavigate();

    // 정렬된 에피소드 배열 생성
    const sortedEpisodes = React.useMemo(() => {
        if (sort === "first") {
            // 1화부터: episodeNum 오름차순
            return [...episodes].sort((a, b) => a.episodeNum - b.episodeNum);
        } else {
            // 최신화부터: episodeNum 내림차순
            return [...episodes].sort((a, b) => b.episodeNum - a.episodeNum);
        }
    }, [episodes, sort]);

    // id를 string으로 변환
    const displayedEpisodes = showAll ? sortedEpisodes : sortedEpisodes.slice(0, 20);
    const displayedIds = displayedEpisodes.map(e => String(e.id));

    // 선택된 에피소드 id 배열 (string)
    const [selectedIds, setSelectedIds] = useState<string[]>([]);

    // 전체 선택 체크박스 상태
    const isAllChecked = displayedIds.length > 0 && displayedIds.every(id => selectedIds.includes(id));
    const isIndeterminate = selectedIds.length > 0 && !isAllChecked;

    // 전체 선택 토글
    const handleAllCheck = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.checked) {
            // 현재 보여지는 에피소드만 전체 선택
            setSelectedIds(prev => Array.from(new Set([...prev, ...displayedIds])));
        } else {
            // 현재 보여지는 에피소드만 전체 해제
            setSelectedIds(prev => prev.filter(id => !displayedIds.includes(id)));
        }
    };

    // 개별 체크박스 토글
    const handleEpisodeCheck = (id: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.checked) {
            setSelectedIds(prev => [...prev, id]);
        } else {
            setSelectedIds(prev => prev.filter(selectedId => selectedId !== id));
        }
    };

    // 구매/대여 버튼 클릭시 로그인 체크
    const handleRequireLogin = (e: React.MouseEvent<HTMLButtonElement>) => {
        if (!isLoggedIn()) {
            e.preventDefault();
            alert('로그인이 필요합니다.');

            navigate("/users/login");
            return;
        }
        // 실제 구매/대여 로직은 여기에 추가

    };

    
    const handleEpisodePurchase = (episodeId: number) => async (e: React.MouseEvent<HTMLButtonElement>) => {
        if (!isLoggedIn()) {
            e.preventDefault();
            alert('로그인이 필요합니다.');

            navigate("/users/login");
            return;
        }

        if (window.confirm("에피소드를 구매하시겠습니까?")) {
            try {
                await api.post(`/${type}/episodes/${episodeId}/subscribe?purchaseType=OWN`);

                navigate(`/${type}/${contentId}/viewer/${episodeId}`);
            } catch (error) {
                console.error("에피소드 구매 실패 : ", error);
            }
        }
    };

    const handleEpisodeRent = (episodeId: number) => async (e: React.MouseEvent<HTMLButtonElement>) => {
        if (!isLoggedIn()) {
            e.preventDefault();
            alert('로그인이 필요합니다.');

            navigate("/users/login");
            return;
        }

        if (window.confirm("에피소드를 대여하시겠습니까?")) {
            try {
                await api.post(`/${type}/episodes/${episodeId}/subscribe?purchaseType=RENT`);

                navigate(`/${type}/${contentId}/viewer/${episodeId}`);
            } catch (error) {
                console.error("에피소드 구매 실패 : ", error);
            }
        }
    } 

    

    return (
        <S.EpisodeSection>
            <form action="">
                <div>
                    <S.EpisodeListOption>
                        <S.OptionLeft>
                            <S.OptionItem>
                                <S.OptionCheckbox
                                    type='checkbox'
                                    checked={isAllChecked}
                                    ref={el => {
                                        if (el) el.indeterminate = isIndeterminate;
                                    }}
                                    onChange={handleAllCheck}
                                />
                                <S.OptionCheckboxLabel>전체 선택</S.OptionCheckboxLabel>
                            </S.OptionItem>
                            <S.OptionItem>
                                <S.EpisodeSellBtnWrapper>
                                    {type === 'WEBTOON' && (
                                        <S.RentalBtn type="button" onClick={handleRequireLogin}>
                                            선택 대여
                                        </S.RentalBtn>
                                    )}
                                    <S.PurchaseBtn type="button" onClick={handleRequireLogin}>
                                        선택 구매
                                    </S.PurchaseBtn>
                                </S.EpisodeSellBtnWrapper>
                            </S.OptionItem>
                        </S.OptionLeft>
                        <S.OptionRight>
                            <S.EpisodeFilterWrapper>
                                <S.EpisodeFilterBtn type='button' $active={sort === "recent"} onClick={()=>setSort('recent')}>
                                    최신화부터
                                </S.EpisodeFilterBtn>
                                <S.FilterBtnDivider></S.FilterBtnDivider>
                                <S.EpisodeFilterBtn type='button' $active={sort === "first"} onClick={()=>setSort('first')} style={{width: "50px"}}>
                                    1화부터
                                </S.EpisodeFilterBtn>
                            </S.EpisodeFilterWrapper>
                        </S.OptionRight>
                    </S.EpisodeListOption>
                    <S.EpisodeListWrapper>
                        <ul>
                            {displayedEpisodes.map((episode) => {
                                const createDate = dayjs(episode.createdAt).format("YYYY.MM.DD")
                                const episodeIdStr = String(episode.id);
                                return (
                                    <S.EpisodeItem key={episode.id}>
                                        <S.EpisodeItemLeft>
                                            <S.OptionCheckbox
                                                type='checkbox'
                                                checked={selectedIds.includes(episodeIdStr)}
                                                onChange={handleEpisodeCheck(episodeIdStr)}
                                            />
                                            {type === 'webtoons' && 
                                                <S.EpisodeThumbnailContainer>
                                                    <S.EpisodeThumbnailImage src=''/>
                                                </S.EpisodeThumbnailContainer>
                                            }
                                            <S.EpisodeInfoContainer>
                                                <S.EpisodeTitleAndNum>
                                                    <span>{episode.episodeNum}화</span>
                                                    <S.EpisodeTitle to={`/${type}/${contentId}/viewer/${episode.id}`}>{episode.episodeTitle}</S.EpisodeTitle>
                                                </S.EpisodeTitleAndNum>
                                                <S.EpisodeDateAndPurchaseData>
                                                    <S.EpisodeCreateDate>
                                                        {createDate}
                                                    </S.EpisodeCreateDate>
                                                    {episode.episodePurchase && (
                                                        <S.EpisodePurchaseText>
                                                            {episode.episodePurchase.purchaseType === 'OWN' 
                                                                ? '소장' 
                                                                : expirationDate(episode.episodePurchase.expiredAt)
                                                            }
                                                        </S.EpisodePurchaseText>
                                                    )}
                                                </S.EpisodeDateAndPurchaseData>
                                            </S.EpisodeInfoContainer>
                                        </S.EpisodeItemLeft>
                                        <S.EpisodeItemRight>
                                            <S.EpisodeSellBtnWrapper>
                                                {episode.episodePurchase == null || 
                                                    (episode.episodePurchase.purchaseType === "RENT" && expirationCheck(episode.episodePurchase.expiredAt)) 
                                                    ? (
                                                        <>
                                                            {type === 'webtoons' && (
                                                                <S.RentalBtn type="button" onClick={handleEpisodeRent(episode.id)}>
                                                                    대여
                                                                </S.RentalBtn>
                                                            )}
                                                            <S.PurchaseBtn
                                                                type="button"
                                                                onClick={handleEpisodePurchase(episode.id)}
                                                            >
                                                                구매
                                                            </S.PurchaseBtn>
                                                        </>
                                                    ) : (
                                                        <S.ViewBtn
                                                            type="button"
                                                            onClick={() => navigate(`/${type}/${contentId}/viewer/${episode.id}`)}
                                                        >
                                                            보기
                                                        </S.ViewBtn>
                                                    )
                                                }
                                            </S.EpisodeSellBtnWrapper>
                                        </S.EpisodeItemRight>
                                    </S.EpisodeItem>
                                );
                            })}
                        </ul>
                    </S.EpisodeListWrapper>
                    {episodes.length > 20 && !showAll && (
                        <S.ShowAllBtnContainer>
                            <S.ShowAllBtn type='button' onClick={() => setShowAll(true)}>
                                더보기
                            </S.ShowAllBtn>
                        </S.ShowAllBtnContainer>
                    )}
                </div>
            </form>
        </S.EpisodeSection>
    )

}

export default ContentEpisodeListLayout;