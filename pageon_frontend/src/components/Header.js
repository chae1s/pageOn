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
    navigate("/my-library");
  };

  const handleMyInfoClick = () => {
    // 내 정보 페이지로 이동 (예: /my-info)
    navigate("/my-info");
  };

  const handleLogoutClick = () => {
    const confirmed = window.confirm("로그아웃 하시겠습니까?");
    if (confirmed) {
      localStorage.removeItem("accessToken");
      setIsLoggedIn(false);
      // 필요시 홈으로 이동
      navigate("/", { replace: true });
    }
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
              <button className="logout-btn" onClick={handleLogoutClick}>로그아웃</button>
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
