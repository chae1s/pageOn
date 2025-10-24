import { useState, useEffect } from "react";
import "./MockVerify.css"
import axios from "axios";

const MockVerify = () => {
  const [step, setStep] = useState<"form" | "code" | "done">("form");
  const [formData, setFormData] = useState({
    name: "",
    birthdate: "",
    gender:"",
    phone: "",
    code: "",
  });

  const [identityVerificationId, setIdentityVerificationId] = useState<string>("");
  const [seconds, setSeconds] = useState(180);

  useEffect(() => {
    if (step !== "code") return;
    setSeconds(180); // reset timer when entering code step
  }, [step]);

  useEffect(() => {
    if (step !== "code" || seconds <= 0) return;
    const timer = setInterval(() => setSeconds(s => s - 1), 1000);
    return () => clearInterval(timer);
  }, [step, seconds]);

 

  // 시간 표시용
  const min = String(Math.floor(seconds / 60));
  const sec = String(seconds % 60).padStart(2, "0");

  useEffect(() => {
    const fetchData = async () => {
        try {
            const response = await axios.post("/api/identity-verifications",{}, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                },
                withCredentials: true
            })

            const id = response.data.identityVerificationId
            console.log(response.data.identityVerificationId)
            setIdentityVerificationId(id)
        } catch (error) {
            console.error("본인인증 ID 발급 실패", error);
        }
    };
    fetchData();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleRequestCode = async () => {
    if (formData.name && formData.birthdate && formData.phone && formData.gender) {
        const identityNum = `${formData.birthdate.trim()}${formData.gender.trim()}`
        try {
            await axios.post(`/api/identity-verifications/${identityVerificationId}/send`, 
                {
                    customer: {
                        name: formData.name,
                        phoneNumber: formData.phone,
                        identityNumber: identityNum
                    },
                    method: "SMS"
                }, {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                    },
                    withCredentials: true
                }
            );
            setStep("code");
            console.log(formData)
            setSeconds(180); // 재전송 성공 시 타이머 초기화
        } catch (error) {
            console.error("인증번호 전송 실패", error);
            alert("인증번호 요청에 실패했습니다.");
        }
      
    } else {
      alert("모든 정보를 입력해주세요.");
    }
  };

  const handleVerifyCode = async () => {
    const randomDi = Math.random().toString(36).substring(2, 20);
    if (!formData.code) {
        alert("인증번호를 입력해주세요.")
        return;
    }

    try {
        await axios.post(`/api/identity-verifications/${identityVerificationId}/confirm`, {
            di: randomDi,
            otp: formData.code,
            identityProvider: "DANAL"
        }, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("accessToken")}`
            },
            withCredentials: true
        })

        setStep("done")
        window.opener.postMessage({ verified: true }, "*");
        window.close();
    } catch (error) {
        console.error("인증번호 일치 실패", error);
        alert("인증번호가 일치하지 않습니다.");
    }
    
  };

  return (
    <div style={{ padding: 20, fontFamily: "sans-serif" }}>
      <h2>본인인증</h2>
      {step === "form" && (
        <form className="verify-form" onSubmit={e => { e.preventDefault(); handleRequestCode(); }}>
          <div className="verify-group">
            <label className="verify-label">이름</label>
            <input name="name" placeholder="성명입력" className="verify-input" onChange={handleChange} autoComplete="off" />
          </div>
          <div className="verify-group">
            <label className="verify-label">생년월일/성별</label>
            <div className="verify-birth-gender-row">
              <input name="birthdate" placeholder="●●●●●●" className="verify-input birth-input" maxLength={6} onChange={handleChange} autoComplete="off" />
              <span className="verify-birth-dash">-</span>
              <input name="gender" placeholder="●" className="verify-input gender-input" maxLength={1} onChange={handleChange} autoComplete="off" />
              <span>●●●●●●</span>
            </div>
          </div>
          <div className="verify-group">
            <label className="verify-label">휴대폰번호</label>
            <div className="verify-phone-row">
              <select name="carrier" className="verify-carrier-select">
                <option value="">통신사 선택</option>
                <option value="SKT">SKT</option>
                <option value="KT">KT</option>
                <option value="LGU+">LG U+</option>
                <option value="알뜰폰">알뜰폰</option>
              </select>
              <input name="phone" placeholder="01000000000" className="verify-input" onChange={handleChange} autoComplete="off" />
            </div>
          </div>
          <div className="verify-btn-row">
            <button type="submit" className="verify-btn confirm">확인</button>
          </div>
        </form>
      )}
      {step === "code" && (
        <form className="verify-code-form" onSubmit={e => { e.preventDefault(); handleVerifyCode(); }}>
          <div className="verify-group">
            <label className="verify-label">인증번호</label>
            <input name="code" placeholder="숫자 6자리 입력" className="verify-input code-input" maxLength={6} onChange={handleChange} autoComplete="off" />
          </div>
          <div className="verify-code-row">
            <span className="verify-timer-label">남은시간</span>
            <span className="verify-timer">{min}:{sec}</span>
            <button type="button" className="verify-timer-btn" onClick={handleRequestCode}>재전송</button>
          </div>
          <div className="verify-btn-row">
            <button type="button" className="verify-btn cancel">취소</button>
            <button type="submit" className="verify-btn confirm">인증확인</button>
          </div>
        </form>
      )}
    </div>
  );
};

export default MockVerify;
