import React from "react";
import "./Header.css";
import "./Sidebar.css";

function Sidebar({ active }) {
  const handleMyPageClick = () => {
    window.location.href = "/users/my-page";
  };

  return (
    <div className="mypage-sidebar">
      {/* 사이드바 */}
      <h3 onClick={handleMyPageClick} style={{ cursor: 'pointer' }}>마이 페이지</h3>
      <aside>
        <nav>
          <ul>
            {/* 책 */}
            <li>
              <div>책</div>
              <ul>
                <li>
                  <a href="#favorite-books">관심 작품</a>
                </li>
                <li>
                  <a href="#recent-books">최근 조회 작품</a>
                </li>
                <li>
                  <a href="#my-comments">내가 쓴 댓글</a>
                </li>
              </ul>
            </li>
            {/* 내 정보 */}
            <li>
              <div>내 정보</div>
              <ul>
                <li>
                  <a href="/users/password-check?next=edit" className={active === "edit" ? "sidebar-link-active" : undefined}>내 정보 수정</a>
                </li>
                <li>
                  <a href="/users/password-check?next=withdraw">회원탈퇴</a>
                </li>
                <li>
                  <a href="#edit-profile">1:1 문의</a>
                </li>
              </ul>
            </li>
            {/* 구매 */}
            <li>
              <div>구매</div>
              <ul>
                <li>
                  <a href="#cash-history">내 캐시 내역</a>
                </li>
                <li>
                  <a href="#ticket-history">이용권 내역</a>
                </li>
              </ul>
            </li>
          </ul>
          <div className="mypage-sidebar-author-link" style={{ marginTop: '20px', paddingTop: '20px', borderTop: '1px solid #f0f0f0' }}>
            <a href="#author-registration" style={{ color: '#999', fontSize: '0.9em' }}>작가 등록하기</a>
          </div>
        </nav>
      </aside>
    </div>
  );
}

export default Sidebar; 