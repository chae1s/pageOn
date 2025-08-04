import React, { useState, useEffect } from "react";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import { ContentDetail } from "../../types/Content";
import ContentDetailLayout from "../../components/ContentDetailLayout";
import axios from "axios";
import { useParams } from "react-router-dom";

function WebtoonDetailPage(){
    const [webtoon, setWebnovel] = useState<ContentDetail | null>(null);
    const { id } = useParams();

    useEffect(() => {
        async function fetchData() {
            const response = await axios.get(`/api/webtoons/${id}`); // 실제 ID로 교체
            setWebnovel(response.data);
        }
        fetchData();
    }, [id]);

    if (!webtoon) {
        return (
            <MainContainer>
                <NoSidebarMain>
                    <p>로딩 중...</p>
                </NoSidebarMain>
            </MainContainer>
        )
    }

    return(
        <MainContainer>
            <NoSidebarMain>
                <ContentDetailLayout type="WEBTOON" content={webtoon}></ContentDetailLayout>
            </NoSidebarMain>
        </MainContainer>
    )

}

export default WebtoonDetailPage;