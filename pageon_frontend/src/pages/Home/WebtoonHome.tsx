import React, { useState, useEffect } from "react";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import * as H from "./Home.styles"
import { SimpleContent, RankingBook } from "../../types/Content";
import ThumbnailContentList from "../../components/Contents/ThumbnailContentList";
import RankingContentList from "../../components/Contents/RankingContentList";
import axios from "axios";
import api from "../../api/axiosInstance";

function WebtoonHome() {
    const dummyBooks: RankingBook[] = [
        {
            id: 1,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/1/webnovel1.png',
            title: '임시 작품 제목 1',
            author: '작가A',
            rating: 4.9124,
            ratingCount: 13974,
            contentType: 'WEBNOVEL'
        },
        {
            id: 2,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/2/webnovel2.png',
            title: '임시 작품 제목 2',
            author: '작가B',
            rating: 4.9124,
            ratingCount: 10266,
            contentType: 'WEBNOVEL'
        },
        {
            id: 3,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/3/webnovel3.png',
            title: '임시 작품 제목 3',
            author: '작가C',
            rating: 4.9124,
            ratingCount: 758,
            contentType: 'WEBNOVEL'
        },
        {
            id: 4,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/4/webnovel4.png',
            title: '임시 작품 제목 4',
            author: '작가D',
            rating: 4.9124,
            ratingCount: 108,
            contentType: 'WEBNOVEL'
        },
        {
            id: 5,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/5/webnovel5.png',
            title: '임시 작품 제목 5',
            author: '작가E',
            rating: 4.8124,
            ratingCount: 4751,
            contentType: 'WEBNOVEL'
        },
        {
            id: 6,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/6/webnovel6.png',
            title: '임시 작품 제목 6',
            author: '작가E',
            rating: 4.8124,
            ratingCount: 7793,
            contentType: 'WEBNOVEL'
        },
        {
            id: 7,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/7/webnovel7.png',
            title: '임시 작품 제목 7',
            author: '작가A',
            rating: 4.7124,
            ratingCount: 4582,
            contentType: 'WEBNOVEL'
        },
        {
            id: 8,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/8/webnovel8.png',
            title: '임시 작품 제목 8',
            author: '작가B',
            rating: 4.7124,
            ratingCount: 591,
            contentType: 'WEBNOVEL'
        },
        {
            id: 9,
            cover: 'https://d2ge55k9wic00e.cloudfront.net/webnovels/9/webnovel9.png',
            title: '임시 작품 제목 9',
            author: '작가C',
            rating: 4.8124,
            ratingCount: 6574,
            contentType: 'WEBNOVEL'
        }
    ]

    const [dailyContents, setDailyContents] = useState<SimpleContent[]>([]);
    const [newContents, setNewContents] = useState<SimpleContent[]>([]);
    const [masterpieceContents, setMasterpieceContents] = useState<SimpleContent[]>([]);

    const todayIndex = new Date().getDay();

    const dayOfWeekNames = ["월", "화", "수", "목", "금", "토", "일"];
    const dayOfWeekNamesEng = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];

    const initialDay = todayIndex === 0 ? "일" : dayOfWeekNames[todayIndex - 1];
    const initialDayEng = todayIndex === 0 ? "SUNDAY" : dayOfWeekNamesEng[todayIndex - 1];

    const [activeDay, setActiveDay] = useState<string>(initialDay);

    useEffect(() => {
        async function fetchData() {
            try {
                const [dailyRes, newRes, masterpieceRes] = await Promise.all([
                    api.get(`/webtoons/daily/${initialDayEng}`),
                    api.get(`/webtoons/recent`, {
                        params: {
                            size: 6
                        }
                    }), 
                    api.get(`/webtoons/masterpiece`, {
                        params: {
                            size: 6
                        }
                    })

                ]);
                
                setDailyContents(dailyRes.data);
                
                setNewContents(newRes.data.content);

                setMasterpieceContents(masterpieceRes.data.content);
            } catch (error) {
                console.error("웹툰 데이터 조회 실패: ", error);
            }
        }

        fetchData();
    }, []);


    const handleDayClick = async(dayIndex: number) => {
        const dayName = dayOfWeekNames[dayIndex];
        setActiveDay(dayName);
        const day = dayOfWeekNamesEng[dayIndex];
        setDailyContents([]);

        try {
            const response = await axios.get(`/api/webtoons/daily/${day}`);
            console.log("요일별 웹툰 데이터: ", response.data);
            setDailyContents(response.data);
        } catch (error) {
            console.error("요일별 웹툰 데이터 조회 실패: ", error);
        }
    }

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
                    <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>요일별 웹툰</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"#"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <H.WeeklyTabsWrapper>
                        <H.WeeklyTabs>
                            {dayOfWeekNames.map((dayName, dayIndex) => (
                                <H.WeeklyTabsBtn key={dayIndex} $isActive={activeDay === dayName} onClick={() => handleDayClick(dayIndex)}>
                                    {dayName}
                                </H.WeeklyTabsBtn>
                            ))}
                        </H.WeeklyTabs>
                    </H.WeeklyTabsWrapper>
                    <ThumbnailContentList contents={dailyContents}  key={activeDay}/>
                </H.SectionBookList>
                <H.SectionBookList>
                    <H.SectionBookListTitle>웹툰 실시간 랭킹</H.SectionBookListTitle>
                    <RankingContentList contents={ dummyBooks } layout="grid" />
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>장르별 인기(ex.로맨스 웹툰 인기작)</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"#"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={newContents} />   
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>웹툰 신작</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"/webtoons/new"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={newContents} />    
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>정주행 필수 명작</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"/webtoons/masterpiece"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={masterpieceContents} />  
                </H.SectionBookList>
            </NoSidebarMain>
        </MainContainer>
    )

}


export default WebtoonHome;