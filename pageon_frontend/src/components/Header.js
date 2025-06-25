import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/reset.css";
import "../styles/global.css";
import "./Header.css";

function Header() {
  const navigate = useNavigate();

  // accessToken이 localStorage에 있으면 로그인 상태로 간주
  const isLoggedIn = !!localStorage.getItem("accessToken");

  const handleSignupClick = () => {
    navigate("/users/signup");
  };

  const handleLoginClick = () => {
    navigate("/users/login");
  };

  const handleMyLibraryClick = () => {
    // 내 서재 페이지로 이동 (예: /my-library)
    navigate("/my-library");
  };

  const handleMyInfoClick = () => {
    // 내 정보 페이지로 이동 (예: /my-info)
    navigate("/my-info");
  };

  return (
    <header className="home-header">
      <div className="header-inner">
        <a href="/" className="logo">
          <span>pageOn</span>
        </a>
        <nav>
          <ul className="nav-list">
            <li><a href="/" className="nav-link">홈</a></li>
            <li><a href="#" className="nav-link">웹툰</a></li>
            <li><a href="#" className="nav-link">웹소설</a></li>
            <li><a href="#" className="nav-link">이벤트</a></li>
          </ul>
        </nav>
        <div>
          {isLoggedIn ? (
            <>
              <button className="my-library-btn" onClick={handleMyLibraryClick}>내 서재</button>
              <button className="my-info-btn" onClick={handleMyInfoClick}>내 정보</button>
              <button className="logout-btn">로그아웃</button>
            </>
          ) : (
            <>
              <button className="signup-btn" onClick={handleSignupClick}>회원가입</button>
              <button className="login-btn" onClick={handleLoginClick}>로그인</button>
            </>
          )}
        </div>
      </div>
    </header>
  );
}

export default Header;
