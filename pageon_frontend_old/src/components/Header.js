import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/reset.css";
import "../styles/global.css";
import "./Header.css";

function Header() {
  const navigate = useNavigate();

  // 로그인 상태를 state로 관리 (로그아웃 시 즉시 반영)
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("accessToken"));

  useEffect(() => {
    // 다른 탭에서 로그아웃/로그인 시 동기화
    const handleStorage = () => {
      setIsLoggedIn(!!localStorage.getItem("accessToken"));
    };
    window.addEventListener("storage", handleStorage);
    return () => window.removeEventListener("storage", handleStorage);
  }, []);

  const handleSignupClick = () => {
    navigate("/users/signup");
  };

  const handleLoginClick = () => {
    navigate("/users/login");
  };

  const handleMyLibraryClick = () => {
    // 내 서재 페이지로 이동 (예: /my-library)
    navigate("/users/my-library");
  };

  const handleMyInfoClick = () => {

    // 마이페이지로 이동
    navigate("/users/my-page");
  };

  const handleLogoutClick = async () => {
    alert("로그아웃 하시겠습니까?");
    // GET 방식으로 로그아웃 API 호출
    try {
      const response = await fetch("/api/users/logout", {
        method: "GET",
        credentials: "include", // 쿠키 등 인증정보 필요시
      });
      if (response.ok) {
        localStorage.removeItem("accessToken");
        setIsLoggedIn(false);
        navigate("/", { replace: true });
      } else {
        alert("로그아웃에 실패했습니다.");
      }
    } catch (error) {
      alert("로그아웃 중 오류가 발생했습니다.");
    }
  };

  return (
    <header className="home-header">
      <div className="header-inner">
        <div className="logo">
          <a href="/" >
            <span>pageOn</span>
          </a>
        </div>
        <div className="nav-list">
          <a>
           <span href="/" className="nav-link">홈</span>
          </a>
          <a>
           <span href="/" className="nav-link">웹툰</span>
          </a>
          <a>
           <span href="/" className="nav-link">웹소설</span>
          </a>
          <a>
           <span href="/" className="nav-link">이벤트</span>
          </a>
        </div>
        <div className="header-right">
          <div className="books-search-wrapper">
            <input
              className="books-search-input"
              type="text"
              placeholder="작품명, 작가명 검색"
            />
            <button className="books-search-btn" type="button">
              <svg width="18" height="18" viewBox="0 0 20 20" fill="none">
                <circle cx="9" cy="9" r="7" stroke="#b0b0b0" strokeWidth="2"/>
                <line x1="14.2" y1="14.2" x2="18" y2="18" stroke="#b0b0b0" strokeWidth="2" strokeLinecap="round"/>
              </svg>
            </button>
          </div>
          <div>
            {isLoggedIn ? (
              <>
                <button className="my-library-btn" onClick={handleMyLibraryClick}>내 서재</button>
                <button className="my-info-btn" onClick={handleMyInfoClick}>마이페이지</button>
              </>
            ) : (
              <>
                <button className="signup-btn" onClick={handleSignupClick}>회원가입</button>
                <button className="login-btn" onClick={handleLoginClick}>로그인</button>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}

export default Header;
