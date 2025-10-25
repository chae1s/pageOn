import React, {useEffect, useState} from "react";
import { MainContainer, NoSidebarMain, SortBtn } from "../../styles/Layout.styles";
import { useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";
import { Category } from "../../types/Keyword";
import * as S from "./Search.styles"
import { SearchContent } from "../../types/Content";
import SearchContentList from "../../components/Contents/SearchContentList";
import { Pagination } from "../../types/Page";

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

    const type = searchParams.get("type") || "webtoons";
    const q = searchParams.get("q") || "SF";
    const sort = searchParams.get("sort") || "latest"
    const page = parseInt(searchParams.get("page") || "0", 10);

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

    useEffect(() => {
        async function fetchCategoryKeywords() {
            try {

                const response = await api.get("/keywords");
                
                setCategories(response.data);
                console.log(categories);
            } catch (error) {
                console.error("카테고리 별 키워드 조회 실패: ", error);
            }
        }

        fetchCategoryKeywords();
        
    }, []);

    useEffect(() => {
        async function fetchSearchResults() {
            try {
                const response = await api.get("/search/keywords", {
                    params: {
                        type: type,
                        q: q,
                        page: page,
                    }
                });
                
                console.log(response.data)
                setPageData(response.data)

            } catch (error) {
                console.error("키워드 검색 결과 조회 실패: ", error);
            }
        }

        if (q) {
            fetchSearchResults();
        }
        
    }, [type, q, sort, page]); // 👈 type, q, sort가 바뀔 때마다 재실행

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
                        <S.ContentTypeBtn $active={type === "webtoons"} onClick={() => handleParamClick("type", "webtoons")}>
                            웹툰
                        </S.ContentTypeBtn>
                    </S.ContentTypeItem>
                    <S.ContentTypeItem>
                        <S.ContentTypeBtn $active={type === "webnovels"} onClick={() => handleParamClick("type", "webnovels")}>
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
                                            <S.KeywordBtn $active={q === `${keyword.name}`} onClick={() => handleParamClick("q", `${keyword.name}`)}>
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
                        <SortBtn $active={sort === "latest"} onClick={() => handleParamClick("sort", "latest")}>최신순</SortBtn>
                        <SortBtn $active={sort === "rating"} onClick={() => handleParamClick("sort", "rating")}>별점순</SortBtn>
                        <SortBtn $active={sort === "popular"} onClick={() => handleParamClick("sort", "popular")}>인기순</SortBtn>
                    </S.SelectSortBtnGroup>
                </S.SelectSortSection>
                {pageData && (
                    <SearchContentList 
                        contents={pageData.content} 
                        totalElements={pageData.totalElements} // 👈 totalElement -> totalElements로 수정
                    />
                )}

{pageData && pageData.totalPages > 0 && (
                    <S.PaginationContainer>
                        {/* 👈 3. 이전 버튼에 onClick, disabled 속성 추가 */}
                        <S.PaginationIconWrapper
                            onClick={() => handlePageChange(pageData.pageNumber - 1)}
                            disabled={pageData.first}
                        >
                            <PrevIcon />
                        </S.PaginationIconWrapper>

                        <S.PaginationNumberList>
                            {/* 👈 4. 불필요한 바깥쪽 ListItem 제거 */}
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

                        {/* 👈 5. 다음 버튼에 onClick, disabled 속성 추가 */}
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

export default KeywordSearch;