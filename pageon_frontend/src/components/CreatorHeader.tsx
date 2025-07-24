import React, {useState, useEffect} from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../styles/reset.css";
import "../styles/global.css";
import "./Header.css"
import logo from '../assets/icon.png'
import axios from "axios";


function CreatorHeader() {
    const {isAuthenticated, logout} = useAuth();
    const navigate = useNavigate();
    const handleLogoutClick = async (e:React.MouseEvent<HTMLAnchorElement | HTMLButtonElement>) => {
        e.preventDefault();

        if (!window.confirm("로그아웃 하시겠습니까?")) return;

        try {
            const response = await axios.get("/api/users/logout", {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                },
                withCredentials: true
            })

            if (response.status === 200) {
                logout();
                navigate("/", {replace: true});
            } else {
                alert("로그아웃에 실패했습니다.");
            }
        } catch (err) {
            alert("로그아웃 중 오류가 발생했습니다.");
        }
    };

    
    return (
        <div className="header-container">
            <header className="header">
                <div className="header-logo-search-user">
                    <Link to={"/creators/dashboard"} className="logo">
                        <img src={ logo }/>
                    </Link>
                    <div className="book-search-form creator-header-space">
                    </div>
                    <div className="user-link">
                        <div className="user-link-list">
                            {isAuthenticated ? (
                                <>
                                    <div className="user-link-item">
                                        <Link to={"#logout"} onClick={handleLogoutClick}>로그아웃</Link>
                                    </div>
                                    <div className="user-link-item last-item">
                                        <Link to={"/users/my-page"}>마이페이지</Link> 
                                    </div>
                                </>
                            ) : (
                                <>
                                    <div className="user-link-item">
                                        <Link to={"/users/signup"}>회원가입</Link>
                                    </div>
                                    <div className="user-link-item last-item">
                                        <Link to={"/users/login"}>로그인</Link> 
                                    </div>
                                </>
                            )}
                        </div>
                    </div>
                </div>
                <div className="header-contents-etc">
                    <div className="header-link-list">
                        <NavLink to={"/creators"} className="nav-link first-link">작가 센터</NavLink>
                        <NavLink to={"/webtoons"} className="nav-link">내 작품</NavLink>   
                        <NavLink to={"/webnovels"} className="nav-link">수익 관리</NavLink>
                    </div>
                    <div className="header-etc-link-list">
                        <Link to={"/"}>
                            <span>pageOn 홈</span>
                        </Link>
                        <Link to={"/"}>
                            <span>알림</span>
                        </Link>
                    </div>
                </div>
            </header>
        </div>
    )

}

export default CreatorHeader;