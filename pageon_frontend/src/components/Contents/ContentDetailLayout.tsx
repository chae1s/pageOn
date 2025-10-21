import React, {useState} from 'react';
import { ContentType, ContentStatus, ContentDetail } from '../../types/Content';
import axios from "axios";
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

    const [isInterested, setIsInterested] = useState(content.isInterested);
    const [showNotification, setShowNotification] = useState(false);

    const RatingFullIcon = () => (
        <svg width="16" height="16" viewBox="0 0 16 16" fill="#FFD600" xmlns="http://www.w3.org/2000/svg">
            <path d="M8 1.6l2.02 4.09 4.51.66-3.26 3.18.77 4.5L8 11.13l-4.04 2.13.77-4.5-3.26-3.18 4.51-.66L8 1.6z" />
        </svg>
    )

    const PlusIcon = () => (
        <svg width="16" height="16" viewBox="0 0 16 16" fill="#69a3ff" xmlns="http://www.w3.org/2000/svg">
            <rect x="7" y="3" width="2" height="10" rx="1" />
            <rect x="3" y="7" width="10" height="2" rx="1" />
        </svg>
    )

    const CheckIcon = () => (
        <svg width="16" height="16" viewBox="0 0 16 16" fill="#fff" xmlns="http://www.w3.org/2000/svg">
            <path d="M4 8.5L7 11.5L12 5.5" stroke="#fff" strokeWidth="2" fill="none" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    )

    const RegisterLike = async (e: React.MouseEvent<HTMLButtonElement>) => {

        try {

            if (content.contentType === "webnovels") {
                const response = await axios.post(`/api/webnovels/${content.id}/interests`, {}, {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                    }
                })

                console.log(response.data)
            } else if (content.contentType === "webtoons") {
                await axios.post(`/api/webtoons/${content.id}/interests`, {}, {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                    }
                })
            }
            
            setIsInterested(true);
            setShowNotification(true);
            
            // 3초 후 알림 메시지 숨기기
            setTimeout(() => {
                setShowNotification(false);
            }, 3000);
            
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <>
            <S.ContentDetailHeader>
                <S.ContentImageContainer>
                    <S.ContentImage src={content.cover} alt={content.title} />
                </S.ContentImageContainer>
                <S.ContentInfoContainer>
                    <S.ContentTitleWrapper>
                        <S.ContentTitle className="detail-title">{content.title}</S.ContentTitle>
                        <S.ContentStatus $status={content.status}>{statusMap[content.status]}</S.ContentStatus>
                        <S.ContentLikeBtnContainer>
                            {isInterested ? (
                                <S.ContentInterestBtn>
                                    <CheckIcon />
                                    <S.ContentLikeBtnText>관심</S.ContentLikeBtnText>
                                </S.ContentInterestBtn>
                            ):(
                                <S.ContentInterestRegisterBtn type="button" onClick={RegisterLike}>
                                    <PlusIcon /> 
                                    <S.ContentLikeBtnText>관심</S.ContentLikeBtnText>
                                </S.ContentInterestRegisterBtn>
                            )}
                            
                        </S.ContentLikeBtnContainer>
                        
                    
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
            <S.NotificationContainer $show={showNotification}>
                관심 작품에 등록되었습니다.
            </S.NotificationContainer>
        </>
    );
}

export default ContentDetailLayout;