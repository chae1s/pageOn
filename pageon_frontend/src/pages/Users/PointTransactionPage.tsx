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
    const [isChargeOpen, setIsChargeOpen] = useState<boolean>(true);
    const [openTransactionId, setOpenTransactionId] = useState<number | null>(null);
    
    const handleParamClick = (newKey: string, newValue: string) => {
        const newParams = new URLSearchParams(searchParams);

        // type 이 CHARGE 인 상태에서 다시 "충전내역"을 누르면 토글로 여닫기
        if (newKey === "type" && newValue === "CHARGE" && type === "CHARGE") {
            setIsChargeOpen((prev) => !prev);
            return;
        }

        newParams.set(newKey, newValue);
        newParams.set("page", "0");
        setSearchParams(newParams);

        if (newKey === "type" && newValue === "CHARGE") {
            setIsChargeOpen(true);
        }
    }

    useEffect(() => {
        async function fetchData() {
            const params: any = {
                type: type,
                page: page
            };

            try {
                const response = await api.get("/users/points/history", {params: params});
                
                setPointTransactions(response.data.content);
                setPageData(response.data);

            } catch (error) {
                console.log("포인트 내역 조회 실패: ", error);
            }
        }

        fetchData();
        setOpenTransactionId(null);
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

    const handleRefundClick = async () => {
        try {

            const isConfirmed = window.confirm(
                "환불하시겠습니까?"
            );

            if (!isConfirmed) return;

            const response = await api.post(`/payments/cancel/${openTransactionId}`);

            console.log(response.data);
            window.location.reload();
        } catch (error) {
            console.error("결제 취소 실패: ", error);
        }
    };

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
                                {(type !== "CHARGE" || isChargeOpen) && (
                                    <M.PointTransactionTable>
                                        <colgroup>
                                            <M.PointTransactionColDate></M.PointTransactionColDate>
                                            <M.PointTransactionColTitle></M.PointTransactionColTitle>
                                            <M.PointTransactionColAmount></M.PointTransactionColAmount>
                                            <M.PointTransactionColBalance></M.PointTransactionColBalance>
                                        </colgroup>
                                        <M.PointTransactionTableHead>
                                            {type === 'USE' ? (
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
                                            {pointTransactions.map((transaction) => {
                                                const isOpen = type === "CHARGE" && openTransactionId === transaction.id;
                                                const isRefunded = transaction.transactionStatus?.toLowerCase() === "refunded";
                                                const baseDate = isRefunded && transaction.cancelledAt
                                                    ? transaction.cancelledAt
                                                    : transaction.paidAt;
                                                const createdAtDate = new Date(baseDate);
                                                const now = new Date();
                                                const diffMs = now.getTime() - createdAtDate.getTime();
                                                const sevenDaysMs = 7 * 24 * 60 * 60 * 1000;
                                                const isRefundAvailable = diffMs <= sevenDaysMs;
                                                const refundedStyle = isRefunded
                                                    ? { textDecoration: "line-through", color: "#999" }
                                                    : undefined;

                                                return (
                                                    <React.Fragment key={transaction.id}>
                                                        <M.PointTransactionTbodyTr
                                                            style={isOpen ? { borderBottom: "none" } : undefined}
                                                        >
                                                            <M.PointTransactionTdDate style={refundedStyle}>
                                                                {formatDateAndTime(baseDate)}
                                                            </M.PointTransactionTdDate>
                                                            <M.PointTransactionTdTitle
                                                                onClick={() => {
                                                                    if (type === "CHARGE") {
                                                                        setOpenTransactionId((prev) =>
                                                                            prev === transaction.id ? null : transaction.id
                                                                        );
                                                                    }
                                                                }}
                                                                style={
                                                                    type === "CHARGE"
                                                                        ? { cursor: "pointer", ...(refundedStyle || {}) }
                                                                        : refundedStyle
                                                                }
                                                            >
                                                                {transaction.description}
                                                            </M.PointTransactionTdTitle>
                                                            <M.PointTransactionTdAmount style={refundedStyle}>
                                                                {transaction.point}P
                                                            </M.PointTransactionTdAmount>
                                                            <M.PointTransactionTdBalance style={refundedStyle}>
                                                                {formatNumber(transaction.balance)}P
                                                            </M.PointTransactionTdBalance>
                                                        </M.PointTransactionTbodyTr>
                                                        {isOpen && (
                                                            <M.PointTransactionDetailTr>
                                                                <M.PointTransactionDetailTd colSpan={4}>
                                                                    <M.PointTransactionDetailContent>
                                                                        충전 금액: {formatNumber(transaction.amount)}원
                                                                    </M.PointTransactionDetailContent>
                                                                    <M.PointTransactionDetailContent>
                                                                        충전 후 잔액: {formatNumber(transaction.balance)}P
                                                                    </M.PointTransactionDetailContent>
                                                                    <M.PointTransactionDetailContent>
                                                                        결제 수단: {transaction.paymentMethod}
                                                                    </M.PointTransactionDetailContent>
                                                                    <M.PointTransactionDetailContent>
                                                                        {isRefunded
                                                                            ? `결제 취소일: ${formatDateAndTime(baseDate)}`
                                                                            : `충전일: ${formatDateAndTime(baseDate)}`}
                                                                    </M.PointTransactionDetailContent>
                                                                    <M.RefundBtnWrapper>
                                                                        {isRefundAvailable && !isRefunded && (
                                                                            <M.RefundBtn
                                                                                type="button"
                                                                                onClick={handleRefundClick}
                                                                            >
                                                                                결제 취소
                                                                            </M.RefundBtn>
                                                                        )}
                                                                    </M.RefundBtnWrapper>
                                                                </M.PointTransactionDetailTd>
                                                            </M.PointTransactionDetailTr>
                                                        )}
                                                    </React.Fragment>
                                                );
                                            })}
                                        </M.PointTransactionTableBody>
                                    </M.PointTransactionTable>
                                )}
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