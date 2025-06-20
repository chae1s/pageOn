import React from "react";
import "../styles/reset.css";
import "../styles/global.css";
import "./Home.css";
import Header from "../components/Header";


function Home() {
  return (
    <div className="home-container">
      <Header />
      <main className="home-main">
        <section className="main-banner">
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
          <div className="popular-list">
            {[1,2,3,4,5].map((i) => (
              <div key={i} className="popular-item">
                <img
                  src={`https://via.placeholder.com/140x200?text=웹툰+${i}`}
                  alt={`웹툰 ${i}`}
                  className="popular-img"
                />
                <div className="popular-title">웹툰 제목 {i}</div>
                <div className="popular-author">작가명</div>
              </div>
            ))}
          </div>
        </section>
        <section className="section-popular">
          <h2>인기 웹소설</h2>
          <div className="popular-list">
            {[1,2,3,4,5].map((i) => (
              <div key={i} className="popular-item">
                <img
                  src={`https://via.placeholder.com/140x200?text=웹소설+${i}`}
                  alt={`웹소설 ${i}`}
                  className="popular-img"
                />
                <div className="popular-title">웹소설 제목 {i}</div>
                <div className="popular-author">작가명</div>
              </div>
            ))}
          </div>
        </section>
      </main>
      <footer className="home-footer">
        <div className="footer-inner">
          &copy; {new Date().getFullYear()} 웹툰 & 웹소설. All rights reserved.
        </div>
      </footer>
    </div>
  );
}

export default Home;