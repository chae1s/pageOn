import React, {useState} from "react";
import "../../styles/reset.css"
import "../../styles/global.css"
import { useNavigate, Link} from "react-router-dom";
import axios from "axios";
import Sidebar from "../../components/MyPageSidebar";
import "./MyPage.css"

function PasswordCheck() {
    const [password, setPassword] = useState<string>("");
    const [error, setError] = useState<string>("");
    const navigate = useNavigate();

    const handleChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;

        setPassword(value);
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!password) {
            setError("비밀번호를 입력하세요.");
            return;
        }

        try {
            const response = await axios.post("/api/users/check-password", {password: password}, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                }
            });

            console.log(response.data)

            if (response.data.isCorrect) {
                sessionStorage.setItem("passwordVerified", "true");
                navigate("/users/edit");
            } else {
                setError("비밀번호가 일치하지 않습니다.");
            }
        } catch (err) {
            setError("비밀번호 확인 중 오류가 발생했습니다.");
        }
    };


    return (
        <div className="main-container">
            <main className="sidebar-main">
                <Sidebar />
                <div className="sidebar-right-wrap">
                    <h2 className="mypage-title">비밀번호 재확인</h2>
                    <form className="password-check-form" onSubmit={handleSubmit}>
                        <div className="password-check-form-group">
                            <label htmlFor="password">비밀번호</label>
                            <input 
                                id="password"
                                type="password"
                                value={password}
                                className="password-check-input"
                                onChange={handleChange}
                                autoFocus
                            />
                            {error && 
                                <div className="error-message">{error}</div>
                            }
                            <button type="submit" className="submit-btn">확인</button>
                        </div>
                    </form>
                </div>
            </main>

        </div>
    )
}

export default PasswordCheck