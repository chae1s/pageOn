import React, {useState, useEffect} from "react";
import { Link, NavLink} from "react-router-dom";
import "../styles/reset.css";
import "../styles/global.css";
import "./CommentList.css"
import { Comment } from "../types/Comments";

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
        <div className="comments-list">
            {!mypage && (
            <div className="sort-btn-list">
                <button type="button" className={`sort-btn${sort==='recent' ? ' active' : ''}`} onClick={()=>setSort('recent')}>최신순</button>
                <button type="button" className={`sort-btn${sort==='liked' ? ' active' : ''}`} onClick={()=>setSort('liked')}>공감순</button>
            </div>
            )}
            <ul>
                {comments.length === 0 ? (
                    <p>{mypage ? "작성한 댓글이 없습니다." : "댓글이 없습니다."}</p>
                ) : (
                    comments.map((comment) => (
                        <li>
                            <div className="comment-episode">
                                <div className="comment-title">{comment.bookTitle}</div>
                                <div className="comment-episode-num">{comment.episodeNum}화</div>
                            </div>
                            <div className="comment-content">
                                <p>{comment.content}</p>
                            </div>
                            <div className="comment-info">
                                <div className="comment-info-left">
                                    <div className="comment-user-info">
                                        <div className="comment-nickname">
                                            {comment.nickname}
                                        </div>
                                    </div>
                                    <div className="comment-date-btn">
                                        <div className="comment-date">{comment.date}</div>
                                        <div className="comment-space"></div>
                                        <button className="comment-btn comment-report-btn">신고</button>
                                        <div className="comment-btn-divider"></div>
                                        <button type="button" className="comment-btn comment-edit-btn">수정</button>
                                    </div>  
                                </div>
                                <div className="comment-info-right">
                                    <button type="button" className="comment-like-btn">
                                        <LikeEmptyIcon />
                                        <span>{comment.likes}</span>
                                    </button>
                                </div>
                            </div>
                        </li>
                    ))
                )}
            </ul>
        </div>
        
    )
}

export default CommentList