import React, {useEffect, useState} from "react";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import * as H from "./Home.styles"
import { SimpleContent, RankingBook } from "../../types/Content";
import RankingContentList from "../../components/Contents/RankingContentList";
import ThumbnailContentList from "../../components/Contents/ThumbnailContentList";

function Home() {
    const dummyBooks: RankingBook[] = [
        {
            id: 1,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/1/webnovel1.png',
            title: '임시 작품 제목 1',
            author: '작가A',
            rating: 4.9124,
            ratingCount: 13974,
            contentType: 'webnovels'
        },
        {
            id: 2,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/2/webnovel2.png',
            title: '임시 작품 제목 2',
            author: '작가B',
            rating: 4.9124,
            ratingCount: 10266,
            contentType: 'webnovels'
        },
        {
            id: 3,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/3/webnovel3.png',
            title: '임시 작품 제목 3',
            author: '작가C',
            rating: 4.9124,
            ratingCount: 758,
            contentType: 'webnovels'
        },
        {
            id: 4,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/4/webnovel4.png',
            title: '임시 작품 제목 4',
            author: '작가D',
            rating: 4.9124,
            ratingCount: 108,
            contentType: 'webnovels'
        },
        {
            id: 5,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/5/webnovel5.png',
            title: '임시 작품 제목 5',
            author: '작가E',
            rating: 4.8124,
            ratingCount: 4751,
            contentType: 'webnovels'
        },
        {
            id: 6,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/6/webnovel6.png',
            title: '임시 작품 제목 6',
            author: '작가E',
            rating: 4.8124,
            ratingCount: 7793,
            contentType: 'webnovels'
        },
        {
            id: 7,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/7/webnovel7.png',
            title: '임시 작품 제목 7',
            author: '작가A',
            rating: 4.7124,
            ratingCount: 4582,
            contentType: 'webnovels'
        },
        {
            id: 8,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/8/webnovel8.png',
            title: '임시 작품 제목 8',
            author: '작가B',
            rating: 4.7124,
            ratingCount: 591,
            contentType: 'webnovels'
        },
        {
            id: 9,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/9/webnovel9.png',
            title: '임시 작품 제목 9',
            author: '작가C',
            rating: 4.8124,
            ratingCount: 6574,
            contentType: 'webnovels'
        },
        {
            id: 10,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/10/webnovel10.png',
            title: '임시 작품 제목 10',
            author: '작가A',
            rating: 4.9124,
            ratingCount: 13974,
            contentType: 'webnovels'
        },
        {
            id: 11,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/11/webnovel11.png',
            title: '임시 작품 제목 11',
            author: '작가B',
            rating: 4.9124,
            ratingCount: 10266,
            contentType: 'webnovels'
        },
        {
            id: 12,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/12/webnovel12.png',
            title: '임시 작품 제목 12',
            author: '작가C',
            rating: 4.9124,
            ratingCount: 758,
            contentType: 'webnovels'
        },
        {
            id: 13,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/13/webnovel13.png',
            title: '임시 작품 제목 13',
            author: '작가D',
            rating: 4.9124,
            ratingCount: 108,
            contentType: 'webnovels'
        },
        {
            id: 14,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/14/webnovel14.png',
            title: '임시 작품 제목 14',
            author: '작가E',
            rating: 4.8124,
            ratingCount: 4751,
            contentType: 'webnovels'
        },
        {
            id: 15,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/15/webnovel15.png',
            title: '임시 작품 제목 15',
            author: '작가E',
            rating: 4.8124,
            ratingCount: 7793,
            contentType: 'webnovels'
        },
        {
            id: 16,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/16/webnovel16.png',
            title: '임시 작품 제목 16',
            author: '작가A',
            rating: 4.7124,
            ratingCount: 4582,
            contentType: 'webnovels'
        },
        {
            id: 17,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/17/webnovel17.png',
            title: '임시 작품 제목 17',
            author: '작가B',
            rating: 4.7124,
            ratingCount: 591,
            contentType: 'webnovels'
        },
        {
            id: 18,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/18/webnovel18.png',
            title: '임시 작품 제목 18',
            author: '작가C',
            rating: 4.8124,
            ratingCount: 6574,
            contentType: 'webnovels'
        }
    ]
    return(
        <MainContainer>
            <NoSidebarMain>
                <H.HomeBanner>
                    <div className="banner-text">
                        <h1>
                            인기 웹툰과 웹소설을<br />한 곳에서 즐기세요
                        </h1>
                        <p>
                            최신 인기작부터 다양한 장르의 작품까지<br />
                            지금 바로 감상해보세요!
                        </p>
                            <div className="banner-btns">
                            <button className="go-webtoon-btn">웹툰 보러가기</button>
                            <button className="go-webnovel-btn">웹소설 보러가기</button>
                        </div>
                    </div>
                    <div className="banner-image">
                        <img
                        src="https://cdn.ridicdn.net/cover/1/cover13/2023/12/cover_1000000001_1701400000.jpg"
                        alt="메인 배너"
                        />
                    </div>
                </H.HomeBanner>
                <H.SectionBookList>
                    <H.SectionBookListTitle>실시간 랭킹</H.SectionBookListTitle>
                    <RankingContentList contents={ dummyBooks } layout="slider" />
                </H.SectionBookList>
                <H.SectionBookList>
                    <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>이번 주 화제의 작품</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"#"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={dummyBooks} layout="slider"/>
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>오늘의 신작</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"#"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={dummyBooks} layout="slider"/>
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>정주행 랭킹</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"#"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={dummyBooks} layout="slider"/>  
                </H.SectionBookList>
            </NoSidebarMain>
        </MainContainer>
        
    )
}

export default Home;