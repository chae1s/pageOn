import React, {useEffect, useState} from "react";
import { MainContainer, NoSidebarMain, SortBtn } from "../../styles/Layout.styles";
import { useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";
import * as S from "./Search.styles"
import { SearchContent } from "../../types/Content";
import SearchContentList from "../../components/Contents/SearchContentList";
import { Pagination } from "../../types/Page";

function TitleCreatorSearch() {

    const [pageData, setPageData] = useState<Pagination<SearchContent> | null>(null);
    const [searchParams, setSearchParams] = useSearchParams();

    const type = searchParams.get("type") || "all";
    const q = searchParams.get("q") || "";
    const sort = searchParams.get("sort") || "popular";
    const page = parseInt(searchParams.get("page") || "0", 10) ;

    useEffect(() => {
        async function fetchSearchResults() {
            try {
                const response = await api.get("/search", {
                    params: {
                        type: type,
                        q: q,
                        sort: sort,
                        page: page,
                    }
                });

                console.log(response.data);
                setPageData(response.data);

            } catch (error) {
                console.log("제목 및 작가 검색 결과 조회 실패: ", error);
            }
        }

        fetchSearchResults();
    }, [type, q, sort, page])
    const NextIcon = () => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M8 5l4 5-4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    )

    const PrevIcon = () => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M12 5l-4 5 4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    )

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

    const pageNumbers = getPageNumbers();

    return (
        <MainContainer>
            <NoSidebarMain>
                <S.ContentTypeListInSearch>
                    <S.ContentTypeItemInSearch>
                        <S.ContentTypeBtnInSearch $active={type === "all"} onClick={() => handleParamClick("type", "all")}>
                            전체
                        </S.ContentTypeBtnInSearch>
                    </S.ContentTypeItemInSearch>
                    <S.ContentTypeItemInSearch>
                        <S.ContentTypeBtnInSearch $active={type === "webtoons"} onClick={() => handleParamClick("type", "webtoons")}>
                            웹툰
                        </S.ContentTypeBtnInSearch>
                    </S.ContentTypeItemInSearch>
                    <S.ContentTypeItemInSearch>
                        <S.ContentTypeBtnInSearch $active={type === "webnovels"} onClick={() => handleParamClick("type", "webnovels")}>
                            웹소설
                        </S.ContentTypeBtnInSearch>
                    </S.ContentTypeItemInSearch>
                </S.ContentTypeListInSearch>
                <S.SelectSortSection>
                    <S.SelectSortBtnGroup>
                        <SortBtn $active={sort === "latest"} onClick={() => handleParamClick("sort", "latest")}>최신 순</SortBtn>
                        <SortBtn $active={sort === "rating"} onClick={() => handleParamClick("sort", "rating")}>별점 순</SortBtn>
                        <SortBtn $active={sort === "popular"} onClick={() => handleParamClick("sort", "popular")}>인기 순</SortBtn>
                    </S.SelectSortBtnGroup>
                </S.SelectSortSection>
                {pageData && (
                    <SearchContentList 
                        contents={pageData.content} 
                        totalElements={pageData.totalElements} 
                    />
                )}

                {pageData && pageData.totalPages > 0 && (
                    <S.PaginationContainer>
                        
                        <S.PaginationIconWrapper
                            onClick={() => handlePageChange(pageData.pageNumber - 1)}
                            disabled={pageData.first}
                        >
                            <PrevIcon />
                        </S.PaginationIconWrapper>

                        <S.PaginationNumberList>
                            
                            {pageNumbers.map((number) => (
                                <S.PaginationNumberListItem key={number}>
                                    <S.PaginationNumberBtn
                                        $active={pageData.pageNumber === number}
                                        onClick={() => handlePageChange(number)}
                                    >
                                        {number + 1}
                                    </S.PaginationNumberBtn>
                                </S.PaginationNumberListItem>
                            ))}
                        </S.PaginationNumberList>

                        
                        <S.PaginationIconWrapper
                            onClick={() => handlePageChange(pageData.pageNumber + 1)}
                            disabled={pageData.last}
                        >
                            <NextIcon />
                        </S.PaginationIconWrapper>
                    </S.PaginationContainer>
                )}
            </NoSidebarMain>
        </MainContainer>
    )
}

export default TitleCreatorSearch;