import React, { useState } from "react";
import styled from "styled-components";
import * as U from "./Users.styles"
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import "./Users.css"
import { useNavigate, Link} from "react-router-dom";
import axios from "axios";

const SuccessMessage = styled.p`
    margin-top: 8px;
    color: #528efa;
`

function PasswordFind() {
    const [email, setEmail] = useState<string>("");
    const navigate = useNavigate();
    const [error, setError] = useState<string>("");
    const [successMsg, setSuccessMsg] = useState<string>("");

    const handleChange = (e:React.ChangeEvent<HTMLInputElement>) => {
        setEmail(e.target.value);
        setError("");
        setSuccessMsg("");
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        setError("");
        setSuccessMsg("");

        try {
            const response = await axios.post("/api/users/find-password", {
                email: email
            });
    
            const {type} = response.data;
            const {message} = response.data;
    
            if (type === "email") {
                setSuccessMsg(message);
            } else if (type === "social") {
                setError(message);
            } else if (type === "noUser") {
                setError(message);
            } else {
                setError("비밀번호 찾기에 실패하였습니다. 다시 시도해주세요.");
            }
        } catch (err) {
            setError("비밀번호 찾기에 실패하였습니다. 다시 시도해주세요.");
        }
    }

    return(
        <MainContainer>
            <NoSidebarMain>
                <U.UsersFormWrapper>
                    <U.UsersTitle>비밀번호 찾기</U.UsersTitle>
                    <U.UsersForm onSubmit={handleSubmit}>
                        <U.UsersFormGroup>
                            <U.UsersFormLabel htmlFor="email">이메일</U.UsersFormLabel>
                            <U.UsersFormInput 
                                type="email"
                                id="email"
                                name="email"
                                value={email}
                                placeholder="이메일을 입력해주세요."
                                onChange={handleChange}
                                required
                            />
                        </U.UsersFormGroup>
                        <div>
                            {error && (
                                <U.ErrorMessage style={{ marginTop: "8px" }}>
                                    {error}
                                </U.ErrorMessage>
                            )}
                            {successMsg && (
                                <SuccessMessage>
                                    {successMsg}
                                </SuccessMessage>
                            )}
                        </div>
                        <U.SubmitBtn type="submit"  style={{marginTop: "16px"}}>
                            비밀번호 찾기
                        </U.SubmitBtn>
                    </U.UsersForm>
                    <U.UsersLinkWrap style={{marginTop: "24px"}}>
                        <U.UsersLink to={"/users/login"}>로그인으로 돌아가기</U.UsersLink>
                    </U.UsersLinkWrap>
                </U.UsersFormWrapper>
            </NoSidebarMain>

        </MainContainer>
    ) 
}

export default PasswordFind