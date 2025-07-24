import React, { useState } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import BookGrid from "../components/BookGrid";
import "./MyPageBook.css";

function RecentViewedWorks() {
  const [books] = useState([
    {
      id: 1,
      coverUrl: 'https://via.placeholder.com/140x200?text=소설+1',
      title: '최근 본 작품 1',
      author: '작가A'
    },
    {
      id: 2,
      coverUrl: 'https://via.placeholder.com/140x200?text=소설+2',
      title: '최근 본 작품 2',
      author: '작가B'
    },
    {
      id: 3,
      coverUrl: 'https://via.placeholder.com/140x200?text=소설+3',
      title: '최근 본 작품 3',
      author: '작가C'
    },
    {
      id: 4,
      coverUrl: 'https://via.placeholder.com/140x200?text=소설+4',
      title: '최근 본 작품 4',
      author: '작가D'
    },
    {
      id: 5,
      coverUrl: 'https://via.placeholder.com/140x200?text=소설+5',
      title: '최근 본 작품 5',
      author: '작가E'
    },
    {
      id: 6,
      coverUrl: 'https://via.placeholder.com/140x200?text=소설+6',
      title: '최근 본 작품 6',
      author: '작가F'
    },
    {
      id: 6,
      coverUrl: 'https://via.placeholder.com/140x200?text=소설+6',
      title: '최근 본 작품 6',
      author: '작가F'
    }
  ]);
  const [sort, setSort] = useState("recent"); // recent | updated
  const [type, setType] = useState("all")
  return (
    <>
      <Header />
      <div className="mypage-container">
        <Sidebar />
        <div className="mypage-main">
          <div className="mypage-books-container">
            <h2 className="mypage-books-title">최근 조회 작품</h2>
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
              <input
                className="mypage-books-search-input"
                type="text"
                placeholder="작품명, 작가명 검색"
              />
          </div>
          <section className="mypage-book-section">
            <BookGrid books={books}/>
          </section>
        </div>
      </div>
    </>
  );
}

export default RecentViewedWorks; 