import React, {useState, useEffect} from "react";
import { Link, NavLink, useNavigate} from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import * as H from "../Styles/Header.styles";
import logo from '../../assets/icon.png'
import api from "../../api/axiosInstance";


function Header() {
    const {isAuthenticated} = useAuth();
    const [query, setQuery] = useState("");
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setQuery(e.target.value);
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const params = new URLSearchParams();
        params.append("type", "all");
        params.append("q", query);
        params.append("page", "0");

        navigate(`/search?${params.toString()}`)
    }
    
    return (
        <H.HeaderContainer>
            <H.Header>
                <H.HeaderLogoSearchUser>
                    <H.HeaderLogo to={"/"}>
                        <img src={ logo }/>
                    </H.HeaderLogo>
                    <H.BookSearchForm onSubmit={handleSubmit}>
                        <H.BookSearchInputWrap>
                            <H.BookSearchInputItem>
                                <H.BookSearchLabel>
                                    <H.BookSearchIcon width="18" height="18" viewBox="0 0 20 20" fill="none" >
                                        <circle cx="9" cy="9" r="7" stroke="#888" strokeWidth="2"/>
                                        <line x1="14.2" y1="14.2" x2="20" y2="20" stroke="#888" strokeWidth="2" strokeLinecap="round"/>
                                    </H.BookSearchIcon>
                                    <H.BookSearchText>인스턴트 검색</H.BookSearchText>
                                    <H.BookSearchInput type="text" maxLength={64} placeholder="제목, 작가를 입력하세요." onChange={handleChange}/>
                                </H.BookSearchLabel>
                            </H.BookSearchInputItem>
                        </H.BookSearchInputWrap>
                    </H.BookSearchForm>
                    <H.AuthLinkContainer>
                        <H.AuthLinkList>
                            {isAuthenticated ? (
                                <>
                                    <H.AuthLinkItem $last = {false}>
                                        <H.AuthLink to={"/library/interests"}>내서재</H.AuthLink>
                                    </H.AuthLinkItem>
                                    <H.AuthLinkItem $last = {true}>
                                        <H.AuthLink to={"/users/my-page"}>마이페이지</H.AuthLink> 
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
                        <H.HeaderNavLink $first = {true} to={"/"}>추천</H.HeaderNavLink>
                        <H.HeaderNavLink $first = {false} to={"/webtoons"}>웹툰</H.HeaderNavLink>   
                        <H.HeaderNavLink $first = {false} to={"/webnovels"}>웹소설</H.HeaderNavLink>
                    </H.HeaderLinkList>
                    <H.HeaderEtcLinkList>
                        <H.HeaderEtcLink to={"/"}>
                            <H.HeaderEtcLinkText>이벤트</H.HeaderEtcLinkText>
                        </H.HeaderEtcLink>
                        <H.HeaderEtcLink to={"/"}>
                            <H.HeaderEtcLinkText>알림</H.HeaderEtcLinkText>
                        </H.HeaderEtcLink>
                        <H.HeaderEtcLink to={"/"}>
                            <H.HeaderEtcLinkText>포인트충전</H.HeaderEtcLinkText>
                        </H.HeaderEtcLink>
                    </H.HeaderEtcLinkList>
                </H.HeaderContentsEtc>
            </H.Header>
        </H.HeaderContainer>
    )

}

export default Header;