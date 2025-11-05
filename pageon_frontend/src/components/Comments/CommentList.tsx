import React, {useState, useEffect} from "react";
import { Link, NavLink} from "react-router-dom";
import { SortBtn } from "../../styles/Layout.styles";
import * as C from "../Styles/Comment.styles";
import { Comment, CreateComment } from "../../types/Comments";
import LikeEmptyIcon from "../../assets/emptyHeartIcon.png";
import LikeFullIcon from "../../assets/fullHeartIcon.png";
import BlankCheckboxIcon from "../../assets/blankCheckboxIcon.png";
import FullCheckboxIcon from "../../assets/fullCheckboxIcon.png";
import api from "../../api/axiosInstance";

interface Props {
    comments: Comment[];
    mypage: boolean;
    contentType?: string;
    episodeId?: string;
}

function CommentList({comments, mypage, contentType, episodeId} : Props) {
    

    const [sort, setSort] = useState<string>("liked") // recent | liked
    const [commentText, setCommentText] = useState<string>("")
    const [isSpoiler, setIsSpoiler] = useState<boolean>(false)

    const handleRegister = async () => {
        const text = commentText.trim()

        if (mypage || !text || !contentType || !episodeId) return
        
        const newComment: CreateComment = {
            text: text,
            isSpoiler: isSpoiler
        }

        console.log(newComment)

        try {
            await api.post(`/${contentType}/episodes/${episodeId}/comments`, newComment);
                
            setCommentText("");
            setIsSpoiler(false);
        } catch (error) {
            console.error("댓글 등록 실패 : ", error);
        }
        
    }

    return (
        <C.CommentList>
            {!mypage && (
                <>
                    <C.CommentHeader>
                        <C.CommentCount>
                            댓글 {comments.length}개
                        </C.CommentCount>
                        <C.SortBtnList>
                            <SortBtn $active={sort === "recent"} type="button" onClick={()=>setSort('recent')}>최신순</SortBtn>
                            <SortBtn $active={sort === "liked"} type="button" onClick={()=>setSort('liked')}>공감순</SortBtn>
                        </C.SortBtnList>
                    </C.CommentHeader>
                    <C.CommentInputSection>
                        <C.CommentInputWrap>
                            <C.CommentInputFlex>
                                <C.CommentInputTextarea
                                    value={commentText}
                                    onChange={(e)=>setCommentText(e.target.value)}
                                >
                                </C.CommentInputTextarea>
                            </C.CommentInputFlex>
                            {commentText.trim().length > 0 && (
                                <C.CommentInputBtn onClick={handleRegister}>
                                    등록
                                </C.CommentInputBtn>
                            )}
                        </C.CommentInputWrap>
                        <C.CommentSpoilerCheckSection>
                            <C.CommentSpoilerCheckWrap>
                                <C.CommentSpoilerCheckboxWrap onClick={() => setIsSpoiler(prev => !prev)}>
                                    <C.CommentSpoilerCheckbox
                                        type="checkbox"
                                        checked={isSpoiler}
                                        onChange={(e)=>setIsSpoiler(e.target.checked)}
                                        style={{ display: 'none' }}
                                    />
                                    {isSpoiler ? (
                                        <C.CommentSpoilerCheckboxCheckIcon src={FullCheckboxIcon} />
                                    ) : (
                                        <C.CommentSpoilerCheckboxEmptyIcon src={BlankCheckboxIcon} />
                                    )}
                                </C.CommentSpoilerCheckboxWrap>
                                <C.CommentSpoilerText onClick={() => setIsSpoiler(prev => !prev)}>
                                    댓글에 스포일러 포함
                                </C.CommentSpoilerText>
                            </C.CommentSpoilerCheckWrap>
                        </C.CommentSpoilerCheckSection>
                    </C.CommentInputSection>
                </>
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
                            <C.CommentInfo>
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
                                        <C.LikeEmptyIcon src={LikeEmptyIcon} />
                                        <span>{comment.likes}</span>
                                    </C.CommentLikeBtn>
                                </div>
                            </C.CommentInfo>
                        </C.CommentListLi>
                    ))
                )}
            </C.CommentListUl>
        </C.CommentList>
        
    )
}

export default CommentList