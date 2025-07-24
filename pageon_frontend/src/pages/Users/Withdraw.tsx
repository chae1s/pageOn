import React, {useState, useEffect} from "react";
import "../../styles/reset.css"
import "../../styles/global.css"
import { UserProfile, DeleteRequest } from "../../types/User";
import axios from "axios";
import Sidebar from "../../components/MyPageSidebar";
import "./MyPage.css"

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
        <div className="main-container">
            <main className="sidebar-main">
                <Sidebar />
                <div className="sidebar-right-wrap withdraw">
                    <h2 className="mypage-title">회원탈퇴</h2>
                    <div className="withdraw-warning">
                        <span className="withdraw-email">{userInfo?.email}</span><span className="unit">님</span>
                        <p>회원탈퇴 시 다음 사항을 확인해주세요. : </p>
                        <ul>
                            <li>• 모든 개인정보와 서비스 이용 기록이 삭제됩니다.</li>
                            <li>• 보유한 포인트와 쿠폰이 모두 소멸됩니다.
                                <div className="withdraw-point-info">
                                    <span>현재 내 잔여 포인트: </span>
                                    <span className="withdraw-point-value">{userInfo?.pointBalance} P</span>
                                </div>
                            </li>
                            <li>• 탈퇴 후에는 복구할 수 없습니다.</li>
                            <li>• 탈퇴를 원하시면 비밀번호를 입력해주세요.</li>
                        </ul>
                    </div>
                    <form onSubmit={handleSumbit}>
                        <div className="withdraw-reason-section">
                            <p className="withdraw-reason-title">탈퇴하는 이유를 말해주세요 :</p>
                            <div className="withdraw-reason-options">
                                {withdrawReasons.map((reason, index) => (
                                    <label key={index} className="withdraw-reason-option">
                                        <input 
                                            type="radio"
                                            name="withdrawReason"
                                            value={index}
                                            onChange={handleReasonChange}
                                        />
                                        <span className="withdraw-reason-text">{reason}</span>
                                        {reason === "기타" && (
                                            <input 
                                                type="text" 
                                                className="withdraw-other-reason" 
                                                value={deleteData.otherReason}
                                                placeholder="기타 이유를 입력해주세요."
                                                onChange={handleOtherReasonChange}
                                            />
                                        )}
                                    </label>
                                ))}
                            </div>
                        </div>
                        <div className="password-check-form">
                            {userInfo?.oauthProvider === "EMAIL" && (
                                <div className="password-check-form-group">
                                    <label htmlFor="">비밀번호</label>
                                    <input 
                                        type="password" 
                                        id="password"
                                        value={deleteData.password}
                                        className="password-check-input"
                                        onChange={handlePasswordChange}
                                    />
                                    {error && 
                                        <div className="error-message">{error}</div>
                                    }
                                </div>
                            )}
                            <button type="submit" className="submit-btn">탈퇴하기</button>
                        </div>
                    </form>
                </div>
            </main>
        </div>
    )

}

export default Withdraw