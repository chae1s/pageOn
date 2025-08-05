import React, { useState, useEffect } from "react";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import { ContentDetail } from "../../types/Content";
import ContentDetailLayout from "../../components/Contents/ContentDetailLayout";
import axios from "axios";
import { useParams } from "react-router-dom";
import ContentEpisodeListLayout from "../../components/Contents/ContentEpisodeListLayout";

function WebtoonDetailPage(){
    const [webtoon, setWebnovel] = useState<ContentDetail | null>(null);
    const { id } = useParams();

    useEffect(() => {
        async function fetchData() {
            const response = await axios.get(`/api/webtoons/${id}`); 
            console.log(response.data)
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
                <ContentDetailLayout content={webtoon}></ContentDetailLayout>
                <ContentEpisodeListLayout type="webtoons" episodes={webtoon.episodes} />
            </NoSidebarMain>
        </MainContainer>
    )

}

export default WebtoonDetailPage;