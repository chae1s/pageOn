import React, { useState } from "react";
import styled from "styled-components";
import * as U from "./Users.styles"
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import { LoginRequest } from "../../types/User";
import { useNavigate, Link} from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import axios from "axios";


// styled 코드
const FindPasswordLink = styled(Link)`
margin: 16px 0 0 0;
background: none;
border: none;
cursor: pointer;
font-size: 15px;
display: block;
padding: 0;
align-self: flex-end;
color: #666;
`

const DividerWrapper = styled.div`
  margin: 20px 0 20px;
  display: flex;
  gap: 20px;
`

const Divider = styled.div`
  width: 130px;
  height: 8px;
  border-bottom: 1px solid #e5e5e5;
`

const SocialBtnGroup = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  margin: 16px 0 24px 0;
  gap: 8px;
`

const SocialBtnItem = styled.button`
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  margin: 0 8px;
  cursor: pointer;
  background-color: #fff;
  padding: 0;
`

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
            setError("이메일 또는 비밀번호가 올바르지 않습니다.");
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
        <MainContainer>
            <NoSidebarMain>
                <U.UsersFormWrapper>
                    <U.UsersTitle>로그인</U.UsersTitle>
                    <U.UsersForm onSubmit={handleSubmit}>
                        <U.UsersFormGroup>
                            <U.UsersFormLabel htmlFor="email">이메일</U.UsersFormLabel>
                            <U.UsersFormInput 
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                placeholder="이메일을 입력해주세요."
                                onChange={handleChange}
                            />
                        </U.UsersFormGroup>
                        <U.UsersFormGroup>
                            <U.UsersFormLabel htmlFor="password">비밀번호</U.UsersFormLabel>
                            <U.UsersFormInput 
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                placeholder="비밀번호를 입력해주세요."
                                onChange={handleChange}
                            />
                        </U.UsersFormGroup>
                        <div>
                            <U.ErrorMessage style={{marginTop: "8px"}}>
                                {error && (error)}
                            </U.ErrorMessage>
                        </div>
                        <U.SubmitBtn type="submit">
                            로그인
                        </U.SubmitBtn>
                        <FindPasswordLink to={"/users/find-password"}>비밀번호 찾기</FindPasswordLink>
                    </U.UsersForm>
                    <DividerWrapper>
                        <Divider></Divider>
                        <span>또는</span>
                        <Divider></Divider>
                    </DividerWrapper>
                    <SocialBtnGroup>
                        <SocialBtnItem 
                            type="button" 
                            onClick={handleKakaoLogin}
                            style={{background: "#FEE500"}}
                        >
                            <KakaoIcon />
                        </SocialBtnItem>
                        <SocialBtnItem 
                            type="button" 
                            onClick={handleNaverLogin}
                            style={{background: "#03C75A"}}
                        >
                            <NaverIcon />
                        </SocialBtnItem>
                        <SocialBtnItem
                            type="button" 
                            onClick={handleGoogleLogin}
                            style={{border: "1px solid #eee"}}
                        >
                            <GoogleIcon />
                        </SocialBtnItem>
                    </SocialBtnGroup>
                    <U.UsersLinkWrap>
                        <span>아직 계정이 없으신가요?</span>
                        <U.UsersLink to={"/users/signup"}>회원가입</U.UsersLink>
                    </U.UsersLinkWrap>
                </U.UsersFormWrapper>
            </NoSidebarMain>

        </MainContainer>
    )
}

export default Login