import React, { useState } from "react";
import "../../styles/reset.css"
import "../../styles/global.css"
import "./Users.css"
import { LoginRequest } from "../../types/User";
import { useNavigate, Link} from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import axios from "axios";



function Login() {
    const {login} = useAuth();
    const [formData, setFormData] = useState<LoginRequest> ({
        email: "",
        password: ""
    });

    const [error, setError] = useState<string>("");
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
        setError("");
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError("");
        
        try {
            const response = await axios.post("/api/users/login", {
                email: formData.email,
                password: formData.password
            });

            const jwtInfo = response.data.success;
            console.log(jwtInfo)
            if(jwtInfo && jwtInfo.isLogin) {
                login(jwtInfo.accessToken, jwtInfo.userRoles, jwtInfo.oauthProvider);

                alert("로그인에 성공하였습니다.");
                navigate("/");
            } else {
                setError("이메일 또는 비밀번호가 올바르지 않습니다.");
            }
        } catch (err) {
            setError("로그인 중 오류가 발생했습니다. 다시 시도해주세요.");
        } 
    };

    const handleKakaoLogin = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/kakao";
    };

    const handleNaverLogin = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/naver";
    };

    const handleGoogleLogin = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
    };


    // 소셜 로그인 아이콘
    const KakaoIcon = () => (
        <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
            <ellipse cx="20" cy="20" rx="20" ry="20" fill="#FEE500"/>
            <ellipse cx="20" cy="20" rx="16" ry="13" fill="#FEE500"/>
            <ellipse cx="20" cy="20" rx="16" ry="13" fill="#FEE500"/>
            <g transform="translate(13.5,12)">
               <path d="M9 1C4.03 1 0 4.186 0 8.118c0 2.558 1.706 4.8 4.269 6.055-.189.702-.682 2.546-.78 2.94-.123.49.178.484.377.353.155-.104 2.466-1.676 3.463-2.355.543.08 1.1.123 1.671.123 4.97 0 9-3.186 9-7.118C18 4.186 13.97 1 9 1z" fill="#371C1D" />
            </g>
        </svg> 
    );

    const NaverIcon = () => (
        <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
           <circle cx="20" cy="20" r="20" fill="#03C75A"/>
            <g transform="translate(13,12)">
                <path d="M2 1h4.5l5.5 7.5V1H16v14h-4.5L6 7.5V15H2V1z" fill="#fff"/>
            </g>
        </svg>
    );
    
    const GoogleIcon = () => (
        <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
            <circle cx="20" cy="20" r="20" fill="#fff"/>
            <g transform="translate(11,9) scale(0.45)">
                <g>
                    <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"></path>
                    <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"></path>
                    <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"></path>
                    <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"></path>
                    <path fill="none" d="M0 0h48v48H0z"></path>
                </g>
            </g>
        </svg>
    );

    return (
        <div className="main-container">
            <main className="no-sidebar-main">
                <div className="users-form-wrapper">
                    <h1 className="users-title">로그인</h1>
                    <form className="users-form" onSubmit={handleSubmit}>
                        <div className="users-form-group">
                            <label htmlFor="email">이메일</label>
                            <input 
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                placeholder="이메일을 입력해주세요."
                                onChange={handleChange}
                            />
                        </div>
                        <div className="users-form-group">
                            <label htmlFor="password">비밀번호</label>
                            <input 
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                placeholder="비밀번호를 입력해주세요."
                                onChange={handleChange}
                            />
                        </div>
                        <div>
                            <p className="error-message" style={{marginTop: "8px"}}>
                                {error && (error)}
                            </p>
                        </div>
                        <button className="submit-btn" type="submit">
                            로그인
                        </button>
                        <Link to={"/users/find-password"} className="find-password-link">비밀번호 찾기</Link>
                    </form>
                    <div className="divider-wrap">
                        <div className="divider"></div>
                        <span>또는</span>
                        <div className="divider"></div>
                    </div>
                    <div className="social-btn-group">
                        <button 
                            type="button" 
                            className="social-btn-item" 
                            onClick={handleKakaoLogin}
                            style={{background: "#FEE500"}}
                        >
                            <KakaoIcon />
                        </button>
                        <button 
                            type="button" 
                            className="social-btn-item" 
                            onClick={handleNaverLogin}
                            style={{background: "#03C75A"}}
                        >
                            <NaverIcon />
                        </button>
                        <button 
                            type="button" 
                            className="social-btn-item" 
                            onClick={handleGoogleLogin}
                            style={{border: "1px solid #eee"}}
                        >
                            <GoogleIcon />
                        </button>
                    </div>
                    <div className="users-link">
                        <span>아직 계정이 없으신가요?</span>
                        <Link to={"/users/signup"}>회원가입</Link>
                    </div>
                </div>
            </main>

        </div>
    )
}

export default Login