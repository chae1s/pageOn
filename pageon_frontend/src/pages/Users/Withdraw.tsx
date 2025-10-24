import React, {useState, useEffect} from "react";
import { styled } from "styled-components";
import { MainContainer, SidebarMain } from "../../styles/Layout.styles";
import * as M from "./MyPage.styles"
import { UserProfile, DeleteRequest } from "../../types/User";
import axios from "axios";
import Sidebar from "../../components/Sidebars/MyPageSidebar";

const WithdrawWarning = styled.div`
    padding: 20px;
    margin-bottom: 10px;
`

const WithdrawEmail = styled.span`
    color: #528efa !important;
    font-size: 20px;
    font-weight: 600;
    color: #444;
    margin-bottom: 10px;
    margin-top: 0;
    display: inline-block;
`

const WithdrawEmailUnit = styled.span`
    margin-left: 5px;
    font-weight: 600;
    color: #444;
    margin-bottom: 10px;
    margin-top: 0;
    display: inline-block;
`

const WithdrawCheckMsg = styled.p`
    font-weight: 600;
    color: #444;
    margin-bottom: 12px;
    margin-top: 0;
`

const WithdrawCheckUl = styled.ul`
    margin: 0;
    padding-left: 20px;
`

const WithdrawCheckLi = styled.li`
    color: #444;
    margin-bottom: 8px;
    font-size: 0.95rem;
    line-height: 1.4;
`

const WithdrawMyPointInfo = styled.div`
    margin-top: 5px;
    margin-left: 20px;
    font-size: 0.9rem;
    color: #444;
`

const WithDrawMyPointText = styled.span`
    color: #FC5858;
    font-weight: 600;
`

const WithdrawMyPointValue = styled.span`
    color: #FC5858;
    font-weight: 600;
    font-size: 16px;
`

const WithdrawReasonSection = styled.div`
    padding: 20px;
`

const WithdrawReasonTitle = styled.p`
  font-weight: 600;
  color: #444;
  margin-bottom: 12px;
  margin-top: 0;
`

const WithdrawReasonOptions = styled.div`
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-left: 20px;
`

const WithdrawReasonLabel = styled.label`
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 0;
`

const WithdrawReasonInput = styled.input`
    width: 13px;
    height: 13px;
    cursor: pointer;
`

const WithdrawReasonText = styled.span`
    font-size: 0.95rem;
    color: #444;
    cursor: pointer;
    line-height: 1.4;
`

const WithdrawOtherReason = styled.input`
    width: 300px;
    padding: 4px 0;
    border: none;
    border-bottom: 1px solid #ddd;
    font-size: 0.9rem;
    margin-left: 8px;
    background: none;
`

const WithdrawPasswordCheck = styled.div`
    max-width: 400px;
    margin: 30px auto 0;
    padding: 32px;
`


function Withdraw() {
    type WithdrawUserInfo = Pick<UserProfile, "id" | "email" | "pointBalance" | "oauthProvider">;
    const [error, setError] = useState<string>("");
    const [userInfo, setUserInfo] = useState<WithdrawUserInfo>({
        id: 0,
        email: "",
        pointBalance: 0,
        oauthProvider: ""
    })

    const [deleteData, setDeleteData] = useState<DeleteRequest>({
        password: "",
        reasonIndex: -1,
        reason: "",
        otherReason: ""
    })

    useEffect(() => {
        async function fetchData() {
            try {
                const response = await axios.get("/api/users/me", {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                    },
                    withCredentials: true
                });
    
                setUserInfo(response.data);
                console.log(userInfo)
            } catch (error) {
                alert("사용자 정보를 불러오지 못했습니다.");
            }
        } ;
        fetchData();
    }, []);

    const withdrawReasons = [
        "원하는 작품이 부족해서",
        "회원 혜택이 부족해서",
        "시스템 오류가 잦아서",
        "불만, 불편 사항에 대한 응대가 늦어서",
        "자주 사용하지 않아서",
        "개인 정보 및 보안이 우려되어서",
        "중복 가입으로 계정 정리가 필요해서",
        "기타"
    ]

    const handleReasonChange = (e:React.ChangeEvent<HTMLInputElement>) => {
        const reasonIdx = Number(e.target.value)
        setDeleteData(prev => ({
            ...prev,
            reasonIndex: reasonIdx
        }))

        if (reasonIdx < 7) {
            setDeleteData(prev => ({
                ...prev,
                reason: withdrawReasons[reasonIdx],
                otherReason: ""
            }))
        } else {
            setDeleteData(prev => ({
                ...prev,
                reasonIndex: reasonIdx,
                reason: ""
            }))
        }
    }

    const handleOtherReasonChange = (e:React.ChangeEvent<HTMLInputElement>) => {
        const reason = e.target.value;

        setDeleteData(prev => ({
            ...prev,
            otherReason: reason
        }))
    }

    const handlePasswordChange = (e:React.ChangeEvent<HTMLInputElement>) => {
        const checkPassword = e.target.value;
        
        setDeleteData(prev => ({
            ...prev,
            password: checkPassword
        })) 
    }

    const handleSumbit = async (e:React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (userInfo?.oauthProvider === "EMAIL" && !deleteData.password) {
            setError("비밀번호를 입력해주세요.");
            return;
        }

        if (deleteData.reasonIndex === -1) {
            alert("탈퇴 이유를 선택해주세요.");
            return;
        }

        if (deleteData.reasonIndex === 7 && !deleteData.otherReason) {
            alert("기타 이유를 입력해주세요.");
            return;
        }

        const isConfirmed = window.confirm(
            "정말로 회원탈퇴를 하시겠습니까? \n 탈퇴 후에는 모든 데이터가 삭제되며 복구할 수 없습니다."
        );

        if (!isConfirmed) return;

        try {
            const response = await axios.post("/api/users/withdraw", deleteData, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                },
                withCredentials: true
            })
    
            if (response.data.isDeleted) {
                alert(response.data.message);
    
                localStorage.removeItem("accessToken");
    
                window.location.href = "/";
            } else {
                setError(response.data.message)
            }
        } catch (err) {
            setError("회원탈퇴 중 오류가 발생했습니다.")
        }
    }


    return (
        <MainContainer>
            <SidebarMain>
                <Sidebar />
                <M.SidebarRightWrap>
                    <M.MypageTitle>회원탈퇴</M.MypageTitle>
                    <WithdrawWarning>
                        <WithdrawEmail>{userInfo?.email}</WithdrawEmail><WithdrawEmailUnit>님</WithdrawEmailUnit>
                        <WithdrawCheckMsg>회원탈퇴 시 다음 사항을 확인해주세요. : </WithdrawCheckMsg>
                        <WithdrawCheckUl>
                            <WithdrawCheckLi>• 모든 개인정보와 서비스 이용 기록이 삭제됩니다.</WithdrawCheckLi>
                            <WithdrawCheckLi>• 보유한 포인트와 쿠폰이 모두 소멸됩니다.
                                <WithdrawMyPointInfo>
                                    <WithDrawMyPointText>현재 내 잔여 포인트: </WithDrawMyPointText>
                                    <WithdrawMyPointValue>{userInfo?.pointBalance} P</WithdrawMyPointValue>
                                </WithdrawMyPointInfo>
                            </WithdrawCheckLi>
                            <WithdrawCheckLi>• 탈퇴 후에는 복구할 수 없습니다.</WithdrawCheckLi>
                            <WithdrawCheckLi>• 탈퇴를 원하시면 비밀번호를 입력해주세요.</WithdrawCheckLi>
                        </WithdrawCheckUl>
                    </WithdrawWarning>
                    <form onSubmit={handleSumbit}>
                        <WithdrawReasonSection>
                            <WithdrawReasonTitle>탈퇴하는 이유를 말해주세요 :</WithdrawReasonTitle>
                            <WithdrawReasonOptions>
                                {withdrawReasons.map((reason, index) => (
                                    <WithdrawReasonLabel key={index}>
                                        <WithdrawReasonInput 
                                            type="radio"
                                            name="withdrawReason"
                                            value={index}
                                            onChange={handleReasonChange}
                                        />
                                        <WithdrawReasonText>{reason}</WithdrawReasonText>
                                        {reason === "기타" && (
                                            <WithdrawOtherReason 
                                                type="text" 
                                                value={deleteData.otherReason}
                                                placeholder="기타 이유를 입력해주세요."
                                                onChange={handleOtherReasonChange}
                                            />
                                        )}
                                    </WithdrawReasonLabel>
                                ))}
                            </WithdrawReasonOptions>
                        </WithdrawReasonSection>
                        <WithdrawPasswordCheck>
                            {userInfo?.oauthProvider === "EMAIL" && (
                                <div>
                                    <M.PasswordCheckLabel htmlFor="">비밀번호</M.PasswordCheckLabel>
                                    <M.PasswordCheckInput 
                                        type="password" 
                                        id="password"
                                        value={deleteData.password}
                                        onChange={handlePasswordChange}
                                    />
                                    {error && 
                                        <M.ErrorMessage>{error}</M.ErrorMessage>
                                    }
                                </div>
                            )}
                            <M.SubmitBtn type="submit">탈퇴하기</M.SubmitBtn>
                        </WithdrawPasswordCheck>
                    </form>
                </M.SidebarRightWrap>
            </SidebarMain>
        </MainContainer>
    )

}

export default Withdraw