import React, {useState, useEffect} from "react";
import { MainContainer, SidebarMain, SortBtn } from "../../styles/Layout.styles";
import * as M from "./MyPage.styles"
import axios from "axios";
import Sidebar from "../../components/Sidebars/MyPageSidebar";
import { SimpleContent } from "../../types/Content";
import ThumbnailContentList from "../../components/Contents/ThumbnailContentList";

function FavoriteWorks() {

    const dummyBooks: SimpleContent[] = [
        {
            id: 1,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/1/webnovel1.png',
            title: '임시 작품 제목 1',
            author: '작가A',
            contentType: 'WEBNOVEL'
        },
        {
            id: 2,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/2/webnovel2.png',
            title: '임시 작품 제목 2',
            author: '작가B',
            contentType: 'WEBNOVEL'
        },
        {
            id: 3,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/3/webnovel3.png',
            title: '임시 작품 제목 3',
            author: '작가C',
            contentType: 'WEBNOVEL'
        },
        {
            id: 4,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/4/webnovel4.png',
            title: '임시 작품 제목 4',
            author: '작가D',
            contentType: 'WEBNOVEL'
        },
        {
            id: 5,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/5/webnovel5.png',
            title: '임시 작품 제목 5',
            author: '작가E',
            contentType: 'WEBNOVEL'
        },
        {
            id: 6,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/6/webnovel6.png',
            title: '임시 작품 제목 6',
            author: '작가E',
            contentType: 'WEBNOVEL'
        },
        {
            id: 7,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/7/webnovel7.png',
            title: '임시 작품 제목 7',
            author: '작가A',
            contentType: 'WEBNOVEL'
        },
        {
            id: 8,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/8/webnovel8.png',
            title: '임시 작품 제목 8',
            author: '작가B',
            contentType: 'WEBNOVEL'
        },
        {
            id: 9,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/9/webnovel9.png',
            title: '임시 작품 제목 9',
            author: '작가C',
            contentType: 'WEBNOVEL'
        },
        {
            id: 10,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/10/webnovel10.png',
            title: '임시 작품 제목 10',
            author: '작가D',
            contentType: 'WEBNOVEL'
        }
    ]

    const [sort, setSort] = useState<string>("updated") // updated | recent
    const [type, setType] = useState<string>("all")     // all | webtoons | webnovels

    return (
        <MainContainer>
            <SidebarMain>
                <Sidebar />
                <M.SidebarRightWrap>
                    <M.MypageTitle>관심 작품</M.MypageTitle>
                    <M.MypageBooksSortBtnWrapper>
                        <M.mypageBooksSortBtnList>
                             <M.MypageBooksSelectType>
                                <SortBtn active={type === "all"} onClick={()=>setType('all')}>전체</SortBtn>
                                <SortBtn active={type === "webtoons"} onClick={()=>setType('webtoons')}>웹툰</SortBtn>
                                <SortBtn active={type === "webnovels"} onClick={()=>setType('webnovels')}>웹소설</SortBtn>
                            </M.MypageBooksSelectType>
                            <M.MypageBooksSearchSelectSort>
                                <M.MypageBooksSearchGroup>
                                    <M.MypageBooksSearchInput 
                                        type="text" 
                                        placeholder="책 이름을 입력하세요."
                                    />
                                    <M.MypageBooksSearchBtn>
                                        <svg width="13" height="13" viewBox="0 0 20 20" fill="none"  className="search-icon">
                                            <circle cx="9" cy="9" r="7" stroke="#888" strokeWidth="2"/>
                                            <line x1="14.2" y1="14.2" x2="20" y2="20" stroke="#888" strokeWidth="2" strokeLinecap="round"/>
                                        </svg>
                                    </M.MypageBooksSearchBtn>
                                </M.MypageBooksSearchGroup>
                                <M.MypageBooksSortGroup>
                                    <SortBtn active={sort === "updated"} onClick={()=>setSort('updated')}>업데이트순</SortBtn>
                                    <SortBtn active={sort === "recent"} onClick={()=>setSort('recent')}>최근순</SortBtn>
                                </M.MypageBooksSortGroup>
                            </M.MypageBooksSearchSelectSort>
                        </M.mypageBooksSortBtnList>
                    </M.MypageBooksSortBtnWrapper>
                    
                    <M.BookListSection>
                    <ThumbnailContentList contents={dummyBooks} layout="grid"/>
                    </M.BookListSection>
                </M.SidebarRightWrap>
            </SidebarMain>
        </MainContainer>
    )

}

export default FavoriteWorks;