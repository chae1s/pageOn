import React, { useEffect } from "react";
import { Link, useNavigate, NavLink} from "react-router-dom";
import "../styles/reset.css";
import "../styles/global.css";
import "./Sidebar.css"
import { useAuth } from "../context/AuthContext";


function Sidebar() {
    const navigate = useNavigate();

    const handleEditClick = async (e: React.MouseEvent<HTMLAnchorElement | HTMLButtonElement>) => {
        e.preventDefault();

        const provider = localStorage.getItem("provider");
        console.log(provider)
        if (provider && provider === "EMAIL") {
            navigate("/users/check-password");
        } else if (provider) {
            sessionStorage.setItem("passwordVerified", "true");
            navigate("/users/edit");
        }
    }
    const {roles} = useAuth();
    console.log(roles)

    return (
        <div className="sidebar-container">
            <Link to={"/users/my-page"} className="sidebar-main-link">마이페이지</Link>
            <aside>
                <nav className="sidebar-nav">
                    <ul>
                        <li>
                            <div>책</div>
                            <ul>
                                <li>
                                    <NavLink to={"/library/favorites"} className="sidebar-nav-link">관심 작품</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/recent-view"} className="sidebar-nav-link">최근 조회 작품</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/my-comments"} className="sidebar-nav-link">내가 쓴 댓글</NavLink>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <div>내 정보</div>
                            <ul>
                                <li>
                                    <NavLink to={"/users/edit"} onClick={handleEditClick} className="sidebar-nav-link">내 정보 수정</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/users/withdraw"} className="sidebar-nav-link">회원탈퇴</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/my-comments"} className="sidebar-nav-link">1:1 문의</NavLink>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <div>구매</div>
                            <ul>
                                <li>
                                    <NavLink to={"/library/favorites"} className="sidebar-nav-link">내 캐시 내역</NavLink>
                                </li>
                                <li>
                                    <NavLink to={"/library/recent-view"} className="sidebar-nav-link">이용권 내역</NavLink>
                                </li>
                            </ul>
                        </li>
                    </ul>
                    <div className="sidebar-author-link">
                        {roles.includes("ROLE_CREATOR") ? (
                            <Link to={"/creators/dashboard"}>작가 페이지로 이동하기</Link>
                        ) : (
                            <Link to={"/creators/register"}>작가 등록하기</Link>
                        )}
                    </div>
                </nav>
            </aside>
        </div>
    )


}

export default Sidebar