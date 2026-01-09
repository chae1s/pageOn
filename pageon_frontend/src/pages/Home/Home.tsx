import React, {useEffect, useState} from "react";
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import * as H from "./Home.styles"
import { SimpleContent, RankingBook } from "../../types/Content";
import RankingContentList from "../../components/Contents/RankingContentList";
import ThumbnailContentList from "../../components/Contents/ThumbnailContentList";
import api from "../../api/axiosInstance";

function Home() {

    const [masterpieceContents, setMasterpieceContents] = useState<SimpleContent[]>([]);

    const [webnovelKeywordContents, setWebnovelKeywordContents] = useState<SimpleContent[]>([]);
    const [webnovelKeywordName, setWebnovelKeywordName] = useState<string>("");

    const [webtoonKeywordContents, setWebtoonKeywordContents] = useState<SimpleContent[]>([]);
    const [webtoonKeywordName, setWebtoonKeywordName] = useState<string>("");
    const [rankingContents, setRankingContents] = useState<SimpleContent[]>([]);

    useEffect(() => {
        async function fetchData() {
            try {
                const [masterpieceRes, webnovelKeywordRes, webtoonKeywordRes, rankingRes] = await Promise.all([
                    api.get('/recommendation/masterpiece', {
                        params: {
                            size: 6, 
                            contentType: 'all'
                        }
                    }), 
                    api.get('/recommendation/by-keyword', {
                        params: {
                            size: 6,
                            contentType: 'webnovels'
                        }
                    }), 
                    api.get('/recommendation/by-keyword', {
                        params: {
                            size: 6,
                            contentType: 'webtoons'
                        }
                    }), 
                    api.get('/recommendation/hourly-ranking', {
                        params: {
                            contentType: 'all'
                        }
                    })

                ]);
                
                setMasterpieceContents(masterpieceRes.data.content);

                setWebnovelKeywordName(webnovelKeywordRes.data.keyword);
                setWebnovelKeywordContents(webnovelKeywordRes.data.contents.content);

                setWebtoonKeywordName(webtoonKeywordRes.data.keyword);
                setWebtoonKeywordContents(webtoonKeywordRes.data.contents.content);

                setRankingContents(rankingRes.data);
            } catch (error) {
                console.error("작품 데이터 조회 실패: ", error);
            }
        }

        fetchData();
    }, []);

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
                    <RankingContentList contents={ rankingContents } layout="slider" />
                </H.SectionBookList>
                <H.SectionBookList>
                    <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>추천 {webnovelKeywordName} 웹소설</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"/webnovels/recommend/by-keyword"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={webnovelKeywordContents} />
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>추천 {webtoonKeywordName} 웹툰</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"/webtoons/recommend/by-keyword"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={webtoonKeywordContents} />
                </H.SectionBookList>
                <H.SectionBookList>
                     <H.SectionBookTitleWrapper>
                        <H.SectionBookListTitle>정주행 랭킹</H.SectionBookListTitle>
                        <H.SectionBookListMoreViewLink to={"/contents/masterpiece"}>더보기</H.SectionBookListMoreViewLink>
                    </H.SectionBookTitleWrapper>
                    <ThumbnailContentList contents={masterpieceContents} />
                </H.SectionBookList>
            </NoSidebarMain>
        </MainContainer>
        
    )
}

export default Home;