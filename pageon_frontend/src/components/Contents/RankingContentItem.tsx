import React from "react";
import * as S from "../Styles/RankingContent.styles"
import { Link } from "react-router-dom";
import { SimpleContent } from "../../types/Content";
import { formatKorean, formatUrl } from "../../utils/formatContentType";

interface Props {
    content: SimpleContent;
    rank: number;
}

function RankingContentItem({ content, rank }: Props) {
    const RatingFullIcon = () => (
        <svg width="16" height="16" viewBox="0 0 16 16" fill="#FFD600" xmlns="http://www.w3.org/2000/svg">
          <path d="M8 1.6l2.02 4.09 4.51.66-3.26 3.18.77 4.5L8 11.13l-4.04 2.13.77-4.5-3.26-3.18 4.51-.66L8 1.6z" />
        </svg>
      );

      return (
        <S.ContentItem>
            <S.ContentItemWrapper>
                <S.ContentImageContainer>
                    <Link to={`/${formatUrl(content.contentType)}/${content.contentId}`}>
                        <S.ContentImageWrapper>
                            <S.ContentImageCover>
                                <S.ContentImage
                                    src={content.cover}
                                    alt={content.contentTitle}
                                />
                            </S.ContentImageCover>
                        </S.ContentImageWrapper>
                    </Link>
                </S.ContentImageContainer>

                <S.ContentRankWrapper>
                    <S.ContentRankNumber>{rank}</S.ContentRankNumber>
                </S.ContentRankWrapper>

                <S.ContentInfoWrapper>
                    <S.ContentInfoSection>
                        <S.ContentAuthorWrapper>
                            <S.ContentTitle to={`/${formatUrl(content.contentType)}/${content.contentId}`}>
                                {content.contentTitle}
                            </S.ContentTitle>
                        </S.ContentAuthorWrapper>
                        <S.ContentAuthorWrapper>
                            <S.ContentAuthor>
                                <Link to="#author">{content.author}</Link>
                            </S.ContentAuthor>
                            <S.ContentSeparate>ㆍ</S.ContentSeparate>
                            <S.ContentType>{formatKorean(content.contentType)}</S.ContentType>
                        </S.ContentAuthorWrapper>

                        <S.ContentRatingSection>
                            <S.ContentRatingScoreWrapper>
                                <RatingFullIcon />
                                <S.ContentRatingScore>{Number(content.totalAverageRating ?? 0).toFixed(1)}</S.ContentRatingScore>
                                <S.ContentRatingCount>
                                    ({content.totalRatingCount})
                                </S.ContentRatingCount>
                            </S.ContentRatingScoreWrapper>
                        </S.ContentRatingSection>
                    </S.ContentInfoSection>
                </S.ContentInfoWrapper>
            </S.ContentItemWrapper>
        </S.ContentItem>
      )


}

export default RankingContentItem;