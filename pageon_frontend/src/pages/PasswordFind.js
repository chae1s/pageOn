import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import "../styles/reset.css";
import "../styles/global.css";
import "./Signup.css";
import axios from "axios";

function PasswordFind() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setEmail(e.target.value);
    setError("");
    setSuccessMsg("");
  };

  // 비밀번호 찾기 버튼을 누르면 'api/users/find-password'로 입력한 이메일을 보냄
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccessMsg("");
    setIsLoading(true);

    try {
      // 입력한 이메일을 파라미터로 전송
      const response = await axios.post("/api/users/find-password", { email: email });
      // type: "email" | "social" | "noUser"
      const { type } = response.data;
      const {message} = response.data;
      console.log(message)
    
      if (type === "email") {
        setSuccessMsg(message);
      } else if (type === "social") {
        setError(message);
      } else if (type === "noUser") {
        setError(message);
      } else {
        setError("비밀번호 찾기에 실패하였습니다. 다시 시도해주세요")
      }
    } catch (err) {
      setError(
        err.response && err.response.data && err.response.data.message
          ? err.response.data.message
          : "비밀번호 찾기에 실패하였습니다. 다시 시도해주세요"
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="signup-page-container">
      <Header />
      <main className="signup-main">
        <div className="signup-form-wrapper">
          <h1 className="signup-title">비밀번호 찾기</h1>
          <form onSubmit={handleSubmit} className="signup-form" noValidate>
            <div className="form-group">
              <label htmlFor="email">이메일</label>
              <input
                type="email"
                id="email"
                name="email"
                className="form-input"
                value={email}
                onChange={handleChange}
                placeholder="이메일을 입력해주세요"
                required
              />
            </div>
            <div>
              {error && (
                <p className="error-message" style={{ marginTop: "8px" }}>
                  {error}
                </p>
              )}
              {successMsg && (
                <p className="success-message" style={{ marginTop: "8px", color: "#528efa" }}>
                  {successMsg}
                </p>
              )}
            </div>
            <button
              type="submit"
              className="submit-btn"
              disabled={isLoading}
              style={{ marginTop: "16px" }}
            >
              {isLoading ? "처리 중..." : "비밀번호 찾기"}
            </button>
          </form>
          <div className="login-link" style={{ marginTop: "24px" }}>
            <a href="/users/login">로그인으로 돌아가기</a>
          </div>
        </div>
      </main>
    </div>
  );
}

export default PasswordFind;
