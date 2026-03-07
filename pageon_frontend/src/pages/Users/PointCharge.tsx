import { useEffect, useState } from "react";
import { MainContainer, SidebarMain } from "../../styles/Layout.styles";
import * as S from "./MyPage.styles";
import Sidebar from "../../components/Sidebars/MyPageSidebar";
import api from "../../api/axiosInstance";
import { loadTossPayments } from "@tosspayments/tosspayments-sdk";

type ChargeOption = {
    id: number;
    amount: number; // 원
    point: number;  // P
};

const chargeOptions: ChargeOption[] = [
    { id: 1, amount: 1000, point: 1000 },
    { id: 2, amount: 3000, point: 3000 },
    { id: 3, amount: 5000, point: 5000 },
    { id: 4, amount: 10000, point: 10000 },
    { id: 5, amount: 30000, point: 30000 },
    { id: 6, amount: 50000, point: 50000}, 
    { id: 7, amount: 100000, point: 100000}
];

const clientKey = "test_ck_KNbdOvk5rkyyQRN2PqNy3n07xlzm";

function PointCharge() {

    const [myPoint, setMyPoint] = useState(0);

    useEffect(() => {
        async function fetchData() {
            try {
                const response = await api.get(`/users/points/me`);

                setMyPoint(response.data);
            } catch (error) {
                console.error("내 포인트 조회 실패: ", error);
            }
            
        }

        fetchData();
    }, []);
    const [selectedId, setSelectedId] = useState<number | null>(chargeOptions[0]?.id ?? null);

    const selectedOption = chargeOptions.find((opt) => opt.id === selectedId) || null;

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!selectedOption) return;

        try {
            const response = await api.post("/payments/ready", {
                amount: selectedOption.amount,
                description: `pageon ${selectedOption.point.toLocaleString()}포인트 충전`
            })

            const data = response.data;
            
            const tossPayments = await loadTossPayments(clientKey);
            const payment = tossPayments.payment({
                customerKey: data.customerKey, 
            })

            await payment.requestPayment({
                method: "CARD", 
                amount: {
                    currency: "KRW", 
                    value: data.amount
                }, 
                orderId: data.orderId,
                orderName: `pageon ${selectedOption.point.toLocaleString()}포인트 충전`, 
                successUrl: `${window.location.origin}/payment/success`,
                failUrl: `${window.location.origin}/payment/fail`,

                card: {
                    useEscrow: false,
                    flowMode: "DEFAULT", // 통합결제창 여는 옵션
                    useCardPoint: false,
                    useAppCardOnly: false,
                },

                
            })
            
        } catch (err) {
            console.error("결제 중 에러 발생", err)
        }

    };



    return (
        <MainContainer>
            <SidebarMain>
                <Sidebar />
                <S.SidebarRightWrap>
                    <S.MypageTitle>포인트 충전</S.MypageTitle>
                    <S.PointChargeSection>
                        <S.MyPointText>
                            내 포인트
                            <S.MyPoint>
                                {myPoint.toLocaleString()}P
                            </S.MyPoint>
                        </S.MyPointText>
                        <form onSubmit={handleSubmit}>
                            <S.PointChargeTable>
                                <colgroup>
                                    <S.PointChargeColAmount style={{ width: "40%" }} />
                                    <col></col>
                                    <S.PointChargeColPoint style={{ width: "40%" }} />
                                </colgroup>
                                <S.PointChargeTableHead>
                                    <S.PointChargeTheadTr>
                                        <S.PointChargeThAmount>충전 금액</S.PointChargeThAmount>
                                        <th></th>
                                        <S.PointChargeThPoint>적립 포인트</S.PointChargeThPoint>
                                    </S.PointChargeTheadTr>
                                </S.PointChargeTableHead>
                                <S.PointChargeTableBody>
                                    {chargeOptions.map((option) => {
                                        const isSelected = selectedId === option.id;
                                        return (
                                            <S.PointChargeTbodyTr
                                                key={option.id}
                                                onClick={() => setSelectedId(option.id)}
                                                style={{ cursor: "pointer" }}
                                            >
                                                <S.PointChargeTdAmount>
                                                    <input
                                                        type="radio"
                                                        name="chargeOption"
                                                        value={option.id}
                                                        checked={isSelected}
                                                        onChange={() => setSelectedId(option.id)}
                                                        style={{ marginRight: 8 }}
                                                    />
                                                    {option.amount.toLocaleString()}원
                                                </S.PointChargeTdAmount>
                                                <td></td>
                                                <S.PointChargeTdPoint>
                                                    {option.point.toLocaleString()}P
                                                </S.PointChargeTdPoint>
                                            </S.PointChargeTbodyTr>
                                        );
                                    })}
                                </S.PointChargeTableBody>
                            </S.PointChargeTable>

                            {selectedOption && (
                                <S.PointChargeSummary>
                                    <span>결제 금액</span>
                                    <S.PointChargeSummaryValue>
                                        {selectedOption.amount.toLocaleString()}원
                                    </S.PointChargeSummaryValue>
                                </S.PointChargeSummary>
                            )}
                            <S.PointChargeBtnWrapper>
                                <S.PointChargeBtn type="submit" disabled={!selectedOption}>
                                    충전하기
                                </S.PointChargeBtn>
                            </S.PointChargeBtnWrapper>
                        </form>
                    </S.PointChargeSection>
                </S.SidebarRightWrap>
            </SidebarMain>
        </MainContainer>
    );
}

export default PointCharge;