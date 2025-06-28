import React, { useState } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import "./MyPage.css";
import axios from "axios";

function Withdraw() {
  const [password, setPassword] = useState("");
  const [passwordMsg, setPasswordMsg] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [withdrawReason, setWithdrawReason] = useState("");
  const [otherReason, setOtherReason] = useState("");

  // 탈퇴 이유 옵션들
  const withdrawReasons = [
    "서비스 이용 빈도가 낮음",
    "다른 서비스를 이용하게 됨",
    "개인정보 보호 우려",
    "서비스 품질에 불만족",
    "계정 보안 문제",
    "기타"
  ];

  // 비밀번호 유효성 검사
  const validatePassword = (pw) => {
    if (!pw) return "비밀번호를 입력해주세요.";
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\-?$%&^])[a-zA-Z0-9!@#\-?$%&^]{8,}$/;
    if (!passwordRegex.test(pw)) {
      return "비밀번호는 8자 이상, 영문, 숫자, 특수문자(!@-#$%&^)를 모두 포함해야 합니다.";
    }
    return "";
  };

  const handlePasswordChange = (e) => {
    const value = e.target.value;
    setPassword(value);
    setPasswordMsg(validatePassword(value));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const validationMsg = validatePassword(password);
    if (validationMsg) {
      setPasswordMsg(validationMsg);
      return;
    }

    if (!withdrawReason) {
      alert("탈퇴 이유를 선택해주세요.");
      return;
    }

    if (withdrawReason === "기타" && !otherReason.trim()) {
      alert("기타 이유를 입력해주세요.");
      return;
    }

    // 사용자 확인
    const isConfirmed = window.confirm(
      "정말로 회원탈퇴를 하시겠습니까?\n탈퇴 후에는 모든 데이터가 삭제되며 복구할 수 없습니다."
    );

    if (!isConfirmed) return;

    setIsSubmitting(true);
    try {
      // 실제 API 호출 (예시)
      const finalReason = withdrawReason === "기타" ? otherReason : withdrawReason;
      await axios.post("/api/users/withdraw", { 
        password,
        withdrawReason: finalReason
      });
      alert("회원탈퇴가 완료되었습니다.");
      // 로그인 페이지로 이동
      window.location.href = "/login";
    } catch (error) {
      if (error.response?.status === 401) {
        setPasswordMsg("비밀번호가 일치하지 않습니다.");
      } else {
        alert("회원탈퇴 중 오류가 발생했습니다. 다시 시도해주세요.");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <>
      <Header />
      <div className="mypage-container">
        <Sidebar active="withdraw" />
        <div className="mypage-main">
          <div className="withdraw-container">
            <h2 className="withdraw-title">회원탈퇴</h2>
            <div className="withdraw-warning">
              <p>회원탈퇴 시 다음 사항을 확인해주세요:</p>
              <ul>
                <li>• 모든 개인정보와 서비스 이용 기록이 삭제됩니다.</li>
                <li>• 보유한 포인트와 쿠폰이 모두 소멸됩니다.
                  <div className="withdraw-point-info">
                    <span>현재 내 잔여 포인트: </span>
                    <span className="withdraw-point-value">1,250P</span>
                  </div>
                </li>
                <li>• 탈퇴 후에는 복구할 수 없습니다.</li>
                <li>• 탈퇴를 원하시면 비밀번호를 입력해주세요.</li>
              </ul>
            </div>
            <div className="withdraw-reason-section">
              <p className="withdraw-reason-title">탈퇴하는 이유를 말해주세요</p>
              <div className="withdraw-reason-options">
                {withdrawReasons.map((reason, index) => (
                  <label key={index} className="withdraw-reason-option">
                    <input
                      type="radio"
                      name="withdrawReason"
                      value={reason}
                      checked={withdrawReason === reason}
                      onChange={(e) => setWithdrawReason(e.target.value)}
                    />
                    <span className="withdraw-reason-text">{reason}</span>
                    {reason === "기타" && (
                      <input
                        type="text"
                        className="withdraw-other-reason"
                        value={otherReason}
                        onChange={(e) => setOtherReason(e.target.value)}
                        placeholder="기타 이유를 입력해주세요"
                      />
                    )}
                  </label>
                ))}
              </div>
            </div>
            <form className="withdraw-form" onSubmit={handleSubmit}>
              <div className="withdraw-row">
                <label className="withdraw-label">비밀번호</label>
                <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                  <input
                    type="password"
                    className={`withdraw-input ${passwordMsg ? 'input-error' : (password ? 'input-success' : '')}`}
                    value={password}
                    onChange={handlePasswordChange}
                    placeholder="비밀번호를 입력해주세요"
                    autoComplete="current-password"
                  />
                  {passwordMsg && <div className="withdraw-msg" style={{ color: 'var(--error-color)' }}>{passwordMsg}</div>}
                </div>
              </div>
              <div className="withdraw-buttons">
                <button 
                  type="submit" 
                  className="withdraw-submit-btn"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? "처리 중..." : "탈퇴하기"}
                </button>
                <button 
                  type="button" 
                  className="withdraw-cancel-btn"
                  onClick={() => window.location.href = "/users/my-page"}
                >
                  취소
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  );
}

export default Withdraw; 