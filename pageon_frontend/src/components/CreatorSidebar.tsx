import React, {useState, useEffect} from "react";
import { Link, NavLink} from "react-router-dom";
import "../styles/reset.css";
import "../styles/global.css";
import "./Sidebar.css"

function CreatorSidebar() {

    return (
        <div className="sidebar-container">
            <Link to={"/creators/dashbord"} className="sidebar-main-link">대시 보드</Link>
            <aside>
                <nav className="sidebar-nav">
                    <ul>
                        <li>
                            <div>작품 관리</div>
                            <ul>
                                <li>
                                    <NavLink to={"/creators/works"} className="sidebar-nav-link">내 작품 목록</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/recent-view"} className="sidebar-nav-link">작품 등록</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/my-comments"} className="sidebar-nav-link">에피소드 관리</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/my-comments"} className="sidebar-nav-link">연재 일정 관리</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/my-comments"} className="sidebar-nav-link">작품 반응</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/my-comments"} className="sidebar-nav-link">작품별 통계</NavLink>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <div>수익 / 정산</div>
                            <ul>
                                <li>
                                    <NavLink to={"/users/edit"} className="sidebar-nav-link">수익 현황</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/users/withdraw"} className="sidebar-nav-link">정산 내역</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/my-comments"} className="sidebar-nav-link">계좌 정보 관리</NavLink>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <div>계정 관리</div>
                            <ul>
                                <li>
                                    <NavLink to={"/library/favorites"} className="sidebar-nav-link">내 프로필 수정</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/recent-view"} className="sidebar-nav-link">또 뭐가 있지</NavLink>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </nav>
            </aside>
        </div>
    )
}

export default CreatorSidebar;