import { useNavigate } from "react-router-dom";
import { SimpleContent } from "../../types/Content";
import * as S from "../Styles/SimpleContent.styles"
import { formatKorean, formatUrl } from "../../utils/formatContentType";
interface Props {
    content: SimpleContent;
}

function MyPageSimpleContentItem({content} : Props) {
    
    const navigate = useNavigate();

    const handleContentClick = (contentType: string, contentId: number) => (e:React.MouseEvent<HTMLDivElement>) => {
        e.preventDefault();

        navigate(`/${formatUrl(contentType)}/${contentId}`)
        
    }

    return (
        <S.ContentItem>
            <S.ContentImageCover onClick={handleContentClick(content.contentType, content.id)}>
                <S.ContentImage
                    src={content.cover || "https://via.placeholder.com/140x200"}
                    alt={content.title}
                />
            </S.ContentImageCover>
            <S.ContentInfoCover>
                <S.ContentTitle onClick={handleContentClick(content.contentType, content.id)}>{content.title}</S.ContentTitle>
                <S.ContentInfoWrapper>
                    <S.ContentAuthor>{content.author}</S.ContentAuthor>
                    <S.ContentSeparate>ㆍ</S.ContentSeparate>
                    <S.ContentType>{formatKorean(content.contentType)}</S.ContentType>
                </S.ContentInfoWrapper>
            </S.ContentInfoCover>
        </S.ContentItem>
    )
}

export default MyPageSimpleContentItem;