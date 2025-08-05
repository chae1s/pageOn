import React, { useState } from 'react';
import { ContentType, ContentStatus, ContentDetail } from '../../types/Content';
import { SortBtn } from '../../styles/Layout.styles';
import * as S from "../Styles/ContentDetail.styles";
import { EpisodeSummary } from '../../types/Episodes';
import dayjs from 'dayjs';

interface Props {
    type: string,
    episodes: EpisodeSummary[];
}

function ContentEpisodeListLayout( {type, episodes}: Props ) {

    const [sort, setSort] = useState<string>("recent") // first | recent

    return (
        <S.EpisodeSection>
            <form action="">
                <div>
                    <S.EpisodeListOption>
                        <S.OptionLeft>
                            <S.OptionItem>
                                <S.OptionCheckbox type='checkbox' />
                                <S.OptionCheckboxLabel>전체 선택</S.OptionCheckboxLabel>
                            </S.OptionItem>
                            <S.OptionItem>
                                <S.EpisodeSellBtnWrapper>
                                    {type === 'webtoons' && <S.RentalBtn>선택 대여</S.RentalBtn>}
                                    <S.PurchaseBtn>선택 구매</S.PurchaseBtn>
                                </S.EpisodeSellBtnWrapper>
                            </S.OptionItem>
                        </S.OptionLeft>
                        <S.OptionRight>
                            <S.EpisodeFilterWrapper>
                                <S.EpisodeFilterBtn type='button' active={sort === "recent"} onClick={()=>setSort('recent')}>
                                    최신화부터
                                </S.EpisodeFilterBtn>
                                <S.FilterBtnDivider></S.FilterBtnDivider>
                                <S.EpisodeFilterBtn type='button' active={sort === "first"} onClick={()=>setSort('first')} style={{width: "50px"}}>
                                    1화부터
                                </S.EpisodeFilterBtn>
                            </S.EpisodeFilterWrapper>
                        </S.OptionRight>
                    </S.EpisodeListOption>
                    <S.EpisodeListWrapper>
                        <ul>
                            {episodes.map((episode) => {
                                const createDate = dayjs(episode.createdAt).format("YYYY.MM.DD")
                                return (
                                    <S.EpisodeItem key={episode.id}>
                                        <S.EpisodeItemLeft>
                                            <S.OptionCheckbox type='checkbox' />
                                            {type === 'webtoons' && 
                                                <S.EpisodeThumbnailContainer>
                                                    <S.EpisodeThumbnailImage src=''/>
                                                </S.EpisodeThumbnailContainer>
                                            }
                                            <S.EpisodeInfoContainer>
                                                <S.EpisodeTitleAndNum>
                                                    <span>{episode.episodeNum}화</span>
                                                    <S.EpisodeTitle>{episode.episodeTitle}</S.EpisodeTitle>
                                                </S.EpisodeTitleAndNum>
                                                <S.EpisodeCreateDate>
                                                    {createDate}
                                                </S.EpisodeCreateDate>
                                            </S.EpisodeInfoContainer>
                                        </S.EpisodeItemLeft>
                                        <S.EpisodeItemRight>
                                            <S.EpisodeSellBtnWrapper>
                                                {type === 'webtoons' && <S.RentalBtn>대여</S.RentalBtn>}
                                                <S.PurchaseBtn>구매</S.PurchaseBtn>
                                            </S.EpisodeSellBtnWrapper>
                                        </S.EpisodeItemRight>
                                    </S.EpisodeItem>
                                );
                            })}
                        </ul>
                    </S.EpisodeListWrapper>
                    <div>
                        <button>

                        </button>
                    </div>
                </div>
            </form>
        </S.EpisodeSection>
    )

}

export default ContentEpisodeListLayout;