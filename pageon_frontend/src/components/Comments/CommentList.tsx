import React, {useState, useEffect} from "react";
import { Link, NavLink} from "react-router-dom";
import { SortBtn } from "../../styles/Layout.styles";
import * as C from "../Styles/Comment.styles";
import { Comment } from "../../types/Comments";

interface Props {
    comments: Comment[];
    mypage: boolean
}

function CommentList({comments, mypage} : Props) {
    const LikeEmptyIcon = () => (
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
            <path
                d="M9 16s-5.5-4.35-7.2-6.19C.6 8.36.5 6.13 2.01 4.61 3.53 3.09 5.98 3.09 7.5 4.61L9 6.11l1.5-1.5c1.52-1.52 3.97-1.52 5.49 0 1.51 1.52 1.41 3.75.21 5.2C14.5 11.65 9 16 9 16z"
                fill="#FFF"
                stroke="#444"
                strokeWidth="1"
            />
        </svg>
    )

    const LikeFullIcon = () => (
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
            <path
                d="M9 16s-5.5-4.35-7.2-6.19C.6 8.36.5 6.13 2.01 4.61 3.53 3.09 5.98 3.09 7.5 4.61L9 6.11l1.5-1.5c1.52-1.52 3.97-1.52 5.49 0 1.51 1.52 1.41 3.75.21 5.2C14.5 11.65 9 16 9 16z"
                fill="#444"
                stroke="#444"
                strokeWidth="1"
            />
        </svg>
    )

    const [sort, setSort] = useState<string>("liked") // recent | liked

    return (
        <C.CommentList>
            {!mypage && (
                <C.CommentHeader>
                    <C.CommentCount>
                        댓글 {comments.length}개
                    </C.CommentCount>
                    <C.SortBtnList>
                        <SortBtn $active={sort === "recent"} type="button" onClick={()=>setSort('recent')}>최신순</SortBtn>
                        <SortBtn $active={sort === "liked"} type="button" onClick={()=>setSort('liked')}>공감순</SortBtn>
                    </C.SortBtnList>
                </C.CommentHeader>
            
            )}
            <C.CommentListUl>
                {comments.length === 0 ? (
                    <C.CommentListEmptyText>{mypage ? "작성한 댓글이 없습니다." : "댓글이 없습니다."}</C.CommentListEmptyText>
                ) : (
                    comments.map((comment) => (
                        <C.CommentListLi>
                            <C.CommentEpisode>
                                <C.CommentTitle>{comment.bookTitle}</C.CommentTitle>
                                <div>{comment.episodeNum}화</div>
                            </C.CommentEpisode>
                            <C.CommentContentWrap>
                                <C.CommentContent>{comment.content}</C.CommentContent>
                            </C.CommentContentWrap>
                            <C.Commentinfo>
                                <C.CommentInfoLeft>
                                    <C.CommentUserInfo>
                                        <div>
                                            {comment.nickname}
                                        </div>
                                    </C.CommentUserInfo>
                                    <C.CommentDateBtn>
                                        <div>{comment.date}</div>
                                        <C.CommentSpace></C.CommentSpace>
                                        <C.CommentReportBtn>신고</C.CommentReportBtn>
                                        <C.CommentBtnDivider></C.CommentBtnDivider>
                                        <C.CommentEditBtn type="button">수정</C.CommentEditBtn>
                                    </C.CommentDateBtn>  
                                </C.CommentInfoLeft>
                                <div>
                                    <C.CommentLikeBtn type="button">
                                        <LikeEmptyIcon />
                                        <span>{comment.likes}</span>
                                    </C.CommentLikeBtn>
                                </div>
                            </C.Commentinfo>
                        </C.CommentListLi>
                    ))
                )}
            </C.CommentListUl>
        </C.CommentList>
        
    )
}

export default CommentList