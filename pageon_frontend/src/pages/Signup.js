import React from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import "../styles/reset.css";
import "../styles/global.css";
import "./Signup.css";

function SignUp() {
  const navigate = useNavigate();

  const handleEmailSignupClick = () => {
    navigate("/signup/email");
  };

  return (
    
    <div className="signup-page-container">
        <Header />
            <main className="signup-main">
                <div className="signup-form-wrapper">
                    <h1 className="signup-title">회원가입</h1>
                    <p className="subtitle">SNS 계정으로 간편하게 시작하세요.</p>
                    
                    <div className="social-buttons">
                        <button className="social-btn naver">네이버로 시작하기</button>
                        <button className="social-btn kakao">카카오로 시작하기</button>
                        <button className="social-btn google">구글로 시작하기</button>
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
