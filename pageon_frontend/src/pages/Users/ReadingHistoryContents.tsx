import React, {useState, useEffect} from "react";
import { MainContainer, SidebarMain, SortBtn } from "../../styles/Layout.styles";
import * as S from "./MyPage.styles"
import axios from "axios";
import Sidebar from "../../components/Sidebars/MyPageSidebar";
import { RecentReadContent } from "../../types/Content";
import ThumbnailContentList from "../../components/Contents/ThumbnailContentList";
import { useSearchParams } from "react-router-dom";
import { Pagination } from "../../types/Page";
import api from "../../api/axiosInstance";
import PageNavigator from "../../components/Pagination/PageNavigator";
import RecentReadContentItem from "../../components/Contents/RecentReadContentItem";

function ReadingHistoryContents() {
    
    const [searchParams, setSearchParams] = useSearchParams();

    const sort = searchParams.get("sort") || "recently_read";
    const type = searchParams.get("type") || "all";
    const page = parseInt(searchParams.get("page") || "0", 10);

    const [recentReadContents, setRecentReadContents] = useState<RecentReadContent[]>([]);
    const [pageData, setPageData] = useState<Pagination<RecentReadContent> | null>(null);


    useEffect(()  => {
        async function fetchData() {
            const params: any = {
                type: type,
                sort: sort,
                page: page,
            };

            try {
                const response = await api.get("/users/reading-history", {params: params})

                setRecentReadContents(response.data.content);
                setPageData(response.data);
            } catch (error) {
                console.log("최근 읽은 작품 목록 데이터 조회 실패: ", error)
            }
        }

        fetchData();
    }, [type, sort, page]);

    const handleParamClick = (newKey: string, newValue: string) => {
        const newParams = new URLSearchParams(searchParams);

        newParams.set(newKey, newValue);
        newParams.set("page", "0");
        setSearchParams(newParams);
    }

    const handlePageChange = (newPage: number) => {
        const newParams = new URLSearchParams(searchParams);
        newParams.set("page", newPage.toString());
        setSearchParams(newParams);
    }

    const getPageNumbers = () => {
        if (!pageData) return [];

        const currentPage = pageData.pageNumber;
        const totalPages = pageData.totalPages;

        // 한 번에 보여줄 페이지 번호 개수
        const pageBlockSize = 6;

        const startPage = Math.floor(currentPage / pageBlockSize) * pageBlockSize;

        let endPage = startPage + pageBlockSize - 1;

        if (endPage >= totalPages) {
            endPage = totalPages - 1;
        }

        const pages = [];
        for (let i = startPage; i <= endPage; i++) {
            pages.push(i)
        }

        return pages;
    }

    return (
        <MainContainer>
            <SidebarMain>
                <Sidebar />
                <S.SidebarRightWrap>
                    <S.MypageTitle>관심 작품</S.MypageTitle>
                    <S.MypageBooksSortBtnWrapper>
                        <S.mypageBooksSortBtnList>
                             <S.MypageBooksSelectType>
                                <SortBtn $active={type === "all"} onClick={() => handleParamClick("type", "all")}>전체</SortBtn>
                                <SortBtn $active={type === "webtoons"} onClick={() => handleParamClick("type", "webtoons")}>웹툰</SortBtn>
                                <SortBtn $active={type === "webnovels"} onClick={() => handleParamClick("type", "webnovels")}>웹소설</SortBtn>
                            </S.MypageBooksSelectType>
                            <S.MypageBooksSearchSelectSort>
                                <S.MypageBooksSearchGroup>
                                    <S.MypageBooksSearchInput 
                                        type="text" 
                                        placeholder="책 이름을 입력하세요."
                                    />
                                    <S.MypageBooksSearchBtn>
                                        <svg width="13" height="13" viewBox="0 0 20 20" fill="none"  className="search-icon">
                                            <circle cx="9" cy="9" r="7" stroke="#888" strokeWidth="2"/>
                                            <line x1="14.2" y1="14.2" x2="20" y2="20" stroke="#888" strokeWidth="2" strokeLinecap="round"/>
                                        </svg>
                                    </S.MypageBooksSearchBtn>
                                </S.MypageBooksSearchGroup>
                                <S.MypageBooksSortGroup>
                                    <SortBtn $active={sort === "recently_read"} onClick={() => handleParamClick("sort", "recently_read")}>최근 읽은 순</SortBtn>
                                    <SortBtn $active={sort === "update"} onClick={() => handleParamClick("sort", "update")}>업데이트 순</SortBtn>
                                </S.MypageBooksSortGroup>
                            </S.MypageBooksSearchSelectSort>
                        </S.mypageBooksSortBtnList>
                    </S.MypageBooksSortBtnWrapper>
                    
                    <S.BookListSection>
                        <S.LibraryContentListWrapper>
                            <S.LibraryContentList>
                                {recentReadContents.length === 0 ? (
                                    <S.EmptyListText>최근 읽은 작품이 없습니다.</S.EmptyListText>
                                ) : (
                                    recentReadContents.map((content) => (
                                        <RecentReadContentItem key={content.contentId} content={content}/>
                                    ))
                                )}
                            </S.LibraryContentList>
                        </S.LibraryContentListWrapper>
                        {pageData && pageData.totalPages > 0 && (
                            <PageNavigator pageData={pageData} handlePageChange={handlePageChange} />
                        )}
                    </S.BookListSection>
                </S.SidebarRightWrap>
            </SidebarMain>
        </MainContainer>
    )
}

export default ReadingHistoryContents;