import React, {useState, useEffect} from "react";
import axios from "axios";
import Sidebar from "../../components/Sidebars/MyPageSidebar";
import * as M from "./MyPage.styles"
import * as C from "../Contents/Comment.styles";
import * as S from "../Contents/Viewer.styles";
import LikeEmptyIcon from "../../assets/emptyHeartIcon.png";
import { MainContainer, SidebarMain, SortBtn } from "../../styles/Layout.styles";
import { useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";
import { MyComment } from "../../types/Comments";
import { Pagination } from "../../types/Page";
import { formatDate } from "../../utils/formatDate";

function MyComments() {
    const [searchParams, setSearchParams] = useSearchParams();

    const type = searchParams.get("type") || "webtoons";
    const page = parseInt(searchParams.get("page") || "0", 10);

    const [pageData, setPageData] = useState<Pagination<MyComment> | null>(null);
    const [myComments, setMyComments] = useState<MyComment[]>([]);

    const handleParamClick = (newKey: string, newValue: string) => {
        const newParams = new URLSearchParams(searchParams);

        newParams.set(newKey, newValue);
        newParams.set("page", "0");
        setSearchParams(newParams);
    }

    useEffect(() => {
        async function getMyComments() {
            try {
                const response = await api.get("/users/comments", {
                    params: {
                        type: type,
                        page: page
                    }
                })

                setMyComments(response.data.content ?? []);
                setPageData(response.data);

                console.log(response.data.content)
            } catch (error) {
                console.log("내가 작성한 댓글 조회 실패: ", error)
            }
        }

        getMyComments();
    }, [type]);


    const handlePageChange = (newPage: number) => {
        const newParams = new URLSearchParams(searchParams);
        newParams.set("page", newPage.toString());
        setSearchParams(newParams);
    }

    const getPageNumbers = () => {
        if (!pageData) return [];

        const currentPage = pageData.pageNumber;
        const totalPages = pageData.totalPages;

        // 한 번에 보여줄 페이지 번호 개수
        const pageBlockSize = 6;

        const startPage = Math.floor(currentPage / pageBlockSize) * pageBlockSize;

        let endPage = startPage + pageBlockSize - 1;

        if (endPage >= totalPages) {
            endPage = totalPages - 1;
        }

        const pages = [];
        for (let i = startPage; i <= endPage; i++) {
            pages.push(i)
        }

        return pages;
    }

    const pageNumbers = getPageNumbers();

    const NextIcon = () => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M8 5l4 5-4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    )

    const PrevIcon = () => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M12 5l-4 5 4 5" stroke="#222" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    )

    
    return (
        <MainContainer>
            <SidebarMain>
                <Sidebar />
                <M.SidebarRightWrap>
                    <M.MypageTitle>내가 쓴 댓글</M.MypageTitle>
                    <M.MypageBooksSortBtnWrapper>
                        <M.mypageBooksSortBtnList>
                             <M.MypageBooksSelectType>
                                <SortBtn $active={type === "webtoons"} onClick={() => handleParamClick("type", "webtoons")}>웹툰</SortBtn>
                                <SortBtn $active={type === "webnovels"} onClick={() => handleParamClick("type", "webnovels")}>웹소설</SortBtn>
                            </M.MypageBooksSelectType>
                        </M.mypageBooksSortBtnList>
                    </M.MypageBooksSortBtnWrapper>
                    <M.MypageCommentsSection>
                        <C.CommentList>
                          <C.CommentListUl>
                            {myComments.length === 0 ? (
                                <C.CommentListEmptyText>작성한 댓글이 없습니다.</C.CommentListEmptyText>
                            ) : (
                                myComments.map((comment) => (
                                    <C.CommentListLi>
                                        <C.CommentContentWrap>
                                            <C.CommentContent>{comment.text}</C.CommentContent>
                                        </C.CommentContentWrap>
                                        <C.CommentInfo>
                                            <C.CommentInfoLeft>                           
                                                <C.CommentEpisode>
                                                    <C.CommentTitle>{comment.contentTitle}</C.CommentTitle>
                                                    <div>{comment.episodeNum}화</div>
                                                </C.CommentEpisode>
                                                <C.CommentDateBtn>
                                                    <div>{formatDate(comment.createdAt)}</div>
                                                    <C.CommentSpace></C.CommentSpace>
                                                    <C.CommentLeftBtn>수정</C.CommentLeftBtn>
                                                    <C.CommentBtnDivider></C.CommentBtnDivider>
                                                    <C.CommentRightBtn type="button">삭제</C.CommentRightBtn>
                                                </C.CommentDateBtn>  
                                            </C.CommentInfoLeft>
                                            <div>
                                                <C.CommentLikeBtn type="button">
                                                    <C.LikeEmptyIcon src={LikeEmptyIcon} />
                                                    <span>{comment.likeCount}</span>
                                                </C.CommentLikeBtn>
                                            </div>
                                        </C.CommentInfo>
                                    </C.CommentListLi>
                                ))
                            )}
                          </C.CommentListUl>
                          {pageData && pageData.totalPages > 0 && (
                            <S.PaginationContainer>
                                
                                <S.PaginationIconWrapper
                                    onClick={() => handlePageChange(pageData.pageNumber - 1)}
                                    disabled={pageData.first}
                                >
                                    <PrevIcon />
                                </S.PaginationIconWrapper>

                                <S.PaginationNumberList>
                                    
                                    {pageNumbers.map((number) => (
                                        <S.PaginationNumberListItem key={number}>
                                            <S.PaginationNumberBtn
                                                $active={pageData.pageNumber === number}
                                                onClick={() => handlePageChange(number)}
                                            >
                                                {number + 1}
                                            </S.PaginationNumberBtn>
                                        </S.PaginationNumberListItem>
                                    ))}
                                </S.PaginationNumberList>

                                
                                <S.PaginationIconWrapper
                                    onClick={() => handlePageChange(pageData.pageNumber + 1)}
                                    disabled={pageData.last}
                                >
                                    <NextIcon />
                                </S.PaginationIconWrapper>
                            </S.PaginationContainer>
                        )}
                        </C.CommentList>
                    </M.MypageCommentsSection>
                </M.SidebarRightWrap>
            </SidebarMain>
        </MainContainer>
    )

}

export default MyComments;