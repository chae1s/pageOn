import React, { useState, useEffect } from "react";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import { ContentDetail } from "../../types/Content";
import ContentDetailLayout from "../../components/ContentDetailLayout";
import axios from "axios";
import { useParams } from "react-router-dom";

function WebnovelDetailPage(){
    const [webnovel, setWebnovel] = useState<ContentDetail | null>(null);
    const { id } = useParams();

    useEffect(() => {
        async function fetchData() {
            const response = await axios.get(`/api/webnovels/${id}`); // 실제 ID로 교체
            setWebnovel(response.data);
        }
        fetchData();
    }, [id]);

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
                <ContentDetailLayout type="WEBNOVEL" content={webnovel}></ContentDetailLayout>
            </NoSidebarMain>
        </MainContainer>
    )

}

export default WebnovelDetailPage;