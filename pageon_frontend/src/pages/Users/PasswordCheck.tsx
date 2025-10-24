import React, {useState} from "react";
import styled from "styled-components";
import { MainContainer, SidebarMain } from "../../styles/Layout.styles";
import * as M from "./MyPage.styles"
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Sidebar from "../../components/Sidebars/MyPageSidebar";
import "./MyPage.css"


function PasswordCheck() {
    const [password, setPassword] = useState<string>("");
    const [error, setError] = useState<string>("");
    const navigate = useNavigate();

    const handleChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;

        setPassword(value);
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!password) {
            setError("비밀번호를 입력하세요.");
            return;
        }

        try {
            const response = await axios.post("/api/users/check-password", {password: password}, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                }
            });

            console.log(response.data)

            if (response.data.isCorrect) {
                sessionStorage.setItem("passwordVerified", "true");
                navigate("/users/edit");
            } else {
                setError("비밀번호가 일치하지 않습니다.");
            }
        } catch (err) {
            setError("비밀번호 확인 중 오류가 발생했습니다.");
        }
    };


    return (
        <MainContainer>
            <SidebarMain>
                <Sidebar />
                <M.SidebarRightWrap>
                    <M.MypageTitle>비밀번호 재확인</M.MypageTitle>
                    <M.PasswordCheckForm onSubmit={handleSubmit}>
                        <div>
                            <M.PasswordCheckLabel htmlFor="password">비밀번호</M.PasswordCheckLabel>
                            <M.PasswordCheckInput 
                                id="password"
                                type="password"
                                value={password}
                                onChange={handleChange}
                                autoFocus
                            />
                            {error && 
                                <M.ErrorMessage>{error}</M.ErrorMessage>
                            }
                            <M.SubmitBtn type="submit" >확인</M.SubmitBtn>
                        </div>
                    </M.PasswordCheckForm>
                </M.SidebarRightWrap>
            </SidebarMain>
        </MainContainer>
    )
}

export default PasswordCheck