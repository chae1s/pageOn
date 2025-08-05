import React from 'react';
import { ContentType, ContentStatus, ContentDetail } from '../../types/Content';
import * as S from "../Styles/ContentDetail.styles"

interface Props {
    content: ContentDetail;
}


function ContentDetailLayout({content}: Props) {
    const dayKoMap: Record<string, string> = {
        MONDAY: "월요일",
        TUESDAY: "화요일",
        WEDNESDAY: "수요일",
        THURSDAY: "목요일",
        FRIDAY: "금요일",
        SATURDAY: "토요일",
        SUNDAY: "일요일"
    }

    const statusMap: Record<string, string> = {
        COMPLETED: "완결",
        ONGOING: "연재",
        REST: "휴재"
    }

    const RatingFullIcon = () => (
        <svg width="16" height="16" viewBox="0 0 16 16" fill="#FFD600" xmlns="http://www.w3.org/2000/svg">
            <path d="M8 1.6l2.02 4.09 4.51.66-3.26 3.18.77 4.5L8 11.13l-4.04 2.13.77-4.5-3.26-3.18 4.51-.66L8 1.6z" />
        </svg>
    )

    return (
        <S.ContentDetailHeader>
            <S.ContentImageContainer>
                <S.ContentImage src={content.cover} alt={content.title} />
            </S.ContentImageContainer>
            <S.ContentInfoContainer>
                <S.ContentTitleWrapper>
                    <S.ContentTitle className="detail-title">{content.title}</S.ContentTitle>
                    <S.ContentStatus $status={content.status}>{statusMap[content.status]}</S.ContentStatus>
                    <S.ContentInterestBtnContainer>
                        <S.ContentInterestBtn>관심</S.ContentInterestBtn>
                </S.ContentInterestBtnContainer>
                </S.ContentTitleWrapper>
                <S.ContentInfoText>
                    <S.ContentAuthor className="detail-author">{content.author}</S.ContentAuthor>
                    <S.ContentSerialDay className="detail-serialDay">{dayKoMap[content.serialDay]} 연재</S.ContentSerialDay>
                </S.ContentInfoText>
                <S.ContentScoreContainer>
                    <S.ContentRatingContainer>
                        <RatingFullIcon />
                        <S.ContentRatingScore>{content.rating}</S.ContentRatingScore>
                        <S.ContentRatingCount>({content.ratingCount})</S.ContentRatingCount>
                    </S.ContentRatingContainer>
                    <S.ContentViewCount>
                        <S.ContentViewCountName>조회 수</S.ContentViewCountName>
                        <span>{content.viewCount}</span>
                    </S.ContentViewCount>
                </S.ContentScoreContainer>
                <S.ContentDescription>
                    {content.description}
                </S.ContentDescription>
                <S.ContentKeywordContainer>
                    {content.keywords.map((keyword, index) => (
                        <S.ContentKeywordItem key={index}>#{keyword.name}</S.ContentKeywordItem>
                    ))}
                </S.ContentKeywordContainer>
                
            </S.ContentInfoContainer>
            <div className="detail-content-notice">

            </div>
        </S.ContentDetailHeader>
        
    );
}

export default ContentDetailLayout;