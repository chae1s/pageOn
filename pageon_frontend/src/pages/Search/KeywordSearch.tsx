import React, {useEffect, useState} from "react";
import { MainContainer, NoSidebarMain, SortBtn } from "../../styles/Layout.styles";
import { useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";
import { Category } from "../../types/Keyword";
import * as S from "./Search.styles"

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

    const type = searchParams.get("type") || "webtoons";
    const q = searchParams.get("q") || "SF";
    const sort = searchParams.get("sort") || "latest"

    useEffect(() => {
        async function fetchData() {
            const params: any = {
                
            };

            try {

                const response = await api.get("/keywords", {params: params});
                
                setCategories(response.data);
                console.log(categories);
            } catch (error) {
                console.error("카테고리 별 키워드 조회 실패: ", error);
            }
        }

        fetchData();
        
    }, [type, q]);

    const handleParamClick = (newKey: string, newValue: string) => {
        const newParams = new URLSearchParams(searchParams);

        newParams.set(newKey, newValue);

        setSearchParams(newParams);
    }


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
            </NoSidebarMain>
        </MainContainer>
    )
}

export default KeywordSearch;