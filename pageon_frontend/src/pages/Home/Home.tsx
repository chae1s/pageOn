import React, {useEffect, useState} from "react";
import "../../styles/reset.css";
import "../../styles/global.css";
import "./Home.css"
import { SimpleBook } from "../../types/Book";
import BookList from "../../components/BookList";

function Home() {
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
    return(
        <div className="main-container">
            <main className="no-sidebar-main">
                <section className="home-banner">
                    <div className="banner-text">
                        <h1>
                            인기 웹툰과 웹소설을<br />한 곳에서 즐기세요
                        </h1>
                        <p>
                            최신 인기작부터 다양한 장르의 작품까지<br />
                            지금 바로 감상해보세요!
                        </p>
                            <div className="banner-btns">
                            <button className="go-webtoon-btn">웹툰 보러가기</button>
                            <button className="go-webnovel-btn">웹소설 보러가기</button>
                        </div>
                    </div>
                    <div className="banner-image">
                        <img
                        src="https://cdn.ridicdn.net/cover/1/cover13/2023/12/cover_1000000001_1701400000.jpg"
                        alt="메인 배너"
                        />
                    </div>
                </section>
                <section className="section-popular">
                    <h2>인기 웹툰</h2>
                    <BookList simpleBooks={ dummyBooks } home={ true } ></BookList>    
                </section>
                <section className="section-popular">
                    <h2>인기 웹소설</h2>
                    <BookList simpleBooks={ dummyBooks } home={ true } ></BookList>    
                </section>
            </main>
        </div>
        
    )
}

export default Home;