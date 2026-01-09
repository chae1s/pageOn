import React from 'react';
import * as S from '../Styles/ThumbnailContent.styles'
import { SimpleContent } from '../../types/Content';
import { formatKorean, formatUrl } from '../../utils/formatContentType';

interface Props {
  content: SimpleContent;
}

function ThumbnailContentItem({ content }: Props) {

    return (
        <S.ContentItem>
            <S.ContentImageCover to={`/${formatUrl(content.contentType)}/${content.contentId}`}>
                <S.ContentImage src={content.cover || 'https://via.placeholder.com/140x200'} alt={content.contentTitle} />
            </S.ContentImageCover>
            <S.ContentInfoCover>
                <S.ContentTitle to={`/${formatUrl(content.contentType)}/${content.contentId}`}>{content.contentTitle}</S.ContentTitle>
                <S.ContentInfoWrapper>
                    <S.ContentAuthor>{content.author}</S.ContentAuthor>
                    <S.ContentSeparate>ㆍ</S.ContentSeparate>
                    <S.ContentType>{formatKorean(content.contentType)}</S.ContentType>
                </S.ContentInfoWrapper>
            </S.ContentInfoCover>
        </S.ContentItem>
    )
}

export default ThumbnailContentItem;

