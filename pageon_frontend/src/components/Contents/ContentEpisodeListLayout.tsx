import React, { useState } from 'react';
import * as S from "../Styles/ContentDetail.styles";
import { EpisodeSummary, PurchaseTargetEpisode } from '../../types/Episodes';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axiosInstance';
import { expirationCheck, expirationDate } from '../../utils/rentalEpisodeFormat';
import PurchaseModal from '../Modals/PurchaseModal';

interface Props {
    type: string,
    contentId: number,
    contentTitle: string,
    episodes: EpisodeSummary[];
}

// 임시 로그인 여부 함수
function isLoggedIn() {
    return !!localStorage.getItem('accessToken');
}

type PurchaseModalMode = 'OWN' | 'RENT' | 'SELECT';

function ContentEpisodeListLayout( {type, contentId, contentTitle, episodes}: Props ) {

    const [sort, setSort] = useState<string>("recent") // first | recent
    const [showAll, setShowAll] = useState<boolean>(false);
    const [purchasePrompt, setPurchasePrompt] = useState<{ episode: PurchaseTargetEpisode; mode: PurchaseModalMode; allowRent: boolean } | null>(null);
    const navigate = useNavigate();
    const normalizedType = type.toLowerCase();
    const isWebtoonType = normalizedType === 'webtoon' || normalizedType === 'webtoons';
    const isWebnovelType = normalizedType === 'webnovel' || normalizedType === 'webnovels';

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
    const checkLogin = (): boolean => {
        if (!isLoggedIn()) {
            alert('로그인이 필요합니다.');
            navigate("/users/login");
            return false; // 로그인 실패
        }
        return true; // 로그인 성공
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
            await api.post(`/${type}/episodes/${episode.id}/subscribe?purchaseType=${purchaseType}`);
            closePurchasePrompt();
            navigate(`/${type}/${contentId}/viewer/${episode.id}`);
        } catch (error) {
            console.error("에피소드 구매 실패 : ", error);
        }
    };

    const handleEpisodePurchase = (episode: EpisodeSummary) => (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        if (!checkLogin()) {
            return;
        }
        openPurchasePrompt(episode, 'OWN');
    };

    const handleEpisodeRent = (episode: EpisodeSummary) => (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        if (!checkLogin()) {
            return;
        }
        openPurchasePrompt(episode, 'RENT');
    } 

    const handleEpisodeView = (episodeId: number) => async (e: React.MouseEvent<HTMLButtonElement>) => {
        if (!checkLogin()) {
            e.preventDefault();
            return;
        }

        try {
            const response = await api.get(`/${type}/episodes/${episodeId}/subscribe`, {
                params: {
                    contentId: contentId,
                }
            });

            console.log(response.data)
            if (!response.data) {
                const targetEpisode = episodes.find(ep => ep.id === episodeId);
                if (!targetEpisode) {
                    return;
                }
                
                openPurchasePrompt(targetEpisode, 'SELECT', isWebtoonType);
                e.preventDefault(); // Prevent navigation
                return;
            }
            
            if (response.data) {
                navigate(`/${type}/${contentId}/viewer/${episodeId}`);
            }
        } catch (error) {
            const targetEpisode = episodes.find(ep => ep.id === episodeId);
            if (!targetEpisode) {
                return;
            }
            openPurchasePrompt(targetEpisode, 'SELECT', isWebtoonType);
            e.preventDefault();
        }
    }
    

    const formatPointValue = (value?: number | null) => {
        if (typeof value !== 'number') {
            return '0';
        }
        return value.toLocaleString();
    };


    return (
        <>
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
                                    {isWebtoonType && (
                                        <S.RentalBtn type="button" >
                                            선택 대여
                                        </S.RentalBtn>
                                    )}
                                    <S.PurchaseBtn type="button" >
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
                                            {isWebtoonType && 
                                                <S.EpisodeThumbnailContainer>
                                                    <S.EpisodeThumbnailImage src=''/>
                                                </S.EpisodeThumbnailContainer>
                                            }
                                            <S.EpisodeInfoContainer>
                                                <S.EpisodeTitleAndNum>
                                                    <span>{episode.episodeNum}화</span>
                                                    <S.EpisodeTitle type='button' onClick={handleEpisodeView(episode.id)}>{episode.episodeTitle}</S.EpisodeTitle>
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
                                                            {isWebtoonType && (
                                                                <S.RentalBtn type="button" onClick={handleEpisodeRent(episode)}>
                                                                    대여
                                                                </S.RentalBtn>
                                                            )}
                                                            <S.PurchaseBtn
                                                                type="button"
                                                                onClick={handleEpisodePurchase(episode)}
                                                            >
                                                                구매
                                                            </S.PurchaseBtn>
                                                        </>
                                                    ) : (
                                                        <S.ViewBtn
                                                            type="button"
                                                            onClick={handleEpisodeView(episode.id)}
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
        <PurchaseModal 
            isOpen={!!purchasePrompt}
            onClose={closePurchasePrompt}
            onConfirm={processTransaction}
            contentTitle={contentTitle}
            episode={purchasePrompt?.episode || null}
            mode={purchasePrompt?.mode || 'SELECT'}
            allowRent={purchasePrompt?.allowRent || false}
            isWebnovelType={isWebnovelType}
        />
        </>
    )

}

export default ContentEpisodeListLayout;