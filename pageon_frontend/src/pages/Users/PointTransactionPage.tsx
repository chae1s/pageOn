import React, { useEffect, useState } from "react";
import { MainContainer, SidebarMain, SortBtn } from "../../styles/Layout.styles";
import Sidebar from "../../components/Sidebars/MyPageSidebar";
import * as M from "./MyPage.styles"
import { useSearchParams } from "react-router-dom";
import api from "../../api/axiosInstance";
import { PointTransaction } from "../../types/User";
import dayjs from "dayjs";
import { formatDateAndTime, formatNumber } from "../../utils/formatData";

function PointTransactionPage() {
    const [searchParams, setSearchParams] = useSearchParams();

    const type = searchParams.get("type") || "USE";
    const page = parseInt(searchParams.get("page") || "0", 10);

    const [pointTransactions, setPointTransactions] = useState<PointTransaction[]>([]);

    
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

            } catch (error) {
                console.log("포인트 내역 조회 실패: ", error);
            }
        }

        fetchData();
    }, [type]);

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
                        </M.PointTransactionList>
                    </M.MypagePointSection>
                </M.SidebarRightWrap>
            </SidebarMain>
        </MainContainer>
    )
}

export default PointTransactionPage;