import React from 'react';
import { ContentType, ContentStatus, ContentDetail } from '../types/Content';
import * as C from "./Content.styles"

interface Props {
    type: ContentType;
    content: ContentDetail;
}


function ContentDetailLayout({type, content}: Props) {
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
        
        <C.ContentDetailHeader>
            <C.ContentImageContainer>
                <C.ContentImage src={content.cover} alt={content.title} />
            </C.ContentImageContainer>
            <C.ContentInfoContainer>
                <C.ContentTitleWrapper>
                    <C.ContentTitle className="detail-title">{content.title}</C.ContentTitle>
                    <C.ContentStatus status={content.status}>{statusMap[content.status]}</C.ContentStatus>
                    <C.ContentInterestBtnContainer>
                        <C.ContentInterestBtn>관심</C.ContentInterestBtn>
                </C.ContentInterestBtnContainer>
                </C.ContentTitleWrapper>
                <C.ContentInfoText>
                    <C.ContentAuthor className="detail-author">{content.author}</C.ContentAuthor>
                    <C.ContentSerialDay className="detail-serialDay">{dayKoMap[content.serialDay]} 연재</C.ContentSerialDay>
                </C.ContentInfoText>
                <C.ContentScoreContainer>
                    <C.ContentRatingContainer>
                        <RatingFullIcon />
                        <C.ContentRatingScore>{content.rating}</C.ContentRatingScore>
                        <C.ContentRatingCount>({content.ratingCount})</C.ContentRatingCount>
                    </C.ContentRatingContainer>
                    <C.ContentViewCount>
                        <C.ContentViewCountName>조회 수</C.ContentViewCountName>
                        <span>{content.viewCount}</span>
                    </C.ContentViewCount>
                </C.ContentScoreContainer>
                <C.ContentDescription>
                    {content.description}
                </C.ContentDescription>
                <C.ContentKeywordContainer>
                    {content.keywords.map((keyword, index) => (
                        <C.ContentKeywordItem key={index}>#{keyword.name}</C.ContentKeywordItem>
                    ))}
                </C.ContentKeywordContainer>
                
            </C.ContentInfoContainer>
            <div className="detail-content-notice">

            </div>
        </C.ContentDetailHeader>
        
    );
}

export default ContentDetailLayout;