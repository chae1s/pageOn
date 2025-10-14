import React, { useState, useEffect } from "react";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import { ContentDetail } from "../../types/Content";
import ContentDetailLayout from "../../components/Contents/ContentDetailLayout";
import axios from "axios";
import { useParams } from "react-router-dom";
import ContentEpisodeListLayout from "../../components/Contents/ContentEpisodeListLayout";

function WebnovelDetailPage(){
    const [webnovel, setWebnovel] = useState<ContentDetail | null>(null);
    const { contentId } = useParams();

    useEffect(() => {
        async function fetchData() {
            const response = await axios.get(`/api/webnovels/${contentId}`); 
            setWebnovel(response.data);
        }
        fetchData();
    }, [contentId]);

    if (!webnovel) {
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
                <ContentDetailLayout content={webnovel} />
                <ContentEpisodeListLayout type="webnovels" contentId={webnovel.id} episodes={webnovel.episodes} />
            </NoSidebarMain>
        </MainContainer>
    )

}

export default WebnovelDetailPage;