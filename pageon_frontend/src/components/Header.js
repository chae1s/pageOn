import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/reset.css";
import "../styles/global.css";
import "./Header.css";

function Header() {
  const navigate = useNavigate();

  const handleSignupClick = () => {
    navigate("/users/signup");
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
          <button className="signup-btn" onClick={handleSignupClick}>회원가입</button>
          <button className="login-btn">로그인</button>
        </div>
      </div>
    </header>
  );
}

export default Header;
