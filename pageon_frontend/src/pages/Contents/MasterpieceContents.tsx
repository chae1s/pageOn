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

interface Props {
    contentType: "WEBNOVEL" | "WEBTOON" | "ALL";
}

function MasterpieceContents({contentType} : Props) {

    const [searchParams, setSearchParams] = useSearchParams();
    const [masterpieceContents, setMasterpieceContents] = useState<SimpleContent[]>([]);
    const [pageData, setPageData] = useState<Pagination<SimpleContent> | null>(null);

    const page = parseInt(searchParams.get("page") || "0", 10);

    useEffect(() => {
        
        console.log(contentType);
        async function fetchData() {
            const params: any = {
                page: page,
                contentType: formatUrl(contentType)
            }
            try {
                const response = await api.get('/recommendation/masterpiece', {
                    params: params
                });

                setMasterpieceContents(response.data.content);
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
                <S.ListPageTitle>정주행 필수 {contentType ==='ALL' ? '명작' : formatKorean(contentType)}</S.ListPageTitle>
                <S.ContentListWrapper>
                    <S.ContentList>
                        {masterpieceContents.map((content) => (
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

export default MasterpieceContents;