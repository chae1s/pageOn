import React, {useState, useEffect} from "react";
import "../../styles/reset.css"
import "../../styles/global.css"
import axios from "axios";
import Sidebar from "../../components/MyPageSidebar";
import { SimpleBook } from "../../types/Book";
import "./MyPage.css"
import BookList from "../../components/BookList";

function RecentViewedWorks() {

    const dummyBooks: SimpleBook[] = [
        {
            id: 1,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/1/webnovel1.png',
            title: '임시 작품 제목 1',
            author: '작가A'
        },
        {
            id: 2,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/2/webnovel2.png',
            title: '임시 작품 제목 2',
            author: '작가B'
        },
        {
            id: 3,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/3/webnovel3.png',
            title: '임시 작품 제목 3',
            author: '작가C'
        },
        {
            id: 4,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/4/webnovel4.png',
            title: '임시 작품 제목 4',
            author: '작가D'
        },
        {
            id: 5,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/5/webnovel5.png',
            title: '임시 작품 제목 5',
            author: '작가E'
        },
        {
            id: 6,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/6/webnovel6.png',
            title: '임시 작품 제목 6',
            author: '작가E'
        },
        {
            id: 7,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/7/webnovel7.png',
            title: '임시 작품 제목 7',
            author: '작가A'
        },
        {
            id: 8,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/8/webnovel8.png',
            title: '임시 작품 제목 8',
            author: '작가B'
        },
        {
            id: 9,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/9/webnovel9.png',
            title: '임시 작품 제목 9',
            author: '작가C'
        },
        {
            id: 10,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/10/webnovel10.png',
            title: '임시 작품 제목 10',
            author: '작가D'
        }
    ]

    const [sort, setSort] = useState<string>("updated") // updated | recent
    const [type, setType] = useState<string>("all")     // all | webtoons | webnovels

    return (
        <div className="main-container">
            <main className="sidebar-main">
                <Sidebar />
                <div className="sidebar-right-wrap">
                    <h2 className="mypage-title">최근 조회 작품</h2>
                    <div className="mypage-books-sort-btn-wrapper">
                        <div className="mypage-books-sort-btn-list">
                            <div className="mypage-books-select-type">
                                <button className={`sort-btn${type==='all' ? ' active' : ''}`} onClick={()=>setType('all')}>전체</button>
                                <button className={`sort-btn${type==='webtoons' ? ' active' : ''}`} onClick={()=>setType('webtoons')}>웹툰</button>
                                <button className={`sort-btn${type==='webnovels' ? ' active' : ''}`} onClick={()=>setType('webnovels')}>웹소설</button>
                            </div>
                            <div className="mypage-books-search-select-sort">
                                <div className="mypage-books-search-group">
                                    <input 
                                        type="text" 
                                        className="mypage-books-search-input" 
                                        placeholder="책 이름을 입력하세요."
                                    />
                                    <button className="mypage-books-search-btn">
                                        <svg width="13" height="13" viewBox="0 0 20 20" fill="none"  className="search-icon">
                                            <circle cx="9" cy="9" r="7" stroke="#888" strokeWidth="2"/>
                                            <line x1="14.2" y1="14.2" x2="20" y2="20" stroke="#888" strokeWidth="2" strokeLinecap="round"/>
                                        </svg>
                                    </button>
                                </div>
                                <div className="mypage-books-sort-group">
                                    <button className={`sort-btn${sort==='updated' ? ' active' : ''}`} onClick={()=>setSort('updated')}>업데이트순</button>
                                    <button className={`sort-btn${sort==='recent' ? ' active' : ''}`} onClick={()=>setSort('recent')}>최근순</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <section className="mypage-books-section">
                        <BookList simpleBooks={dummyBooks} home={false}></BookList>
                    </section>
                </div>
            </main>
        </div>
    )

}

export default RecentViewedWorks;