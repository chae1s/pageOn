import { useSearchParams } from "react-router-dom";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import { formatKorean, formatUrl } from "../../utils/formatContentType";
import { useEffect, useState } from "react";
import { SimpleContent } from "../../types/Content";
import api from "../../api/axiosInstance";
import { Pagination } from "../../types/Page";
import * as S from "./ContentList.styles";
import SimpleContentListItem from "../../components/Contents/SimpleContentListItem";
import PageNavigator from "../../components/Pagination/PageNavigator";

interface NewContentsProps {
    contentType: "WEBNOVEL" | "WEBTOON";
}

function NewContents({contentType} : NewContentsProps) {
    
    const [searchParams, setSearchParams] = useSearchParams();
    const [newContents, setNewContents] = useState<SimpleContent[]>([]);
    const [pageData, setPageData] = useState<Pagination<SimpleContent> | null>(null);

    const page = parseInt(searchParams.get("page") || "0", 10);

    useEffect(() => {
        
        console.log(contentType);
        async function fetchData() {
            const params: any = {
                page: page
            }
            try {
                const response = await api.get(`/${formatUrl(contentType)}/recent`, {params: params});

                setNewContents(response.data.content);
                setPageData(response.data);
            } catch (error) {
                console.error("신작 목록 데이터 조회 실패: ", error);
            }
        }

        fetchData();

    }, [contentType, page]);

    const handlePageChange = (newPage: number) => {
        const newParams = new URLSearchParams(searchParams);
        newParams.set("page", newPage.toString());
        setSearchParams(newParams);
    }

    
    if (!contentType) {
        return null;
    }

    return (
        <MainContainer>
            <NoSidebarMain>
                <S.ListPageTitle>{formatKorean(contentType)} 신작</S.ListPageTitle>
                <S.ContentListWrapper>
                    <S.ContentList>
                        {newContents.map((content) => (
                            <SimpleContentListItem content={content}/>
                        ))}
                    </S.ContentList>
                </S.ContentListWrapper>
                {pageData && pageData.totalPages > 0 && (
                    <PageNavigator pageData={pageData} handlePageChange={handlePageChange} />
                )}
            </NoSidebarMain>
        </MainContainer>
    )
}

export default NewContents;