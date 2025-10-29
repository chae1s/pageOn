import React, {useState, useEffect} from "react";
import { MainContainer, SidebarMain, SortBtn } from "../../styles/Layout.styles";
import * as M from "./MyPage.styles"
import axios from "axios";
import Sidebar from "../../components/Sidebars/MyPageSidebar";
import { SimpleContent } from "../../types/Content";
import ThumbnailContentList from "../../components/Contents/ThumbnailContentList";
import api from "../../api/axiosInstance";
import { useSearchParams } from "react-router-dom";

function InterestContents() {

    const [searchParams, setSearchParams] = useSearchParams();
    const sort = searchParams.get("sort") || "update";
    const type = searchParams.get("type") || "all";
    const page = parseInt(searchParams.get("page") || "0", 10);

    const [interestContents, setInterestContents] = useState<SimpleContent[]>([]);

    useEffect(() => {
        async function fetchData() {

            const params: any = {
                sort: sort,
                page: page,
            };
    
            // 2. type 파라미터를 추가
            if (type === "webnovels") {
                params.type = "WEBNOVEL"
            } else if (type === "webtoons") {
                params.type = "WEBTOON"
            }

            try {
                
                const response = await api.get("/users/interests", {params: params})    
                console.log(response.data.content);
                setInterestContents(response.data.content);

            } catch (error) {
                console.error("나의 관심 목록 데이터 조회 실패: ", error)
            }
        }

        fetchData();
    }, [type, sort]);

    const handleParamClick = (newKey: string, newValue: string) => {
        const newParams = new URLSearchParams(searchParams);

        newParams.set(newKey, newValue);
        newParams.set("page", "0");
        setSearchParams(newParams);
    }

    return (
        <MainContainer>
            <SidebarMain>
                <Sidebar />
                <M.SidebarRightWrap>
                    <M.MypageTitle>관심 작품</M.MypageTitle>
                    <M.MypageBooksSortBtnWrapper>
                        <M.mypageBooksSortBtnList>
                             <M.MypageBooksSelectType>
                                <SortBtn $active={type === "all"} onClick={() => handleParamClick("type", "all")}>전체</SortBtn>
                                <SortBtn $active={type === "webtoons"} onClick={() => handleParamClick("type", "webtoons")}>웹툰</SortBtn>
                                <SortBtn $active={type === "webnovels"} onClick={() => handleParamClick("type", "webnovels")}>웹소설</SortBtn>
                            </M.MypageBooksSelectType>
                            <M.MypageBooksSearchSelectSort>
                                <M.MypageBooksSearchGroup>
                                    <M.MypageBooksSearchInput 
                                        type="text" 
                                        placeholder="책 이름을 입력하세요."
                                    />
                                    <M.MypageBooksSearchBtn>
                                        <svg width="13" height="13" viewBox="0 0 20 20" fill="none"  className="search-icon">
                                            <circle cx="9" cy="9" r="7" stroke="#888" strokeWidth="2"/>
                                            <line x1="14.2" y1="14.2" x2="20" y2="20" stroke="#888" strokeWidth="2" strokeLinecap="round"/>
                                        </svg>
                                    </M.MypageBooksSearchBtn>
                                </M.MypageBooksSearchGroup>
                                <M.MypageBooksSortGroup>
                                <SortBtn $active={sort === "update"} onClick={() => handleParamClick("sort", "update")}>업데이트순</SortBtn>
                                <SortBtn $active={sort === "last_read"} onClick={() => handleParamClick("sort", "last_read")}>최근순</SortBtn>
                                </M.MypageBooksSortGroup>
                            </M.MypageBooksSearchSelectSort>
                        </M.mypageBooksSortBtnList>
                    </M.MypageBooksSortBtnWrapper>
                    
                    <M.BookListSection>
                    <ThumbnailContentList contents={interestContents} layout="grid"/>
                    </M.BookListSection>
                </M.SidebarRightWrap>
            </SidebarMain>
        </MainContainer>
    )

}

export default InterestContents;