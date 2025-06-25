import React from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import "../styles/reset.css";
import "../styles/global.css";
import "./Signup.css";

function SignUp() {
  const navigate = useNavigate();

  const handleEmailSignupClick = () => {
    navigate("/users/signup/email");
  };

  const handleKakaoSignupClick = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/kakao";
  };

  const handleNaverSignupClick = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/naver";
  };

  const handleGoogleSignupClick = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  // 아이콘 SVG 컴포넌트
  const KakaoIcon = () => (
    <svg width="28" height="28" viewBox="0 0 40 40" fill="none" style={{ marginLeft: -12, marginRight: 12 }}>
      <ellipse cx="20" cy="20" rx="20" ry="20" fill="#FEE500"/>
      <g transform="translate(12.5,12)">
        <path d="M9 1C4.03 1 0 4.186 0 8.118c0 2.558 1.706 4.8 4.269 6.055-.189.702-.682 2.546-.78 2.94-.123.49.178.484.377.353.155-.104 2.466-1.676 3.463-2.355.543.08 1.1.123 1.671.123 4.97 0 9-3.186 9-7.118C18 4.186 13.97 1 9 1z" fill="#371C1D" />
      </g>
    </svg>
  );
  const NaverIcon = () => (
    <svg width="28" height="28" viewBox="0 0 40 40" fill="none" style={{ marginLeft: -12, marginRight: 12 }}>
      <circle cx="20" cy="20" r="20" fill="#03C75A"/>
      <g transform="translate(12,12)">
        <path d="M2 1h4.5l5.5 7.5V1H16v14h-4.5L6 7.5V15H2V1z" fill="#fff"/>
      </g>
    </svg>
  );
  const GoogleIcon = () => (
    <svg width="28" height="28" viewBox="0 0 40 40" fill="none" style={{ marginLeft: -12, marginRight: 12 }}>
      <circle cx="20" cy="20" r="20" fill="#fff"/>
      <g transform="translate(2,9) scale(0.45)">
        <g>
          <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"></path>
          <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"></path>
          <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"></path>
          <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"></path>
          <path fill="none" d="M0 0h48v48H0z"></path>
        </g>
      </g>
    </svg>
  );

  return (
    <div className="signup-page-container">
      <Header />
      <main className="signup-main">
        <div className="signup-form-wrapper">
          <h1 className="signup-title">회원가입</h1>
          <p className="subtitle">SNS 계정으로 간편하게 시작하세요.</p>

          <div className="social-buttons">
            <button
              className="social-btn naver"
              onClick={handleNaverSignupClick}
            >
              <NaverIcon />
              네이버 계정으로 가입하기
            </button>
            <button
              className="social-btn kakao"
              onClick={handleKakaoSignupClick}
            >
              <KakaoIcon />
              카카오 계정으로 가입하기
            </button>
            <button
              className="social-btn google"
              onClick={handleGoogleSignupClick}
            >
              <GoogleIcon />
              구글계정으로 가입하기
            </button>
          </div>

          <div className="divider">
            <span>또는</span>
          </div>

          <button className="email-btn" onClick={handleEmailSignupClick}>
            이메일로 가입하기
          </button>

          <p className="login-link">
            이미 계정이 있으신가요? <a href="/login">로그인</a>
          </p>
        </div>
      </main>
    </div>
  );
}

export default SignUp;
