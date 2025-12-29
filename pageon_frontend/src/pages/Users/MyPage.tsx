import React, { useState, useEffect } from "react";
import styled from "styled-components";
import { MainContainer, SidebarMain } from "../../styles/Layout.styles";
import * as M from "./MyPage.styles"
import { UserSimpleProfile } from "../../types/User";
import { useNavigate, Link} from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { SimpleContent } from "../../types/Content";
import Sidebar from "../../components/Sidebars/MyPageSidebar";
import api from "../../api/axiosInstance";
import MyPageSimpleContentItem from "../../components/Contents/MyPageSimpleContentItem";

const MypageSummaryContainer = styled.div`
    display: flex;
    border: 1.5px solid #e0e4ea;
    border-radius: 6px;
    background: #fff;
    margin-bottom: 32px;
    overflow: hidden;
    height: 180px;
`

const MypageSummaryLeft = styled.div`
    background: #f6faff;
    padding: 20px 15px;
    min-width: 180px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: space-between;
`

const MypageNickname = styled.div`
    font-size: 1.3rem;
    font-weight: bold;
    margin: 22px 10px 24px;
    color: #444;
`

const MypageLogoutLink = styled(Link)`
    background: #f8fafc;
    color: #666;
    font-weight: 500;
    border-radius: 4px;
    padding: 9px 16px;
    font-size: 1rem;
    cursor: pointer;
`

const MypageSummaryRight = styled.div`
    flex: 1;
    padding: 24px 32px;
    background: #fff;
    display: flex;
    flex-direction: column;
    justify-content: center;
`

const MypageSummaryRow = styled.div`
    display: flex;
    justify-content: space-between;
    margin-bottom: 0;
`

const MypageSummaryItem = styled.div`
    flex: 1;
    text-align: center;
`

const MypageSummaryIcon = styled.div`
    height: 35px;
    width: 35px;
    margin: 3px auto;
`

const MypageSummaryLabel = styled.div`
    font-size: 15px;
    font-weight: 500;
    margin-top: 5px;
    margin-bottom: 12px;
`

const MypageSummaryValueWrap = styled.div`
    margin: 0 auto;
    display: flex;
    justify-content: center;
    align-items: center;
`

const MypageSummaryValue = styled.div`
    font-size: 23px;
    font-weight: 500;
    color: #444;
    margin-bottom: 2px;
    display: inline-block;
`

const MypageSummaryValueUnit = styled.span`
    font-size: 16px;
    font-weight: 400;
    color: #888;
    margin-left: 2px;
    vertical-align: baseline;
    position: static;
`

const MypageChargeLinkWrap = styled.div`
    font-size: 0.95rem;
    color: #888;
    margin-top: 10px;
`

const MypageChargeLink = styled(Link)`
    color: #b0b8c1;
    text-decoration: none;
    font-weight: 500;
    font-size: 0.92em;
`

const BookSectionList = styled.section`
    margin-bottom: 36px;
`

const BookSectionTitle = styled.div`
    font-size: 1.15rem;
    font-weight: 600;
    margin-bottom: 16px;
    color: #333;
    padding-left: 0;
    display: flex;
    align-items: center;
    position: relative;
    width: 100%;
`

const BookSectionTitleText = styled.span`
    flex: 0 0 auto;
    z-index: 1;  
`

const BookSectionTitleLine = styled.span`
    flex: 1 1 auto;
    height: 1px;
    background: #e0e4ea;
    margin: 0 16px;
    display: block;
`

const BookSectionViewAllLink = styled(Link)`
    color: #b0b8c1;
    background: none;
    border: none;
    font-size: 0.98rem;
    font-weight: 500;
    cursor: pointer;
    text-decoration: none;
    padding: 4px 0px 4px 12px;
    border-radius: 4px;
    transition: background 0.15s;
    display: flex;
    align-items: center;
    gap: 2px;
    margin-left: 0;
`

const UpdateContentListWrapper = styled.div`
    position: relative;
    width: 100%;
    overflow: hidden;
    padding-bottom: 50px;
`

const UpdateContentList = styled.div`
    display: flex;
    flex-wrap: wrap;
    gap: 2px;
    justify-content: flex-start;
    width: 100%;
`

const EmptyListText = styled.div`
    margin: 10px auto;
`



function MyPage() {
    const [userInfo, setUserInfo] = useState<UserSimpleProfile>({
        id: 0,
        nickname: "",
        pointBalance: 0
    });

    const [updateContents, setUpdateContents] = useState<SimpleContent[]>([]);

    const {logout} = useAuth();

    const navigate = useNavigate();

    useEffect(() => {
        async function fetchData() {
            try {
                const response = await api.get<any>("/users/me");

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

    useEffect(() => {
        async function fetchContentData() {
            try {
                const response = await api.get("/users/reading-histories/today");

                setUpdateContents(response.data);
            } catch (error) {
                console.error("마이페이지 오늘 업데이트된 작품 리스트 조회 실패: ", error);
            }
        }

        fetchContentData();
    }, []);

    const handleLogoutClick = async (e:React.MouseEvent<HTMLAnchorElement | HTMLButtonElement>) => {
        e.preventDefault();

        if (!window.confirm("로그아웃 하시겠습니까?")) return;

        try {
            const response = await api.get("/users/logout")

            if (response.status === 200) {
                navigate("/", {replace: true});
                setTimeout(() => {
                    logout();
                }, 0);
            } else {
                alert("로그아웃에 실패했습니다.");
            }
        } catch (err) {
            alert("로그아웃 중 오류가 발생했습니다.");
        }
    };



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
        <MainContainer>
            <SidebarMain>
                <Sidebar />
                <M.SidebarRightWrap>
                    <MypageSummaryContainer>
                        <MypageSummaryLeft>
                            <MypageNickname>{userInfo?.nickname}</MypageNickname>
                            <MypageLogoutLink to={"#logout"} onClick={handleLogoutClick}>로그아웃</MypageLogoutLink>
                        </MypageSummaryLeft>
                        <MypageSummaryRight>
                            <MypageSummaryRow>
                                <MypageSummaryItem>
                                    <MypageSummaryIcon>
                                        <PointIcon />
                                    </MypageSummaryIcon>
                                    <MypageSummaryLabel>내 포인트</MypageSummaryLabel>
                                    <MypageSummaryValueWrap>
                                        <MypageSummaryValue>{userInfo?.pointBalance}</MypageSummaryValue>
                                        <MypageSummaryValueUnit>P</MypageSummaryValueUnit>
                                    </MypageSummaryValueWrap>
                                    <MypageChargeLinkWrap>
                                        <MypageChargeLink to={"#charge"}>충전하기</MypageChargeLink>
                                    </MypageChargeLinkWrap>
                                </MypageSummaryItem>
                                <MypageSummaryItem>
                                    <MypageSummaryIcon>
                                        <CouponIcon />
                                    </MypageSummaryIcon>
                                    <MypageSummaryLabel>쿠폰</MypageSummaryLabel>
                                    <MypageSummaryValueWrap>
                                        <MypageSummaryValue>0</MypageSummaryValue>
                                        <MypageSummaryValueUnit>개</MypageSummaryValueUnit>
                                    </MypageSummaryValueWrap>
                                </MypageSummaryItem>
                                <MypageSummaryItem>
                                    <MypageSummaryIcon>
                                        <BookIcon />
                                    </MypageSummaryIcon>
                                    <MypageSummaryLabel>내가 읽은 작품</MypageSummaryLabel>
                                    <MypageSummaryValueWrap>
                                        <MypageSummaryValue>0</MypageSummaryValue>
                                        <MypageSummaryValueUnit>개</MypageSummaryValueUnit>
                                    </MypageSummaryValueWrap>
                                </MypageSummaryItem>
                            </MypageSummaryRow>
                        </MypageSummaryRight>
                    </MypageSummaryContainer>
                    <BookSectionList>
                        <BookSectionTitle>
                            <BookSectionTitleText>오늘 업데이트된 작품</BookSectionTitleText>
                            <BookSectionTitleLine></BookSectionTitleLine>
                            <BookSectionViewAllLink to="/library/recent-view?sort=update">더보기</BookSectionViewAllLink>
                        </BookSectionTitle>
                        <UpdateContentListWrapper>
                            <UpdateContentList>
                                {updateContents.length === 0 ? (
                                    <EmptyListText>오늘 업데이트된 작품이 없습니다.</EmptyListText>
                                ) : (
                                    updateContents.map((content) => (
                                        <MyPageSimpleContentItem content={content}/>
                                    ))
                                )}
                            </UpdateContentList>
                        </UpdateContentListWrapper>
                    </BookSectionList>
                </M.SidebarRightWrap>
            </SidebarMain>

        </MainContainer>
    ) 
}

export default MyPage;