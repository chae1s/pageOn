import React, { useState, useEffect } from "react";
import * as S from "./Viewer.styles";
import closeIcon from "../../assets/crossIcon.png";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import CommentList from "../../components/Comments/CommentList";


function Comment() {
    const { contentType, contentId, episodeId } = useParams<{contentType: string; contentId: string; episodeId: string}>();

    const { state } = useLocation();
    const navigate = useNavigate();

    

    const handleClose = (e:React.MouseEvent<HTMLAnchorElement>) => {
        e.preventDefault();

        navigate(-1);
    };


    const [comments] = useState([
        {
          id: 1,
          bookTitle: "작품 제목 1",
          bookCover: "https://d2ge55k9wic00e.cloudfront.net/webnovels/1/webnovel1.png",
          content: "정말 재미있게 읽었습니다! 다음 편도 기대돼요.",
          episodeNum: 12,
          nickname: "닉네임1",
          date: "2024-06-01",
          likes: 12
        },
        {
          id: 2,
          bookTitle: "작품 제목 2",
          bookCover: "https://d2ge55k9wic00e.cloudfront.net/webnovels/1/webnovel1.png",
          content: "스토리가 신선해서 좋았어요.",
          episodeNum: 3,
          nickname: "닉네임2",
          date: "2024-05-28",
          likes: 5
        },
        {
          id: 3,
          bookTitle: "작품 제목 3",
          bookCover: "https://via.placeholder.com/60x80?text=작품+3",
          content: "그림체가 마음에 들어요.",
          episodeNum: 7,
          nickname: "닉네임3",
          date: "2024-05-20",
          likes: 8
        }
    ]);
    
      
    if (!contentType || !episodeId || !contentId) {
        return null;
    }

    return (
        <S.Viewer>
            <S.CommentPageHeaderSection>
                <S.CommentPageHeader>
                    <S.CommentPageCloseLink to="/" onClick={handleClose}>
                        <S.CommentPageCloseIcon src={closeIcon}/>
                    </S.CommentPageCloseLink>
                </S.CommentPageHeader>
            </S.CommentPageHeaderSection>
            <S.ViewerCommentSection>
                <CommentList comments={comments} mypage={false} contentType = {contentType} episodeId={ episodeId }/>
            </S.ViewerCommentSection>
        </S.Viewer>
    )


}

export default Comment;