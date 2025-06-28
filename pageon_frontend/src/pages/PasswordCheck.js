import React, { useState } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import "./MyPage.css";
import { useLocation, useNavigate } from "react-router-dom";

function PasswordCheck() {
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [resetEmail, setResetEmail] = useState("");
  const [resetMsg, setResetMsg] = useState("");
  const location = useLocation();
  const navigate = useNavigate();
  const params = new URLSearchParams(location.search);
  const next = params.get("next");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!password) {
      setError("비밀번호를 입력하세요.");
      return;
    }
    // 실제 비밀번호 확인 로직은 API 연동 필요
    if (next === "edit") {
      navigate("/users/edit");
    } else if (next === "withdraw") {
      navigate("/users/withdraw");
    } else {
      alert("비밀번호 확인! (API 연동 필요)");
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setResetMsg("");
    if (!resetEmail) {
      setResetMsg("이메일을 입력하세요.");
      return;
    }
    try {
      const res = await fetch("/api/users/find-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: resetEmail })
      });
      if (res.ok) {
        setResetMsg("비밀번호 재설정 메일이 발송되었습니다.");
      } else {
        setResetMsg("이메일 전송에 실패했습니다.");
      }
    } catch {
      setResetMsg("이메일 전송 중 오류가 발생했습니다.");
    }
  };

  return (
    <>
      <Header />
      <div className="mypage-container">
        <Sidebar />
        <div className="mypage-main">
          <div className="password-check-container">
            <h2 className="password-check-title">비밀번호 재확인</h2>
            <form onSubmit={handleSubmit}>
              <label htmlFor="password" className="password-check-label">비밀번호</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                className="password-check-input"
                autoFocus
              />
              {error && <div className="password-check-error">{error}</div>}
              <button type="submit" className="password-check-btn">확인</button>
            </form>
            <div className="password-check-reset-box">
              <span className="password-check-reset-guide">비밀번호를 잊어버리셨나요?</span>
              <button
                type="button"
                className="password-check-reset-btn-simple"
                onClick={() => {
                  if (window.confirm("임시비밀번호를 발급하시겠습니까?")) {
                    window.location.href = "/api/users/find-password";
                  }
                }}
              >
                비밀번호 재설정
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default PasswordCheck; 