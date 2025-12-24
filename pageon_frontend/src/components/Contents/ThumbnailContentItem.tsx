import React from 'react';
import * as S from '../Styles/ThumbnailContent.styles'
import { SimpleContent } from '../../types/Content';
import { formatKorean, formatUrl } from '../../utils/formatContentType';

interface Props {
  content: SimpleContent;
  layout?: 'grid' | 'slider';
}

function ThumbnailContentItem({ content, layout = 'grid' }: Props) {

    return (
        <S.ContentItem $layout={layout}>
            <S.ContentImageCover $layout={layout} to={`/${formatUrl(content.contentType)}/${content.id}`}>
                <S.ContentImage src={content.cover || 'https://via.placeholder.com/140x200'} alt={content.title} />
            </S.ContentImageCover>
            <S.ContentInfoCover $layout={layout}>
                <S.ContentTitle to={`/${formatUrl(content.contentType)}/${content.id}`}>{content.title}</S.ContentTitle>
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

