import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import BookGrid from "../components/BookGrid";
import axios from '../lib/axios';
import "./MyPageBook.css";

function FavoriteWorks() {
  const [books] = useState([
    {
      id: 1,
      coverUrl: 'https://via.placeholder.com/140x200?text=작품+1',
      title: '임시 작품 제목 1',
      author: '작가A'
    },
    {
      id: 2,
      coverUrl: 'https://via.placeholder.com/140x200?text=작품+2',
      title: '임시 작품 제목 2',
      author: '작가B'
    },
    {
      id: 3,
      coverUrl: 'https://via.placeholder.com/140x200?text=작품+3',
      title: '임시 작품 제목 3',
      author: '작가C'
    },
    {
      id: 4,
      coverUrl: 'https://via.placeholder.com/140x200?text=작품+4',
      title: '임시 작품 제목 4',
      author: '작가D'
    },
    {
      id: 5,
      coverUrl: 'https://via.placeholder.com/140x200?text=작품+5',
      title: '임시 작품 제목 5',
      author: '작가E'
    },
    {
        id: 6,
        coverUrl: 'https://via.placeholder.com/140x200?text=작품+5',
        title: '임시 작품 제목 5',
        author: '작가E'
    },
    {
        id: 1,
        coverUrl: 'https://via.placeholder.com/140x200?text=작품+1',
        title: '임시 작품 제목 1',
        author: '작가A'
      },
      {
        id: 2,
        coverUrl: 'https://via.placeholder.com/140x200?text=작품+2',
        title: '임시 작품 제목 2',
        author: '작가B'
      },
      {
        id: 3,
        coverUrl: 'https://via.placeholder.com/140x200?text=작품+3',
        title: '임시 작품 제목 3',
        author: '작가C'
      },
      {
        id: 4,
        coverUrl: 'https://via.placeholder.com/140x200?text=작품+4',
        title: '임시 작품 제목 4',
        author: '작가D'
      },
      {
        id: 5,
        coverUrl: 'https://via.placeholder.com/140x200?text=작품+5',
        title: '임시 작품 제목 5',
        author: '작가E'
      },
      {
          id: 6,
          coverUrl: 'https://via.placeholder.com/140x200?text=작품+5',
          title: '임시 작품 제목 5',
          author: '작가E'
      },
      {
          id: 6,
          coverUrl: 'https://via.placeholder.com/140x200?text=작품+5',
          title: '임시 작품 제목 5',
          author: '작가E'
      }
  ]);
  const [sort, setSort] = useState("updated"); // updated | recent
  const [type, setType] = useState("all")

  return (
    <>
        <Header />
        <div className="mypage-container">
            <Sidebar />
            <div className="mypage-main">
                <div className="mypage-books-container">
                    <h2 className="mypage-books-title">관심 작품</h2>
                    <div className="mypage-books-sort-btn-container">
                      <div className="mypage-books-select-type">
                          <button className={`sort-btn${type==='all' ? ' active' : ''}`} onClick={()=>setType('all')}>전체</button>
                          <button className={`sort-btn${type==='webtoon' ? ' active' : ''}`} onClick={()=>setType('webtoon')}>웹툰</button>
                          <button className={`sort-btn${type==='webnovel' ? ' active' : ''}`} onClick={()=>setType('webnovel')}>웹소설</button>
                      </div>
                      <div className="mypage-books-select-sort">
                          <button className={`sort-btn${sort==='updated' ? ' active' : ''}`} onClick={()=>setSort('updated')}>업데이트순</button>
                          <button className={`sort-btn${sort==='recent' ? ' active' : ''}`} onClick={()=>setSort('recent')}>최근순</button>
                      </div>
                    </div>
                </div>
                <div className="mypage-books-search-wrapper">
                  <input
                    className="mypage-books-search-input"
                    type="text"
                    placeholder="작품명, 작가명 검색"
                  />
                  <button className="mypage-books-search-btn" type="button">
                    <svg width="18" height="18" viewBox="0 0 20 20" fill="none">
                      <circle cx="9" cy="9" r="7" stroke="#b0b0b0" strokeWidth="2"/>
                      <line x1="14.2" y1="14.2" x2="18" y2="18" stroke="#b0b0b0" strokeWidth="2" strokeLinecap="round"/>
                    </svg>
                  </button>
                </div>
                <section className="mypage-book-section">
                    <BookGrid books={books}/>
                </section>
                
            </div>
        </div>
    </>
  );
}

export default FavoriteWorks;
// 관심작품 페이지 라우트 예시: <Route path="/mypage/favorites" element={<FavoriteWorks />} /> 