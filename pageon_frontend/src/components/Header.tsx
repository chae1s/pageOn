import React, {useState, useEffect} from "react";
import { Link, NavLink} from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../styles/reset.css";
import "../styles/global.css";
import "./Header.css"
import logo from '../assets/icon.png'


function Header() {
    const {isAuthenticated} = useAuth();

    
    return (
        <div className="header-container">
            <header className="header">
                <div className="header-logo-search-user">
                    <Link to={"/"} className="logo">
                        <img src={ logo }/>
                    </Link>
                    <form role="search" className="book-search-form">
                        <div className="book-search-input-wrap-1">
                            <div className="book-search-input-wrap-2">
                                <label className="book-search-label">
                                    <svg width="18" height="18" viewBox="0 0 20 20" fill="none"  className="search-icon">
                                        <circle cx="9" cy="9" r="7" stroke="#888" strokeWidth="2"/>
                                        <line x1="14.2" y1="14.2" x2="20" y2="20" stroke="#888" strokeWidth="2" strokeLinecap="round"/>
                                    </svg>
                                    <span className="book-search-span">인스턴트 검색</span>
                                    <input type="text" maxLength={64} className="book-search-input" placeholder="제목, 작가를 입력하세요."/>
                                </label>
                            </div>
                        </div>
                    </form>
                    <div className="user-link">
                        <div className="user-link-list">
                            {isAuthenticated ? (
                                <>
                                    <div className="user-link-item">
                                        <Link to={"/users/my-library"}>내서재</Link>
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
                        <NavLink to={"/"} className="nav-link first-link">추천</NavLink>
                        <NavLink to={"/webtoons"} className="nav-link">웹툰</NavLink>   
                        <NavLink to={"/webnovels"} className="nav-link">웹소설</NavLink>
                    </div>
                    <div className="header-etc-link-list">
                        <Link to={"/"}>
                            <span>이벤트</span>
                        </Link>
                        <Link to={"/"}>
                            <span>알림</span>
                        </Link>
                        <Link to={"/"}>
                            <span>포인트충전</span>
                        </Link>
                    </div>
                </div>
            </header>
        </div>
    )

}

export default Header;