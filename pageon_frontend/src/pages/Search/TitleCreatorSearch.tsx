import React, {useEffect, useState} from "react";
import { MainContainer, NoSidebarMain, SortBtn } from "../../styles/Layout.styles";
import { useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";
import * as S from "./Search.styles"
import { SearchContent } from "../../types/Content";
import SearchContentList from "../../components/Contents/SearchContentList";
import { Pagination } from "../../types/Page";
import PageNavigator from "../../components/Pagination/PageNavigator";

function TitleCreatorSearch() {

    const [pageData, setPageData] = useState<Pagination<SearchContent> | null>(null);
    const [searchParams, setSearchParams] = useSearchParams();

    let contentType = searchParams.get("contentType") || "all";
    const query = searchParams.get("query") || "";
    const sort = searchParams.get("sort") || "popular";
    const page = parseInt(searchParams.get("page") || "0", 10) ;

    useEffect(() => {
        async function fetchSearchResults() {
            try {
                const response = await api.get(`/${contentType}`, {
                    params: {
                        query: query,
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
    }, [contentType, query, sort, page])

    const handleContentTypeChange = (newContentType: string) => {
        contentType = newContentType;
    }

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
                        <S.ContentTypeBtnInSearch $active={contentType === "all"} onClick={() => handleContentTypeChange("all")}>
                            전체
                        </S.ContentTypeBtnInSearch>
                    </S.ContentTypeItemInSearch>
                    <S.ContentTypeItemInSearch>
                        <S.ContentTypeBtnInSearch $active={contentType === "webtoons"} onClick={() => handleContentTypeChange("webtoons")}>
                            웹툰
                        </S.ContentTypeBtnInSearch>
                    </S.ContentTypeItemInSearch>
                    <S.ContentTypeItemInSearch>
                        <S.ContentTypeBtnInSearch $active={contentType === "webnovels"} onClick={() => handleContentTypeChange("webnovels")}>
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
                        type="title"
                    />
                )}

                {pageData && pageData.totalPages > 0 && (
                    <PageNavigator pageData={pageData} handlePageChange={handlePageChange} />
                )}
            </NoSidebarMain>
        </MainContainer>
    )
}

export default TitleCreatorSearch;