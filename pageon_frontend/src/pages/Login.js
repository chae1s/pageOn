import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import "../styles/reset.css";
import "../styles/global.css";
import "./Signup.css";
import axios from "axios";

function Login() {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    setError("");
  };

  // 로그인 버튼 클릭 시 실행되는 함수
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      const response = await axios.post("/api/users/login", {
        email: formData.email,
        password: formData.password,
      });

      // 백엔드에서 success라는 이름의 jwtDto를 받음
      const jwtDto = response.data.success;
      if (jwtDto && jwtDto.isLogin) {
        // 로그인 성공: accessToken을 localStorage에 저장
        localStorage.setItem("accessToken", jwtDto.accessToken);
        // 로그인 성공 후 메인 페이지로 이동 (혹은 원하는 경로)
        alert("로그인에 성공하였습니다.")
        navigate("/");
      } else {
        // 로그인 실패: 에러 메시지 표시
        setError("이메일 또는 비밀번호가 올바르지 않습니다.");
      }
    } catch (err) {
      // 네트워크 에러 또는 기타 에러 처리
      setError("로그인 중 오류가 발생했습니다. 다시 시도해주세요.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleKakaoLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/kakao";
  };

  const handleNaverLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/naver";
  };

  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  // 비밀번호 찾기 버튼 클릭 시
  const handleFindPassword = () => {
    navigate("/users/find-password");
  };

  // 아이콘 SVG 컴포넌트
  const KakaoIcon = () => (
    <svg width="32" height="32" viewBox="0 0 40 40" fill="none">
      <ellipse cx="20" cy="20" rx="20" ry="20" fill="#FEE500"/>
      <ellipse cx="20" cy="20" rx="16" ry="13" fill="#FEE500"/>
      <ellipse cx="20" cy="20" rx="16" ry="13" fill="#FEE500"/>
      {/* 가운데 정렬 및 살짝 줄인 말풍선, 아래로 3px 이동 */}
      <g transform="translate(11.5,12)">
        <path d="M9 1C4.03 1 0 4.186 0 8.118c0 2.558 1.706 4.8 4.269 6.055-.189.702-.682 2.546-.78 2.94-.123.49.178.484.377.353.155-.104 2.466-1.676 3.463-2.355.543.08 1.1.123 1.671.123 4.97 0 9-3.186 9-7.118C18 4.186 13.97 1 9 1z" fill="#371C1D" />
      </g>
    </svg>
  );
  const NaverIcon = () => (
    <svg width="32" height="32" viewBox="0 0 40 40" fill="none">
      <circle cx="20" cy="20" r="20" fill="#03C75A"/>
      {/* 네이버 로고를 카카오와 동일하게 가운데 정렬 및 아래로 3px 이동 */}
      <g transform="translate(11,12)">
        {/* 네이버 N 로고 (정사각형 18x16 내에 배치) */}
        <path d="M2 1h4.5l5.5 7.5V1H16v14h-4.5L6 7.5V15H2V1z" fill="#fff"/>
      </g>
    </svg>
  );
  const GoogleIcon = () => (
    <svg width="32" height="32" viewBox="0 0 40 40" fill="none">
      {/* 흰 원형 배경 */}
      <circle cx="20" cy="20" r="20" fill="#fff"/>
      {/* 구글 로고를 정확히 원형 중앙에 위치하도록 조정 */}
      <g transform="translate(9,9) scale(0.45)">
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

  // 소셜 버튼 스타일
  const socialBtnStyle = {
    width: "44px",
    height: "44px",
    borderRadius: "50%",
    border: "none",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    margin: "0 8px",
    boxShadow: "0 1px 4px rgba(0,0,0,0.08)",
    cursor: "pointer",
    background: "#fff",
    padding: 0,
    transition: "box-shadow 0.2s",
  };

  
  return (
    <div className="signup-page-container">
      <Header />
      <main className="signup-main">
        <div className="signup-form-wrapper">
          <h1 className="signup-title">로그인</h1>
          <form onSubmit={handleSubmit} className="signup-form" noValidate>
            <div className="form-group">
              <label htmlFor="email">이메일</label>
              <input
                type="email"
                id="email"
                name="email"
                className="form-input"
                value={formData.email}
                onChange={handleChange}
                placeholder="이메일을 입력해주세요"
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="password">비밀번호</label>
              <input
                type="password"
                id="password"
                name="password"
                className="form-input"
                value={formData.password}
                onChange={handleChange}
                placeholder="비밀번호를 입력해주세요"
                required
              />
            </div>
            <div >
              <p className="error-message" style={{ marginTop: "8px" }}>
                {error && (error)}
              </p>  
            </div>
            
            <button
              type="submit"
              className="submit-btn"
              disabled={isLoading}
              style={{ marginTop: "16px" }}
            >
              로그인
            </button>
            {/* 비밀번호 찾기 버튼 추가 */}
            <button
              type="button"
              onClick={handleFindPassword}
              className="password-btn"
            >
              비밀번호 찾기
            </button>
          </form>
          <div className="divider" style={{ margin: "24px 0 16px 0" }}>
            <span>또는</span>
          </div>
          {/* 소셜로그인 버튼: '또는' 밑에, 일렬로 정렬, 원형, 아이콘만 */}
          <div
            className="social-buttons"
            style={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "center",
              alignItems: "center",
              margin: "16px 0 24px 0",
              gap: "8px",
            }}
          >
            <button
              type="button"
              style={{
                ...socialBtnStyle,
                background: "#FEE500",
                border: "none",
              }}
              aria-label="카카오로 로그인"
              onClick={handleKakaoLogin}
            >
              <KakaoIcon />
            </button>
            <button
              type="button"
              style={{
                ...socialBtnStyle,
                background: "#03C75A",
                border: "none",
              }}
              aria-label="네이버로 로그인"
              onClick={handleNaverLogin}
            >
              <NaverIcon />
            </button>
            <button
              type="button"
              style={{
                ...socialBtnStyle,
                background: "#fff",
                border: "1px solid #eee",
              }}
              aria-label="구글로 로그인"
              onClick={handleGoogleLogin}
            >
              <GoogleIcon />
            </button>
          </div>
          <div className="login-link">
            <span>아직 계정이 없으신가요?</span>
            <a href="/users/signup">회원가입</a>
          </div>
        </div>
      </main>
    </div>
  );
}

export default Login;