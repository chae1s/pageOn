import React from 'react';
import * as S from '../Styles/ThumbnailContent.styles'
import { SimpleContent } from '../../types/Content';

interface Props {
  content: SimpleContent;
  layout?: 'grid' | 'slider';
}

function ThumbnailContentItem({ content, layout = 'grid' }: Props) {

    return (
        <S.ContentItem $layout={layout}>
            <S.ContentImageCover $layout={layout} to={`/${content.contentType}/${content.id}`}>
                <S.ContentImage src={content.cover || 'https://via.placeholder.com/140x200'} alt={content.title} />
            </S.ContentImageCover>
            <S.ContentInfoCover $layout={layout}>
                <S.ContentTitle to={`/${content.contentType}/${content.id}`}>{content.title}</S.ContentTitle>
                <S.ContentAuthor>{content.author}</S.ContentAuthor>
            </S.ContentInfoCover>
        </S.ContentItem>
    )
}

export default ThumbnailContentItem;

