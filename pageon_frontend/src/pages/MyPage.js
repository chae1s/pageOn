import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import "./MyPage.css";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import axios from "axios";

function MyPage() {
  const [userInfo, setUserInfo] = useState(null);
  const [library, setLibrary] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchData() {
      try {
        // 내 정보
        const userRes = await axios.get("/api/users/me", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
          },
        });
        setUserInfo(userRes.data);
      } catch (err) {
        alert("마이페이지 정보를 불러오지 못했습니다.");
        return;
      }

      try {
        // 내 서재
        const libraryRes = await axios.get("/api/users/library", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
          },
        });
        setLibrary(libraryRes.data.items || []);
      } catch (err) {
        console.log("서재 정보를 불러오지 못했습니다.");
      }
    }
    fetchData();
  }, []);

  const handleLogoutClick = async (e) => {
    e.preventDefault();
    if (!window.confirm("로그아웃 하시겠습니까?")) return;
    try {
      const response = await axios.get("/api/users/logout", {
        withCredentials: true,
      });
      if (response.status === 200) {
        localStorage.removeItem("accessToken");
        navigate("/", { replace: true });
      } else {
        alert("로그아웃에 실패했습니다.");
      }
    } catch (error) {
      alert("로그아웃 중 오류가 발생했습니다.");
    }
  };

  // 안전하게 pointBalance가 숫자인지 확인 후 toLocaleString 사용
  const renderPoint = () => {
    const pointBalance = userInfo?.pointBalance;
    if (typeof pointBalance === "number" && !isNaN(pointBalance)) {
      return pointBalance.toLocaleString();
    }
    return "0";
  };

  return (
    <>
      <Header />
      <div className="mypage-container">
        <Sidebar />
        {/* 메인 컨텐츠 */}
        <div className="mypage-main">
            
            {/* 상단 정보 (닉네임, 포인트) */}
            <div className="mypage-summary-container">
              <div className="mypage-summary-left">
                <div className="mypage-nickname">{userInfo?.nickname || "사용자"}님</div>
                <a href="#logout" className="mypage-logout-link" onClick={handleLogoutClick}>로그아웃</a>
              </div>
              <div className="mypage-summary-right">
                <div className="mypage-summary-row">
                  <div className="mypage-summary-item">
                    <div className="icon">P</div>
                    <div className="label">내 포인트</div>
                    <div className="value">{renderPoint()}<span className="mypage-value-unit">P</span></div>
                    <div className="desc"><a href="#charge" className="mypage-charge-link">충전하기</a></div>
                  </div>
                  <div className="mypage-summary-item">
                    <div className="icon">🎟️</div>
                    <div className="label">쿠폰</div>
                    <div className="value">0<span className="mypage-value-unit">개</span></div>
                  </div>
                  <div className="mypage-summary-item">
                    <div className="icon">📚</div>
                    <div className="label">내가 읽은 작품</div>
                    <div className="value">0<span className="mypage-value-unit">개</span></div>
                  </div>
                </div>
              </div>
            </div>

            {/* 오늘 업데이트된 작품 */}
            <section className="mypage-section">
                <div className="mypage-section-title">
                  <span className="mypage-section-title-text">오늘 업데이트된 작품</span>
                  <span className="mypage-section-title-line"></span>
                  <a href="#favorite-books" className="mypage-viewall-btn">전체보기</a>
                </div>
                <div className="mypage-today-works">
                  <div className="today-works-list">
                    {[1,2,3,4,5,6].map((i) => (
                      <div key={i} className="today-work-item">
                        <img
                          src={`https://via.placeholder.com/140x200?text=작품+${i}`}
                          alt={`오늘 업데이트된 작품 ${i}`}
                          className="today-work-img"
                        />
                        <div className="today-work-title">오늘 업데이트된 작품 제목 {i}</div>
                        <div className="today-work-author">작가명</div>
                      </div>
                    ))}
                  </div>
                </div>
            </section>
            
        </div>
      </div>
    </>
  );
}

export default MyPage;
