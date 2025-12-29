import { useNavigate } from "react-router-dom";
import { SimpleContent } from "../../types/Content";
import React from "react";
import { formatKorean, formatUrl } from "../../utils/formatContentType";
import * as S from "../Styles/SimpleContent.styles"
import fullStarIcon from "../../assets/fullStarIcon.png";

interface Props {
    content: SimpleContent;
}

function SimpleContentListItem({content}: Props) {

    const navigate = useNavigate();

    const handleContentClick = (contentType: string, contentId: number) => (e:React.MouseEvent<HTMLDivElement>) => {
        e.preventDefault();

        navigate(`/${formatUrl(contentType)}/${contentId}`)
    }
    
    
    return (
        <S.HomeContentItem>
            <S.ContentImageCover onClick={handleContentClick(content.contentType, content.contentId)}>
                <S.ContentImage
                    src={content.cover || "https://via.placeholder.com/140x200"}
                    alt={content.contentTitle}
                />
            </S.ContentImageCover>
            <S.ContentInfoCover>
                <S.ContentTitle onClick={handleContentClick(content.contentType, content.contentId)}>{content.contentTitle}</S.ContentTitle>
                <S.ContentInfoWrapper>
                    <S.ContentAuthor>{content.author}</S.ContentAuthor>
                    <S.ContentSeparate>ㆍ</S.ContentSeparate>
                    <S.ContentType>{formatKorean(content.contentType)}</S.ContentType>
                </S.ContentInfoWrapper>
                <S.ContentRatingWrapper>
                    <S.ContentRatingStarIcon src={fullStarIcon}/>
                    <S.ContentRatingScore>{Number(content.totalAverageRating ?? 0).toFixed(1)}</S.ContentRatingScore>
                    <S.ContentRatingCount>({content.totalRatingCount ?? 0})</S.ContentRatingCount>
                </S.ContentRatingWrapper>
            </S.ContentInfoCover>
        </S.HomeContentItem>
    )
}

export default SimpleContentListItem;