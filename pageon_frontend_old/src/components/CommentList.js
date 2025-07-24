import React from "react";
import "./CommentList.css";

function CommentList( {comments, mypage}) {
    return (
        <div className="comments-list">
              {comments.length === 0 ? (
                <p>{mypage ? "작성한 댓글이 없습니다." : "댓글이 없습니다."}</p>
              ) : (
                comments.map((comment) => (
                  <div key={comment.id} className="comment-item">
                    <div className="comment-body">
                      <div className="comment-content">{comment.content}</div>
                      <div className="comment-title">{comment.bookTitle}  {comment.episodeNum}화</div>
                      <div className="comment-nickname">{comment.nickname}</div>
                      <div className="comment-meta">
                        <div className="comment-date">{comment.date}</div>
                        <div className="comment-btn-container">
                          <button className="comment-btn comment-edit-btn">수정</button>
                          <button className="comment-btn comment-report-btn">신고</button>
                        </div>
                        <span className="comment-like">좋아요 {comment.likes}</span>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
    );
}

export default CommentList;