import React, { useState, useEffect } from "react";
import "../../styles/reset.css"
import "../../styles/global.css"
import { UserSimpleProfile } from "../../types/User";
import { useNavigate, Link} from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import axios from "axios";
import { SimpleBook } from "../../types/Book";
import BookList from "../../components/BookList";
import Sidebar from "../../components/MyPageSidebar";
import "./MyPage.css"


function MyPage() {
    const [userInfo, setUserInfo] = useState<UserSimpleProfile>({
        id: 0,
        nickname: "",
        pointBalance: 0
    });

    const {logout} = useAuth();

    const navigate = useNavigate();

    useEffect(() => {
        async function fetchData() {
            try {
                const response = await axios.get<any>("/api/users/me", {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                    },
                    withCredentials: true
                });

                const simpleProfile: UserSimpleProfile = {
                    id: response.data.id,
                    nickname: response.data.nickname,
                    pointBalance: response.data.pointBalance
                };

                setUserInfo(simpleProfile);
                console.log(userInfo.nickname);
            } catch (err) {
                alert("마이페이지 정보를 불러오지 못했습니다.");
                return;
            }
        }

        fetchData();
    }, []);

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


    const dummyBooks: SimpleBook[] = [
        {
            id: 1,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/1/webnovel1.png',
            title: '임시 작품 제목 1',
            author: '작가A'
        },
        {
            id: 2,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/2/webnovel2.png',
            title: '임시 작품 제목 2',
            author: '작가B'
        },
        {
            id: 3,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/3/webnovel3.png',
            title: '임시 작품 제목 3',
            author: '작가C'
        },
        {
            id: 4,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/4/webnovel4.png',
            title: '임시 작품 제목 4',
            author: '작가D'
        },
        {
            id: 5,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/5/webnovel5.png',
            title: '임시 작품 제목 5',
            author: '작가E'
        },
        {
            id: 6,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/6/webnovel6.png',
            title: '임시 작품 제목 6',
            author: '작가E'
        },
        {
            id: 7,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/7/webnovel7.png',
            title: '임시 작품 제목 7',
            author: '작가A'
        },
        {
            id: 8,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/8/webnovel8.png',
            title: '임시 작품 제목 8',
            author: '작가B'
        },
        {
            id: 9,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/9/webnovel9.png',
            title: '임시 작품 제목 9',
            author: '작가C'
        },
        {
            id: 10,
            coverUrl: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/10/webnovel10.png',
            title: '임시 작품 제목 10',
            author: '작가D'
        }
    ]

    const PointIcon = () => (
        <svg width="26" height="26" viewBox="0 0 26 26" fill="none">
            <circle cx="13" cy="13" r="12" fill="none" stroke="#444" strokeWidth="1.7"/>
            <text
                x="14"
                y="18"
                textAnchor="middle"
                fontFamily="Arial, sans-serif"
                fontWeight="bold"
                fontSize="15"
                fill="#444"
            >
                P
            </text>
        </svg>
    )

    const CouponIcon = () => (
        <svg width="35" height="32" viewBox="0 0 35 32" fill="none">
            <rect x="6.5" y="7" width="22" height="18" rx="3" fill="#fff" stroke="#444" strokeWidth="1.7"/>
            <circle cx="6.5" cy="16" r="2" fill="#fff" stroke="#444" strokeWidth="1.2"/>
            <circle cx="28.5" cy="16" r="2" fill="#fff" stroke="#444" strokeWidth="1.2"/>
            <line x1="11.5" y1="16" x2="23.5" y2="16" stroke="#888" strokeWidth="1" strokeDasharray="2,2"/>
        </svg>
    )

    const BookIcon = () => (
        <svg width="26" height="26" viewBox="0 0 26 26" fill="none">
            <rect x="1" y="3" width="7" height="20" stroke="#444" strokeWidth="1.5" fill="none"/>
            <rect x="3.2" y="11" width="2.6" height="12" fill="#444"/>
            <rect x="9.5" y="6" width="7" height="17" stroke="#444" strokeWidth="1.5" fill="none"/>
            <rect x="11.7" y="14" width="2.6" height="9" fill="#444"/>
            <rect x="18" y="3" width="7" height="20" stroke="#444" strokeWidth="1.5" fill="none"/>
            <rect x="20.2" y="11" width="2.6" height="12" fill="#444"/>
        </svg>
    )

    return (
        <div className="main-container">
            <main className="sidebar-main">
                <Sidebar />
                <div className="sidebar-right-wrap">
                    <div className="mypage-summary-container">
                        <div className="mypage-summary-left">
                            <div className="mypage-nickname">{userInfo?.nickname}</div>
                            <Link to={"#logout"} className="mypage-logout-link" onClick={handleLogoutClick}>로그아웃</Link>
                        </div>
                        <div className="mypage-summary-right">
                            <div className="mypage-summary-row">
                                <div className="mypage-summary-item">
                                    <div className="icon">
                                        <PointIcon />
                                    </div>
                                    <div className="label">내 포인트</div>
                                    <div className="value-wrap">
                                        <div className="value">{userInfo?.pointBalance}</div>
                                        <span className="mypage-value-unit">P</span>
                                    </div>
                                    <div className="desc">
                                        <Link to={"#charge"}>충전하기</Link>
                                    </div>
                                </div>
                                <div className="mypage-summary-item">
                                    <div className="icon coupon">
                                        <CouponIcon />
                                    </div>
                                    <div className="label">쿠폰</div>
                                    <div className="value-wrap">
                                        <div className="value">0</div>
                                        <span className="mypage-value-unit">개</span>
                                    </div>
                                </div>
                                <div className="mypage-summary-item">
                                    <div className="icon">
                                        <BookIcon />
                                    </div>
                                    <div className="label">내가 읽은 작품</div>
                                    <div className="value-wrap">
                                        <div className="value">0</div>
                                        <span className="mypage-value-unit">개</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <section className="book-section-list">
                        <div className="book-section-title">
                            <span className="book-section-title-text">오늘 업데이트된 작품</span>
                            <span className="book-section-title-line"></span>
                            <Link to={"#favorite-book"} className="book-section-viewall-link">전체보기</Link>
                        </div>
                        <BookList simpleBooks={dummyBooks} home={false}></BookList>
                    </section>
                </div>
            </main>

        </div>
    ) 
}

export default MyPage;