import React, { useState } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import "./MyPageBook.css";
import CommentList from "../components/CommentList";

function MyComments() {
  const [comments] = useState([
    {
      id: 1,
      bookTitle: "작품 제목 1",
      bookCover: "https://via.placeholder.com/60x80?text=작품+1",
      content: "정말 재미있게 읽었습니다! 다음 편도 기대돼요.",
      episodeNum: 12,
      nickname: "닉네임1",
      date: "2024-06-01",
      likes: 12
    },
    {
      id: 2,
      bookTitle: "작품 제목 2",
      bookCover: "https://via.placeholder.com/60x80?text=작품+2",
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

  return (
    <>
      <Header />
      <div className="mypage-container">
        <Sidebar />
        <div className="mypage-main">
          <div className="mypage-books-container">
            <h2 className="mypage-books-title">내가 쓴 댓글</h2>
          </div>
          <section className="mypage-book-section">
            <CommentList comments={comments} mypage={true} />
          </section>
        </div>
      </div>
    </>
  );
}

export default MyComments; 