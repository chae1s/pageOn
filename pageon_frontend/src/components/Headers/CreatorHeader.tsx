import React, {useState, useEffect} from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import styled from "styled-components";
import * as H from "../Styles/Header.styles"
import logo from '../../assets/icon.png'
import axios from "axios";

const CreatorHeaderSpace = styled.div`
    width: 260px;
    margin-right: 8px;
    display: flex;
    align-items: flex-start;
    height: 40px;
`

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
        <H.HeaderContainer>
            <H.Header>
                <H.HeaderLogoSearchUser>
                    <H.HeaderLogo to={"/creators/dashboard"}>
                        <img src={ logo }/>
                    </H.HeaderLogo>
                    <CreatorHeaderSpace>
                    </CreatorHeaderSpace>
                    <H.AuthLinkContainer>
                        <H.AuthLinkList>
                            {isAuthenticated ? (
                                <>
                                    <H.AuthLinkItem $last = {false}>
                                        <Link to={"#logout"} onClick={handleLogoutClick}>로그아웃</Link>
                                    </H.AuthLinkItem>
                                    <H.AuthLinkItem $last = {true}>
                                        <Link to={"/users/my-page"}>마이페이지</Link> 
                                    </H.AuthLinkItem>
                                </>
                            ) : (
                                <>
                                    <H.AuthLinkItem $last = {false}>
                                        <H.AuthLink to={"/users/signup"}>회원가입</H.AuthLink>
                                    </H.AuthLinkItem>
                                    <H.AuthLinkItem $last = {true}>
                                        <H.AuthLink to={"/users/login"}>로그인</H.AuthLink> 
                                    </H.AuthLinkItem>
                                </>
                            )}
                        </H.AuthLinkList>
                    </H.AuthLinkContainer>
                </H.HeaderLogoSearchUser>
                <H.HeaderContentsEtc>
                    <H.HeaderLinkList>
                        <H.HeaderNavLink $first = {true} to={"/"}>작가 센터</H.HeaderNavLink>
                        <H.HeaderNavLink $first = {false} to={"/webtoons"}>내 작품</H.HeaderNavLink>   
                        <H.HeaderNavLink $first = {false} to={"/webnovels"}>수익 관리</H.HeaderNavLink>
                    </H.HeaderLinkList>
                    <H.HeaderEtcLinkList>
                        <H.HeaderEtcLink to={"/"}>
                            <H.HeaderEtcLinkText>홈</H.HeaderEtcLinkText>
                        </H.HeaderEtcLink>
                        <H.HeaderEtcLink to={"/"}>
                            <H.HeaderEtcLinkText>알림</H.HeaderEtcLinkText>
                        </H.HeaderEtcLink>
                    </H.HeaderEtcLinkList>
                </H.HeaderContentsEtc>
            </H.Header>
        </H.HeaderContainer>
    )

}

export default CreatorHeader;