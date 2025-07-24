import React, { useState } from "react";
import "../../styles/reset.css"
import "../../styles/global.css"
import "./Users.css"
import { useNavigate, Link} from "react-router-dom";
import axios from "axios";

function PasswordFind() {
    const [email, setEmail] = useState<string>("");
    const navigate = useNavigate();
    const [error, setError] = useState<string>("");
    const [successMsg, setSuccessMsg] = useState<string>("");

    const handleChange = (e:React.ChangeEvent<HTMLInputElement>) => {
        setEmail(e.target.value);
        setError("");
        setSuccessMsg("");
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        setError("");
        setSuccessMsg("");

        try {
            const response = await axios.post("/api/users/find-password", {
                email: email
            });
    
            const {type} = response.data;
            const {message} = response.data;
    
            if (type === "email") {
                setSuccessMsg(message);
            } else if (type === "social") {
                setError(message);
            } else if (type === "noUser") {
                setError(message);
            } else {
                setError("비밀번호 찾기에 실패하였습니다. 다시 시도해주세요.");
            }
        } catch (err) {
            setError("비밀번호 찾기에 실패하였습니다. 다시 시도해주세요.");
        }
    }

    return(
        <div className="main-container">
            <main className="no-sidebar-main">
                <div className="users-form-wrapper">
                    <h1 className="users-title">비밀번호 찾기</h1>
                    <form className="users-form">
                        <div className="users-form-group">
                            <label htmlFor="email">이메일</label>
                            <input 
                                type="email"
                                id="email"
                                name="email"
                                value={email}
                                placeholder="이메일을 입력해주세요."
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div>
                            {error && (
                                <p className="error-message" style={{ marginTop: "8px" }}>
                                    {error}
                                </p>
                            )}
                            {successMsg && (
                                <p className="success-message" style={{ marginTop: "8px", color: "#528efa" }}>
                                    {successMsg}
                                </p>
                            )}
                        </div>
                        <button type="submit" className="submit-btn" style={{marginTop: "16px"}}>
                            비밀번호 찾기
                        </button>
                    </form>
                    <div className="users-link" style={{marginTop: "24px"}}>
                        <Link to={"/users/login"}>로그인으로 돌아가기</Link>
                    </div>
                </div>
            </main>

        </div>
    ) 
}

export default PasswordFind