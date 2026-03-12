import React, {useEffect, useState} from "react";
import { MainContainer, NoSidebarMain, SortBtn } from "../../styles/Layout.styles";
import { useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";
import { Category } from "../../types/Keyword";
import * as S from "./Search.styles"
import { SearchContent } from "../../types/Content";
import SearchContentList from "../../components/Contents/SearchContentList";
import { Pagination } from "../../types/Page";
import PageNavigator from "../../components/Pagination/PageNavigator";

function KeywordSearch() {

    const [searchParams, setSearchParams] = useSearchParams();
    const categoryMap: Record<string, string> = {
        genre: "장르",
        theme: "소재", 
        setting: "배경",
        mood: "분위기", 
        others: "형식/기타",
    }

    const [categories, setCategories] = useState<Category[]>([]);

    const [pageData, setPageData] = useState<Pagination<SearchContent> | null>(null);

    const contentType = searchParams.get("contentType") || "webtoons";
    const query = searchParams.get("keyword") || "SF";
    const sort = searchParams.get("sort") || "popular"
    const page = parseInt(searchParams.get("page") || "0", 10);

    useEffect(() => {
        async function fetchCategoryKeywords() {
            window.scrollTo(0, 0);

            try {

                const response = await api.get("/keywords");
                
                setCategories(response.data);
                console.log(categories);
            } catch (error) {
                console.error("카테고리 별 키워드 조회 실패: ", error);
            }
        }

        fetchCategoryKeywords();
        
    }, [query]);

    useEffect(() => {
        async function fetchSearchResults() {
            try {
                const response = await api.get(`/${contentType}`, {
                    params: {
                        keyword: query,
                        sort: sort,
                        page: page,
                    }
                });
                
                setPageData(response.data)

            } catch (error) {
                console.error("키워드 검색 결과 조회 실패: ", error);
            }
        }

        if (query) {
            fetchSearchResults();
        }
        
    }, [contentType, query, sort, page]); 

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
                <S.ContentTypeList>
                    <S.ContentTypeItem>
                        <S.ContentTypeBtn $active={contentType === "webtoons"} onClick={() => handleParamClick("contentType", "webtoons")}>
                            웹툰
                        </S.ContentTypeBtn>
                    </S.ContentTypeItem>
                    <S.ContentTypeItem>
                        <S.ContentTypeBtn $active={contentType === "webnovels"} onClick={() => handleParamClick("contentType", "webnovels")}>
                            웹소설
                        </S.ContentTypeBtn>
                    </S.ContentTypeItem>
                </S.ContentTypeList>
                <S.KeywordTable>
                    {categories.map((category) => (
                        <S.CategoryWithKeywords key={category.id}>
                            <S.CategoryName>{categoryMap[category.name]}</S.CategoryName>
                            <S.KeywordList>
                                <S.KeywordItemWrap>
                                    {category.keywords.map((keyword) => (
                                        <S.KeywordItem key={keyword.id}>
                                            <S.KeywordBtn $active={query === `${keyword.name}`} onClick={() => handleParamClick("keyword", `${keyword.name}`)}>
                                                {keyword.name}
                                            </S.KeywordBtn>
                                        </S.KeywordItem>
                                    ))}
                                </S.KeywordItemWrap>
                            </S.KeywordList>
                        </S.CategoryWithKeywords>
                    ))}
                </S.KeywordTable>
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
                        type="keyword"
                    />
                )}

                {pageData && pageData.totalPages > 0 && (
                    <PageNavigator pageData={pageData} handlePageChange={handlePageChange} />
                )}
                
                
            </NoSidebarMain>
        </MainContainer>
    )
}

export default KeywordSearch;