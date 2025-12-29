import React, { useState, useEffect } from "react";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import { ContentDetail } from "../../types/Content";
import ContentDetailLayout from "../../components/Contents/ContentDetailLayout";
import axios from "axios";
import api from "../../api/axiosInstance";
import { useParams } from "react-router-dom";
import ContentEpisodeListLayout from "../../components/Contents/ContentEpisodeListLayout";

function WebtoonDetailPage(){
    const [webtoon, setWebtoon] = useState<ContentDetail | null>(null);
    const { contentId } = useParams();

    useEffect(() => {
        async function fetchData() {
            const response = await api.get(`/webtoons/${contentId}`); 
            console.log(response.data)
            setWebtoon(response.data);
        }
        fetchData();
    }, [contentId]);

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
                <ContentDetailLayout content={webtoon}></ContentDetailLayout>
                <ContentEpisodeListLayout type="webtoons" contentId={webtoon.contentId} contentTitle={webtoon.contentTitle} episodes={webtoon.episodes} />
            </NoSidebarMain>
        </MainContainer>
    )

}

export default WebtoonDetailPage;