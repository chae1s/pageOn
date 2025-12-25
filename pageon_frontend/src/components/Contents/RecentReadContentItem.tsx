import { RecentReadContent } from "../../types/Content";
import * as S from "../Styles/LibraryContent.styles";
import { formatDate } from "../../utils/formatData";
import rightArrowIcon from "../../assets/rightArrowIcon.png";
import { useNavigate } from "react-router-dom";
import { formatKorean } from "../../utils/formatContentType";

interface Props {
    content: RecentReadContent
}

function RecentReadContentItem({ content }: Props) {
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

    const navigate = useNavigate();

    const handleContentClick = (contentType: string, contentId: number) => (e:React.MouseEvent<HTMLDivElement>) => {
        e.preventDefault();


        if (contentType === 'WEBNOVEL') {
            navigate(`/webnovels/${contentId}`);
        } else if (contentType === 'WEBTOON') {
            navigate(`/webtoons/${contentId}`);
        }
        
    }

    const handleEpisodeReadClick = (contentType: string, contentId: number, episodeId: number) => (e:React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        
        if (contentType === 'WEBNOVEL') {
            navigate(`/webnovels/${contentId}/viewer/${episodeId}`);
        } else if (contentType === 'WEBTOON') {
            navigate(`/webtoons/${contentId}/viewer/${episodeId}`);
        }

    }

    return (
        <S.ContentItem>
            <S.ContentImageCover>
                <S.ContentImage
                    src={content.cover || "https://via.placeholder.com/140x200"}
                    alt={content.title}
                    onClick={handleContentClick(content.contentType, content.contentId)}
                />
            </S.ContentImageCover>
            <S.ContentInfoCover $history={true}>
                <S.ContentTitleWrapper>
                    <S.ContentTitle onClick={handleContentClick(content.contentType, content.contentId)}>{content.title}</S.ContentTitle>
                </S.ContentTitleWrapper>

                <S.ContentInfoWrapper>
                    <S.ContentAuthor>{content.penName}</S.ContentAuthor>
                    <S.ContentSeparate>ㆍ</S.ContentSeparate>
                    <S.ContentType>{formatKorean(content.contentType)}</S.ContentType>
                </S.ContentInfoWrapper>
                <S.ContentLastReadHistory>
                    {formatDate(content.lastReadAt)} 열람
                </S.ContentLastReadHistory>
                <S.ContentMetaRow>
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
            <S.ContentEpisodeReadBtn type="button" onClick={handleEpisodeReadClick(content.contentType, content.contentId, content.lastReadEpisodeId)}>
                <S.ContentEpisodeReadBtnText>
                    이어보기
                </S.ContentEpisodeReadBtnText>
                <S.ContentEpisodeReadIcon src={rightArrowIcon}/>
            </S.ContentEpisodeReadBtn>
        </S.ContentItem>
    );
}

export default RecentReadContentItem;