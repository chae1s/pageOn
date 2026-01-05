import React, {useState, useEffect} from "react";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import * as H from "./Home.styles"
import { Link, useSearchParams } from "react-router-dom";
import { SimpleContent, RankingBook } from "../../types/Content";
import axios from "axios";
import ThumbnailContentList from "../../components/Contents/ThumbnailContentList";
import RankingContentList from "../../components/Contents/RankingContentList";
import api from "../../api/axiosInstance";

function WebnovelHome() {

    const [dailyContents, setDailyContents] = useState<SimpleContent[]>([]);
    const [newContents, setNewContents] = useState<SimpleContent[]>([]);
    const [masterpieceContents, setMasterpieceContents] = useState<SimpleContent[]>([]);
    const [keywordContents, setKeywordContents] = useState<SimpleContent[]>([]);
    const [keywordName, setKeywordName] = useState<string>("");
    const [rankingContents, setRankingContents] = useState<SimpleContent[]>([]);
    
    const todayIndex = new Date().getDay();

    const dayOfWeekNames = ["월", "화", "수", "목", "금", "토", "일"];
    const dayOfWeekNamesEng = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
    
    const initialDay = todayIndex === 0 ? "일" : dayOfWeekNames[todayIndex - 1];
    const initialDayEng = todayIndex === 0 ? "SUNDAY" : dayOfWeekNamesEng[todayIndex - 1];
    
    
    const [activeDay, setActiveDay] = useState<string>(initialDay);


    useEffect(() => {
        async function fetchData() {
            try {

                const params = {
                    size: 6,
                    contentType: 'webnovels'
                }
                
                const [dailyRes, newRes, masterpieceRes, keywordRes, rankingRes] = await Promise.all([
                    api.get(`/webnovels/daily/${initialDayEng}`),
                    api.get('/recommendation/recent', {
                        params: params
                    }),
                    api.get('/recommendation/masterpiece', {
                        params: params
                    }), 
                    api.get('/recommendation/by-keyword', {
                        params: params
                    }), 
                    api.get('recommendation/hourly-ranking', {
                        params: {
                            contentType: 'webnovels'
                        }
                    })
                ]);
                
                setDailyContents(dailyRes.data);

                setNewContents(newRes.data.content);

                setMasterpieceContents(masterpieceRes.data.content);

                setKeywordName(keywordRes.data.keyword);

                setKeywordContents(keywordRes.data.contents.content);

                setRankingContents(rankingRes.data.content);
            } catch (error) {
                console.error("웹소설 데이터 조회 실패: ", error);
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
            const response = await axios.get(`/api/webnovels/daily/${day}`);
            
            setDailyContents(response.data);
        } catch (error) {
            console.error("웹소설 데이터 조회 실패: ", error);
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
                        <H.SectionBookListTitle>요일별 웹소설</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"#"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <H.WeeklyTabsWrapper>
                        <H.WeeklyTabs>
                            {dayOfWeekNames.map((dayName, dayIndex) => (
                                <H.WeeklyTabsBtn key={dayName} $isActive={activeDay === dayName} onClick={() => handleDayClick(dayIndex)}>
                                    {dayName}
                                </H.WeeklyTabsBtn>
                            ))}
                        </H.WeeklyTabs>
                    </H.WeeklyTabsWrapper>
                    <ThumbnailContentList contents={dailyContents} />
                </H.SectionBookList>
                <H.SectionBookList>
                    <H.SectionBookListTitle>웹소설 실시간 랭킹</H.SectionBookListTitle>
                    <RankingContentList contents={ rankingContents } layout="grid" />
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>추천 {keywordName} 웹소설</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"/webnovels/recommend/by-keyword"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={ keywordContents } />    
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>웹소설 신작</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"/webnovels/new"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={ newContents } />   
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>정주행 필수 명작</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"/webnovels/masterpiece"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={ masterpieceContents } />    
                </H.SectionBookList>
            </NoSidebarMain>
        </MainContainer>
    )

}


export default WebnovelHome;