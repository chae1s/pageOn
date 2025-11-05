import React from "react";
import { SortBtn } from "../../styles/Layout.styles";
import * as C from "../Styles/Comment.styles";
import { Comment } from "../../types/Comments";
import LikeEmptyIcon from "../../assets/emptyHeartIcon.png"
import LikeFullIcon from "../../assets/fullHeartIcon.png"
import { useNavigate } from "react-router-dom";

interface Props {
    comment: Comment;
    contentType: string;
    contentId: string;
    episodeId: string;
}

function BestComment({comment, contentType, contentId, episodeId} : Props) {
    
    const navigate = useNavigate();

    const handleGoToComments = (e:React.MouseEvent) => {
        e.preventDefault();

        const scrollPosition = window.scrollY;
        const commentUrl = `/${contentType}/${contentId}/viewer/${episodeId}/comments`;
        
        sessionStorage.setItem("scrollPosition", scrollPosition.toString());

        navigate(commentUrl);
    }

    return (
        <C.CommentList>
            <C.CommentHeader>
                    <C.CommentCount>
                        총 댓글 개수
                    </C.CommentCount>
                    <C.CommentListBtn onClick={handleGoToComments}>
                        댓글 보기
                    </C.CommentListBtn>
                </C.CommentHeader>
            <C.CommentListLi>
                <C.CommentInfo>
                    <C.CommentBestInfo>
                        <C.CommentBestIcon>
                            BEST
                        </C.CommentBestIcon>
                        <C.CommentBestUserInfo>
                            <div>{comment.nickname}</div>
                        </C.CommentBestUserInfo>
                    </C.CommentBestInfo>
                </C.CommentInfo>
                <C.CommentContentWrap>
                    <C.CommentContent>{comment.content}</C.CommentContent>
                </C.CommentContentWrap>
                <C.CommentDateBtn>
                    <div>{comment.date}</div>
                </C.CommentDateBtn>
            </C.CommentListLi>
        </C.CommentList>
        
    )
}

export default BestComment;