import React, { useEffect, useState } from "react";
import { MainContainer, SidebarMain, SortBtn } from "../../styles/Layout.styles";
import Sidebar from "../../components/Sidebars/MyPageSidebar";
import * as M from "./MyPage.styles"
import { useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";
import { PointTransaction } from "../../types/User";
import { formatDateAndTime, formatNumber } from "../../utils/formatData";
import { Pagination } from "../../types/Page";
import * as S from "../Contents/Viewer.styles";
import PageNavigator from "../../components/Pagination/PageNavigator";

function PointTransactionPage() {
    const [searchParams, setSearchParams] = useSearchParams();

    const type = searchParams.get("type") || "USE";
    const page = parseInt(searchParams.get("page") || "0", 10);

    const [pointTransactions, setPointTransactions] = useState<PointTransaction[]>([]);
    const [ pageData, setPageData ] = useState<Pagination<PointTransaction> | null>(null);
    
    const handleParamClick = (newKey: string, newValue: string) => {
        const newParams = new URLSearchParams(searchParams);

        newParams.set(newKey, newValue);
        newParams.set("page", "0");
        setSearchParams(newParams);
    }

    useEffect(() => {
        async function fetchData() {
            const params: any = {
                type: type,
                page: page
            };

            try {
                const response = await api.get("/points/history", {params: params});
                
                setPointTransactions(response.data.content);
                setPageData(response.data);

            } catch (error) {
                console.log("포인트 내역 조회 실패: ", error);
            }
        }

        fetchData();
    }, [type, page]);

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
                    <M.MypageTitle>내 포인트 내역</M.MypageTitle>
                    <M.MypageBooksSortBtnWrapper>
                        <M.mypageBooksSortBtnList>
                             <M.MypageBooksSelectType>
                                <SortBtn $active={type === "USE"} onClick={() => handleParamClick("type", "USE")}>사용내역</SortBtn>
                                <SortBtn $active={type === "CHARGE"} onClick={() => handleParamClick("type", "CHARGE")}>충전내역</SortBtn>
                            </M.MypageBooksSelectType>
                        </M.mypageBooksSortBtnList>
                    </M.MypageBooksSortBtnWrapper>
                    <M.MypagePointSection>
                        <M.PointTransactionList>
                            {pointTransactions.length === 0 ? (
                                <M.PointTransactionEmpty>
                                    {type === "USE" ? (
                                        <div>사용 내역이 없습니다.</div>
                                    ) : (
                                        <div>충전 내역이 없습니다.</div>
                                    )}  
                                </M.PointTransactionEmpty>
                                
                            ) : (
                             <>
                                <M.PointTransactionTable>
                                    <colgroup>
                                        <M.PointTransactionColDate></M.PointTransactionColDate>
                                        <M.PointTransactionColTitle></M.PointTransactionColTitle>
                                        <M.PointTransactionColAmount></M.PointTransactionColAmount>
                                        <M.PointTransactionColBalance></M.PointTransactionColBalance>
                                    </colgroup>
                                    <M.PointTransactionTableHead>
                                        {type === 'use' ? (
                                            <M.PointTransactionTheadTr>
                                                <M.PointTransactionThDate>구매일</M.PointTransactionThDate>
                                                <M.PointTransactionThTitle>결제 내역</M.PointTransactionThTitle>
                                                <M.PointTransactionThAmount>결제 금액</M.PointTransactionThAmount>
                                                <M.PointTransactionThBalance>잔액</M.PointTransactionThBalance>
                                            </M.PointTransactionTheadTr>
                                        ) : (
                                            <M.PointTransactionTheadTr>
                                                <M.PointTransactionThDate>충전일</M.PointTransactionThDate>
                                                <M.PointTransactionThTitle>충전 내역</M.PointTransactionThTitle>
                                                <M.PointTransactionThAmount>충전 금액</M.PointTransactionThAmount>
                                                <M.PointTransactionThBalance>잔액</M.PointTransactionThBalance>
                                            </M.PointTransactionTheadTr>
                                        )}
                                    </M.PointTransactionTableHead>
                                    <M.PointTransactionTableBody>
                                        {pointTransactions.map((transaction) => (
                                            <M.PointTransactionTbodyTr>
                                                <M.PointTransactionTdDate>{formatDateAndTime(transaction.createdAt)}</M.PointTransactionTdDate>
                                                <M.PointTransactionTdTitle>{transaction.description}</M.PointTransactionTdTitle>
                                                <M.PointTransactionTdAmount>{transaction.amount}P</M.PointTransactionTdAmount>
                                                <M.PointTransactionTdBalance>{formatNumber(transaction.balance)}P</M.PointTransactionTdBalance>
                                            </M.PointTransactionTbodyTr>
                                        ))}
                                    </M.PointTransactionTableBody>
                                </M.PointTransactionTable>
                             </>
                            )}
                            {pageData && pageData.totalPages > 0 && (
                                <PageNavigator pageData={pageData} handlePageChange={handlePageChange} />
                            )}
                        </M.PointTransactionList>
                    </M.MypagePointSection>
                </M.SidebarRightWrap>
            </SidebarMain>
        </MainContainer>
    )
}

export default PointTransactionPage;