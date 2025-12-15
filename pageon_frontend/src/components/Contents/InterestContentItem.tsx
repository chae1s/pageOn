import React from "react";
import { InterestContent } from "../../types/Content";
import * as S from "../Styles/LibraryContent.styles";
import { formatDate } from "../../utils/formatData";

interface Props {
    content: InterestContent;
}

function InterestContentItem({ content }: Props) {
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

    const contentTypeMap: Record<string, string> = {
        WEBNOVEL: "웹소설",
        WEBTOON: "웹툰"
    }

    return (
        <S.ContentItem>
            <S.ContentImageCover>
                <S.ContentImage
                    src={content.cover || "https://via.placeholder.com/140x200"}
                    alt={content.title}
                />
            </S.ContentImageCover>
            <S.ContentInfoCover>
                <S.ContentTitleWrapper>
                    <S.ContentTitle>{content.title}</S.ContentTitle>
                </S.ContentTitleWrapper>

                <S.ContentInfoWrapper>
                    <S.ContentAuthor>{content.penName}</S.ContentAuthor>
                    <S.ContentSeparate>ㆍ</S.ContentSeparate>
                    <S.ContentType>{contentTypeMap[content.contentType]}</S.ContentType>
                </S.ContentInfoWrapper>

                <S.ContentMetaRow>
                    <S.ContentEpisodeUpdatedAt>
                        {formatDate(content.episodeUpdatedAt)}
                    </S.ContentEpisodeUpdatedAt>
                    <S.ContentSeparate>ㆍ</S.ContentSeparate>
                    
                    {content.status === "ONGOING" && (
                        <>
                            <S.ContentMetaItem>
                                {dayKoMap[content.serialDay]}
                            </S.ContentMetaItem>
                            <S.ContentSeparate>ㆍ</S.ContentSeparate>
                        </>
                    )}
                    <S.ContentStatus>
                        {statusMap[content.status]}
                    </S.ContentStatus>
                </S.ContentMetaRow>
            </S.ContentInfoCover>
        </S.ContentItem>
    );
}

export default InterestContentItem;