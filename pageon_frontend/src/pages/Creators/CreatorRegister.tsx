import React, {useState, useEffect, useRef} from "react";
import "../../styles/reset.css"
import "../../styles/global.css"
import { useNavigate, Link} from "react-router-dom";
import { RegisterRequest } from "../../types/Creator";
import axios from "axios";
import "./CreatorRegister.css"

function CreatorRegister() {
    const [isVerified, setIsVerified] = useState<boolean>(false);
    const popupRef = useRef<Window | null>(null);
    const popupCheckInterval = useRef<NodeJS.Timeout | null>(null);
    const hasAlerted = useRef(false);

    const navigate = useNavigate();

    useEffect(() => {
        const checkVerification = async () => {
            try {
                const response = await axios.get("/api/users/check-identity", {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                    },
                    withCredentials: true
                })

                setIsVerified(true)
            } catch (error: any) {
                if (error.response && error.response.status === 403) {
                    alert("작가 등록을 위해 본인인증이 필요합니다.");
                    openVerificationPopup();
                } else {
                    console.error("본인인증 확인 중 오류: ", error)
                }
            }
        };
        checkVerification();
    }, []);

    const openVerificationPopup = () => {
        popupRef.current = window.open(
            "/mock-verify",
            "identityVerification",
            "width=480,height=600,left=300,top=200"
        );

        // 팝업 닫힘 감지
        popupCheckInterval.current = setInterval(() => {
            if (popupRef.current && popupRef.current.closed) {
                clearInterval(popupCheckInterval.current!);
                if (!isVerified && !hasAlerted.current ) {
                    hasAlerted.current = true;
                    alert("본인인증을 완료해야 작가 등록을 진행할 수 있습니다.");
                    // 필요시 navigate("/") 등으로 리디렉션 가능
                    navigate("/users/my-page")
                }
            }
        }, 500);
    
    };

    useEffect(() => {
        const handleMessage = (e: MessageEvent) => {
            console.log(e.data.verified)
            if (e.data.verified) {
                alert("본인인증이 완료되었습니다.");
                window.location.reload();
                setIsVerified(true)
            }
        };
        window.addEventListener("message", handleMessage);
        return () => window.removeEventListener("message", handleMessage)
    }, [])

    const [formData, setFormData] = useState<RegisterRequest>({
        penName: "",
        contentType: "",
        agreedToAiPolicy: false
    })

    type ErrorMap = Record<string, string>;
    const [errors, setErrors] = useState<ErrorMap>({});
    const [getClassName, setGetClassName] = useState<string>("");

    const handlePenNameChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const newPenName = e.target.value;

        if (!newPenName) {
            setErrors(prev => ({
                ...prev, 
                penName: "필명을 입력해주세요."
            }))
            setFormData(prev => ({
                ...prev,
                penName: ""
            }))
            setGetClassName("input-error")
        } else if(newPenName.length < 2) {
            setErrors(prev => ({
                ...prev, 
                penName: "필명은 두 글자 이상으로 입력해주세요."
            }))
            setFormData(prev => ({
                ...prev,
                penName: newPenName
            }))
            setGetClassName("input-error")
        } else {
            setErrors(prev => {
                const newErrors = {...prev}
                delete newErrors.penName;

                return newErrors
            })
            setFormData(prev => ({
                ...prev,
                penName: newPenName
            }))
            setGetClassName("input-success")
        }
    }

    const handleContentTypeChange = (selectContentType: "WEBTOON" | "WEBNOVEL") => {
        setFormData(prev => ({
            ...prev,
            contentType: prev.contentType === selectContentType ? "" : selectContentType
        }))
    }

    const handleTermsChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {checked} = e.target;
        setFormData(prev => ({
            ...prev,
            agreedToAiPolicy: checked
        }))
    }

    const isFormValid = formData.penName && formData.penName.length > 1 && formData.contentType && formData.agreedToAiPolicy

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (Object.keys(errors).length > 0) return;

        try {
            const response = await axios.post("/api/creators/register", formData, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                },
                withCredentials: true
            });
            console.log("작가 등록 성공: ", response.data);
            alert("작가 등록이 완료되었습니다.");
            navigate("/creators")
        } catch (error) {
            alert("작가 등록에 실패했습니다. 다시 시도해주세요.")
            console.error(error)
        }
    }

    return (
        <div className="main-container">
            <main className="no-sidebar-main">
                <div className="creator-form-wrapper">
                    <h1 className="creator-title">작가 등록</h1>
                    <form action="" className="creator-form" onSubmit={handleSubmit}>
                        <div className="creator-form-group">
                            <label htmlFor="">작가 필명</label>
                            <input 
                                type="text"
                                id="penName"
                                name="penName"
                                className={`form-input ${getClassName}`}
                                value={formData.penName}
                                placeholder="필명을 입력해주세요."
                                onChange={handlePenNameChange}
                            />
                            {errors.penName && (
                                <p className="error-message">{errors.penName}</p>
                            )}
                        </div>
                        <div className="users-form-gender-checkbox">
                            <div className="users-form-group-gender-label">컨텐츠</div>
                            <div className="users-form-group-gender">
                                <label className={`users-form-left ${formData.contentType === "WEBTOON" ? "selected" : ""}`}>
                                    <input 
                                        type="checkbox"
                                        name="contentType"
                                        className="form-input"
                                        onChange={() => handleContentTypeChange("WEBTOON")}
                                        checked={formData.contentType === "WEBTOON"}
                                    />
                                    <span>웹툰</span>
                                </label>
                                <label className={`users-form-right ${formData.contentType === "WEBNOVEL" ? "selected" : ""}`}>
                                    <input 
                                        type="checkbox"
                                        name="contentType"
                                        className="form-input"
                                        onChange={() => handleContentTypeChange("WEBNOVEL")}
                                        checked={formData.contentType === "WEBNOVEL"}
                                    />
                                    <span>웹소설</span>
                                </label>
                            </div>
                        </div>
                        <div className="users-terms creator-terms">
                            <div className="terms-content">
                                [창작자 등록 약관]<br /><br />

                                본인은 pageOn의 창작자로 등록함에 있어, 아래 사항을 충분히 이해하고 동의합니다.<br /><br />

                                제1조 (AI 생성 콘텐츠의 고지 및 책임)<br /><br />

                                1. 본 플랫폼은 인공지능(AI) 도구(예: ChatGPT, MidJourney 등)를 활용하여 생성된 콘텐츠의 등록을 허용하고 있습니다.<br /><br />

                                2. AI 생성 콘텐츠는 현행법상 저작권 보호 대상이 아닐 수 있으며, AI가 전적으로 생성한 작품은 타인에 의해 무단으로 사용되더라도 법적 보호에 한계가 있습니다.<br /><br />

                                3. 창작자는 콘텐츠 등록 시 반드시 아래 제작 유형 중 하나를 선택해야 하며, 이에 따라 수익 분배 기준이 달라집니다:<br /><br />
                                <br /><br />
                                - 100% 직접 창작: 창작자 수익 70%<br />
                                - AI 보조 창작(일부 편집·창작성 포함): 창작자 수익 50%<br />
                                - 100% AI 자동 생성: 창작자 수익 20%<br /><br />

                                4. 창작자가 제작 유형을 고의적으로 허위 선택한 경우, 회사는 해당 콘텐츠를 삭제하거나 수익 지급을 보류할 수 있습니다.<br /><br />

                                5. AI 생성 콘텐츠와 관련된 저작권 분쟁, 도용, 명예훼손 등 법적 책임은 콘텐츠 등록자인 창작자 본인에게 있습니다.<br /><br />

                                ---<br /><br />

                                제2조 (개인정보 수집 및 수익 정산 관련)<br /><br />

                                1. 회사는 창작자의 수익금을 지급하기 위해 아래와 같은 개인정보를 수집·이용할 수 있습니다:<br /><br />

                                - 예금주 이름  <br /><br />
                                - 은행명 및 계좌번호  <br /><br />
                                - 주민등록번호(또는 외국인의 경우 외국인등록번호)  <br /><br />
                                - 수익 정산을 위한 연락처 및 이메일<br /><br />

                                2. 수집된 정보는 **수익금 이체 및 세무 처리 목적**으로만 사용되며, 목적 달성 후 일정 기간 보관 후 안전하게 파기됩니다.<br /><br />

                                3. 회사는 전자적 방법으로 수익을 지급하며, 세금 공제 또는 수수료가 발생할 수 있습니다.<br /><br />

                                ---<br /><br />

                                제3조 (약관 동의의 효력 및 책임)<br /><br />

                                1. 본 약관에 동의하지 않을 경우 창작자 등록이 제한될 수 있습니다.<br /><br />

                                2. 창작자가 본 약관에 동의한 후 등록한 콘텐츠에 대해 발생하는 모든 책임은 창작자 본인에게 귀속됩니다.<br /><br />

                                3. 창작자는 수익 분배 구조, 개인정보 처리 방침, AI 콘텐츠의 저작권 한계에 대해 충분히 이해하고 동의합니다.<br /><br />

                                ---
                            </div>
                            <label htmlFor="termsAgreed" className="terms-label">
                                <input 
                                    type="checkbox" 
                                    id="termsAgreed"
                                    name="termsAgreed"
                                    checked = {formData.agreedToAiPolicy}
                                    required
                                    onChange={handleTermsChange}
                                />
                                <span className="term-message">본 이용약관 및 AI 콘텐츠 관련 고지사항을 <br />모두 확인하였으며, 이에 동의합니다.</span>
                            </label>
                        </div>
                        <button className="submit-btn creator-btn" type="submit" disabled={!isFormValid}>
                            작가 등록
                        </button>
                    </form>
                </div>
            </main>
        </div>
    )
}

export default CreatorRegister