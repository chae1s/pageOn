import React, { useState } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import "./MyPage.css";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

function PasswordCheck() {
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [resetEmail, setResetEmail] = useState("");
  const [resetMsg, setResetMsg] = useState("");
  const location = useLocation();
  const navigate = useNavigate();
  const params = new URLSearchParams(location.search);
  const next = params.get("next");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!password) {
      setError("비밀번호를 입력하세요.");
      return;
    }
    
    try {
      const response = await axios.post("/api/users/check-password", {
        password: password
      }, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        }
      });
      
      if (response.data.isCorrect) {
        if (next === "edit") {
          navigate("/users/edit");
        } else if (next === "withdraw") {
          navigate("/users/withdraw");
        } else {
          alert("비밀번호 확인 완료!");
        }
      } else {
        setError("비밀번호가 일치하지 않습니다.");
      }
    } catch (error) {
      setError("비밀번호 확인 중 오류가 발생했습니다.");
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
          </div>
        </div>
      </div>
    </>
  );
}

export default PasswordCheck; 