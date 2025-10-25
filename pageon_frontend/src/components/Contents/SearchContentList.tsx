import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { SearchContent } from "../../types/Content";
import * as S from "../Styles/SearchContents.styles"
import { keyboard } from "@testing-library/user-event/dist/keyboard";

interface Props {
    contents?: SearchContent[];
    totalElements?: number;
}

function SearchContentList({contents, totalElements}: Props) {

    const navigate = useNavigate();

    const RatingFullIcon = () => (
        <svg width="16" height="16" viewBox="0 0 16 16" fill="#FFD600" xmlns="http://www.w3.org/2000/svg">
            <path d="M8 1.6l2.02 4.09 4.51.66-3.26 3.18.77 4.5L8 11.13l-4.04 2.13.77-4.5-3.26-3.18 4.51-.66L8 1.6z" />
        </svg>
    )

    const handleKeywordClick = (contentType: string, name: string) => {
        const params = new URLSearchParams();
        
        params.append("type", contentType)
        params.append("q", name);


        navigate(`/search/keyword?${params}`)
    }

    return (
        <S.ContentSection>
            <S.ContentTotalCount>{totalElements}개의 작품</S.ContentTotalCount>
            <S.ContentSearchList>
               {contents?.map((content) => (
                    <S.ContentSearchListItem>
                        <S.ContentSearchWrapper>
                            <S.ContentSearchItemCoverSection>
                                <S.ContentCoverWrapper>
                                    <S.ContentCoverImage src={content.cover} alt={content.title}/>
                                </S.ContentCoverWrapper>
                            </S.ContentSearchItemCoverSection>
                            <S.ContentSearchItemInfoSection>
                                <S.ContentInfoWrapper>
                                    <S.ContentTitleWrapper>
                                        <S.ContentTitle to={`/${content.contentType}/${content.id}`}>{content.title}</S.ContentTitle>
                                    </S.ContentTitleWrapper>
                                    <S.ContentAuthor>{content.author}</S.ContentAuthor>
                                    <S.ContentEpisodeCount>총 {content.episodeCount}화</S.ContentEpisodeCount>
                                    <S.ContentRatingContainer>
                                        <RatingFullIcon />
                                        <S.ContentRatingScore>4.33</S.ContentRatingScore>
                                        <S.ContentRatingCount>(356214)</S.ContentRatingCount>
                                    </S.ContentRatingContainer>
                                    <S.ContentDescriptionLink to={`/${content.contentType}/${content.id}`}>
                                    <S.ContentDescription>{content.description}</S.ContentDescription>
                                    </S.ContentDescriptionLink>
                                    <S.ContentKeywordContainer>
                                        {content.keywords.map((keyword, index) => (
                                            <S.ContentKeywordItem key={index} onClick={()=>handleKeywordClick(`${content.contentType}`, `${keyword.name}`)}>#{keyword.name}</S.ContentKeywordItem>
                                        ))}
                                    </S.ContentKeywordContainer>
                                </S.ContentInfoWrapper>
                            </S.ContentSearchItemInfoSection>
                        </S.ContentSearchWrapper>
                    </S.ContentSearchListItem>
               ))} 
            </S.ContentSearchList>
        </S.ContentSection>
    )
}

export default SearchContentList;