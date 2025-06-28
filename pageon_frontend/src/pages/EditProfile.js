import React, { useState, useEffect } from "react";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import "./MyPage.css";
import axios from "axios";

function EditProfile() {
  const [userInfo, setUserInfo] = useState(null);
  const [nickname, setNickname] = useState("");
  const [isCheckingNickname, setIsCheckingNickname] = useState(false);
  const [isNicknameDuplicate, setIsNicknameDuplicate] = useState(false);
  const [nicknameMsg, setNicknameMsg] = useState("");
  const [hasNicknameFocused, setHasNicknameFocused] = useState(false);
  const [password, setPassword] = useState("");
  const [passwordMsg, setPasswordMsg] = useState("");
  const [passwordConfirm, setPasswordConfirm] = useState("");
  const [passwordConfirmMsg, setPasswordConfirmMsg] = useState("");

  // 사용자 정보 가져오기
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get("/api/users/me", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
          },
        });
        setUserInfo(response.data);
        setNickname(response.data.nickname || "");
      } catch (error) {
        alert("사용자 정보를 불러오지 못했습니다.");
      }
    };
    fetchUserInfo();
  }, []);

  // 닉네임 중복확인
  const checkNicknameDuplicate = async () => {
    if (!nickname) {
      setNicknameMsg("닉네임을 입력해주세요.");
      return;
    }
    setIsCheckingNickname(true);
    try {
      const response = await axios.get(`/api/users/check-nickname?nickname=${encodeURIComponent(nickname)}`);
      if (response.data.isNicknameDuplicate) {
        setIsNicknameDuplicate(true);
        setNicknameMsg("이미 사용 중인 닉네임입니다.");
      } else {
        setIsNicknameDuplicate(false);
        setNicknameMsg("사용 가능한 닉네임입니다.");
      }
    } catch (error) {
      setIsNicknameDuplicate(true);
      setNicknameMsg("닉네임 중복확인 중 오류가 발생했습니다.");
    } finally {
      setIsCheckingNickname(false);
    }
  };

  // 닉네임 변경 핸들러
  const handleNicknameChange = (e) => {
    const newNickname = e.target.value;
    setNickname(newNickname);
    
    // 닉네임이 비어있거나 원래 값과 같으면 메시지 초기화
    if (!newNickname || newNickname === userInfo?.nickname) {
      setNicknameMsg("");
    } else {
      // 닉네임이 변경되면 메시지 초기화
      setNicknameMsg("");
    }
  };

  // 닉네임 blur 핸들러
  const handleNicknameBlur = () => {
    if (hasNicknameFocused && nickname && nickname !== userInfo?.nickname) {
      checkNicknameDuplicate();
    }
  };

  // 닉네임 focus 핸들러
  const handleNicknameFocus = () => {
    setHasNicknameFocused(true);
  };

  // 비밀번호 유효성 검사
  const validatePassword = (pw) => {
    if (!pw) return "";
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
    // 비밀번호 변경 시 확인값도 다시 체크
    setPasswordConfirmMsg(value && passwordConfirm && value !== passwordConfirm ? "비밀번호가 일치하지 않습니다." : "");
  };

  const handlePasswordConfirmChange = (e) => {
    const value = e.target.value;
    setPasswordConfirm(value);
    setPasswordConfirmMsg(password && value !== password ? "비밀번호가 일치하지 않습니다." : "");
  };

  // 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const updateData = {};
      if (nickname !== userInfo?.nickname) {
        updateData.nickname = nickname;
      }
      if (password) {
        updateData.password = password;
      }
      const response = await axios.patch("/api/users/me", updateData, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        },
      });

      if (response.status === 200) {
        alert("내 정보가 수정되었습니다.");
        // 수정된 정보로 userInfo 업데이트
        setUserInfo(prev => ({ ...prev, ...updateData }));
        // 비밀번호 필드 초기화
        setPassword("");
        setPasswordConfirm("");
        setPasswordMsg("");
        setPasswordConfirmMsg("");
      }
    } catch (error) {
      alert("정보 수정 중 오류가 발생했습니다.");
    }
  };

  return (
    <>
      <Header />
      <div className="mypage-container">
        <Sidebar active="edit" />
        <div className="mypage-main">
          <div className="edit-profile-container">
            <h2 className="edit-profile-title">내 정보 수정</h2>
            <form className="edit-profile-form" onSubmit={handleSubmit}>
              {/* 이메일 */}
              <div className="edit-profile-row">
                <label className="edit-profile-label">이메일</label>
                <span className="edit-profile-value">{userInfo?.email}</span>
                <button type="button" className="edit-profile-auth-btn">본인인증 하기</button>
              </div>
              {/* 비밀번호 */}
              <div className="edit-profile-row">
                <label className="edit-profile-label">비밀번호</label>
                <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                  <input
                    type="password"
                    className={`edit-profile-input ${passwordMsg ? 'input-error' : (password ? 'input-success' : '')}`}
                    value={password}
                    onChange={handlePasswordChange}
                    autoComplete="new-password"
                  />
                  {passwordMsg && <div className="edit-profile-msg" style={{ color: 'var(--error-color)' }}>{passwordMsg}</div>}
                </div>
              </div>
              {/* 비밀번호 확인 */}
              <div className="edit-profile-row">
                <label className="edit-profile-label">비밀번호 확인</label>
                <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                  <input
                    type="password"
                    className={`edit-profile-input ${passwordConfirmMsg ? 'input-error' : (passwordConfirm ? 'input-success' : '')}`}
                    value={passwordConfirm}
                    onChange={handlePasswordConfirmChange}
                    autoComplete="new-password"
                  />
                  {passwordConfirmMsg && <div className="edit-profile-msg" style={{ color: 'var(--error-color)' }}>{passwordConfirmMsg}</div>}
                </div>
              </div>
              {/* 닉네임 */}
              <div className="edit-profile-row">
                <label className="edit-profile-label">닉네임</label>
                <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                  <input
                    type="text"
                    className={`edit-profile-input ${nickname ? (isNicknameDuplicate ? 'input-error' : 'input-success') : ''}`}
                    value={nickname}
                    onChange={handleNicknameChange}
                    onBlur={handleNicknameBlur}
                    onFocus={handleNicknameFocus}
                    maxLength={16}
                  />
                  {nicknameMsg && <div className="edit-profile-msg" style={{ color: isNicknameDuplicate ? 'var(--error-color)' : '#2563eb' }}>{nicknameMsg}</div>}
                </div>
              </div>
              {/* 생년월일 */}
              <div className="edit-profile-row">
                <label className="edit-profile-label">생년월일</label>
                <span className="edit-profile-value">{userInfo?.birthDate}</span>
              </div>
              <button type="submit" className="edit-profile-submit-btn">수정하기</button>
            </form>
          </div>
        </div>
      </div>
    </>
  );
}

export default EditProfile; 