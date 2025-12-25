import React, { useEffect } from "react";
import * as S from "../Styles/Sidebar.styles";
import { Link, useNavigate, NavLink} from "react-router-dom";
import { styled } from "styled-components";
import { useAuth } from "../../context/AuthContext";

const SidebarCreatorLinkWrap = styled.div`
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #f0f0f0;
    color: #999;
    font-size: 14px;
    text-align: right;
`

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

    return (
        <S.SidebarContainser>
            <S.SidebarMainLink to={"/users/my-page"} className="sidebar-main-link">마이페이지</S.SidebarMainLink>
            <aside>
                <S.SidebarNav>
                    <S.SidebarNavSectionUl>
                        <S.SidebarNavSectionLi last = {false}>
                            <S.SidebarNavTitle>책</S.SidebarNavTitle>
                            <S.SidebarNavItemUl>
                                <S.SidebarNavItemLi>
                                    <S.SidebarNavLink to={"/library/interests"} className="sidebar-nav-link">관심 작품</S.SidebarNavLink>
                                </S.SidebarNavItemLi>
                                <S.SidebarNavItemLi>
                                    <S.SidebarNavLink to={"/library/recent-view"} className="sidebar-nav-link">최근 읽은 작품</S.SidebarNavLink>
                                </S.SidebarNavItemLi>
                                <S.SidebarNavItemLi>
                                    <S.SidebarNavLink to={"/library/my-comments"} className="sidebar-nav-link">내가 쓴 댓글</S.SidebarNavLink>
                                </S.SidebarNavItemLi>
                            </S.SidebarNavItemUl>
                        </S.SidebarNavSectionLi>
                        <S.SidebarNavSectionLi last = {false}>
                            <S.SidebarNavTitle>내 정보</S.SidebarNavTitle>
                            <S.SidebarNavItemUl>
                                <S.SidebarNavItemLi>
                                    <S.SidebarNavLink to={"/users/edit"} onClick={handleEditClick} className="sidebar-nav-link">내 정보 수정</S.SidebarNavLink>
                                </S.SidebarNavItemLi>
                                <S.SidebarNavItemLi>
                                    <S.SidebarNavLink to={"/users/withdraw"} className="sidebar-nav-link">회원탈퇴</S.SidebarNavLink>
                                </S.SidebarNavItemLi>
                                <S.SidebarNavItemLi>
                                    <S.SidebarNavLink to={"/library/my-comments"} className="sidebar-nav-link">1:1 문의</S.SidebarNavLink>
                                </S.SidebarNavItemLi>
                            </S.SidebarNavItemUl>
                        </S.SidebarNavSectionLi>
                        <S.SidebarNavSectionLi last = {true}>
                            <S.SidebarNavTitle>구매</S.SidebarNavTitle>
                            <S.SidebarNavItemUl>
                                <S.SidebarNavItemLi>
                                    <S.SidebarNavLink to={"/points/history"} className="sidebar-nav-link">내 포인트 내역</S.SidebarNavLink>
                                </S.SidebarNavItemLi>
                                <S.SidebarNavItemLi>
                                    <S.SidebarNavLink to={"/library/recent-view"} className="sidebar-nav-link">이용권 내역</S.SidebarNavLink>
                                </S.SidebarNavItemLi>
                            </S.SidebarNavItemUl>
                        </S.SidebarNavSectionLi>
                    </S.SidebarNavSectionUl>
                    <SidebarCreatorLinkWrap>
                        {roles.includes("ROLE_CREATOR") ? (
                            <Link to={"/creators/dashboard"}>작가 페이지로 이동하기</Link>
                        ) : (
                            <Link to={"/creators/register"}>작가 등록하기</Link>
                        )}
                    </SidebarCreatorLinkWrap>
                </S.SidebarNav>
            </aside>
        </S.SidebarContainser>
    )


}

export default Sidebar