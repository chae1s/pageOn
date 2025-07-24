import React, {useState, useEffect} from "react";
import "../../styles/reset.css"
import "../../styles/global.css"
import { UserProfile, UpdateRequest } from "../../types/User";
import { useNavigate, Link} from "react-router-dom";
import axios from "axios";
import Sidebar from "../../components/MyPageSidebar";
import "./MyPage.css"

function EditProfile() {
    type ErrorMap = Record<string, string>;

    const navigate = useNavigate();
    const [errors, setErrors] = useState<ErrorMap>({});
    const [successMessage, setSuccessMessage] = useState<string>("");
    const [isNicknameDuplicate, setIsNicknameDuplicate] = useState<boolean>(false);
    
    const [userInfo, setUserInfo] = useState<UserProfile> ({
        id: 0,
        email: "",
        nickname: "",
        pointBalance: 0,
        birthDate: "",
        oauthProvider: ""
    })

    const [updateData, setUpdateData] = useState<UpdateRequest> ({
        nickname: "",
        password: "",
        confirmPassword: ""
    })


    useEffect(() => {
        const isPasswordVerified = sessionStorage.getItem("passwordVerified");
        if (!isPasswordVerified) {
            navigate("/users/check-password");
        }
    }, [navigate]);

    useEffect(() => {
        async function fetchData() {
            try {
                const response = await axios.get("/api/users/me", {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                    },
                    withCredentials: true
                });

                setUserInfo(response.data)
                const newUpdateData: UpdateRequest = {
                    nickname: response.data.nickname,
                    password: "",
                    confirmPassword: ""
                }

                setUpdateData(newUpdateData)
            } catch (error) {
                alert("사용자 정보를 불러오지 못했습니다.");
            }
        };

        fetchData();
    }, []);

    const handleNicknameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newNickname = e.target.value;

        setUpdateData(prev => ({
            ...prev,
            nickname: newNickname
        })) 
        if (!newNickname) {
            setErrors(prev => {
                const newErrors = { ...prev };
                delete newErrors.nickname;
                return newErrors;
            });
            setSuccessMessage("");
        } else if (userInfo.nickname === newNickname) {
            setErrors(prev => {
                const newErrors = {...prev}
                delete newErrors.nickname;
                return newErrors;
            });
            setSuccessMessage("");
        } else if (!!newNickname) {
            setErrors(prev => {
                const newErrors = { ...prev };
                delete newErrors.nickname;
                return newErrors;
            });
            setSuccessMessage("사용 가능한 닉네임입니다.");
        }
    }

    const checkNicknameDuplicate = async (nickname:string) => {
        if (!nickname) return;

        if (nickname === userInfo.nickname) return;

        try {
            const response = await axios.get(`/api/users/check-nickname?nickname=${encodeURIComponent(nickname)}`);

            if (response.data.isNicknameDuplicate) {
                setIsNicknameDuplicate(true);
                setErrors(prev => ({
                    ...prev,
                    nickname: "이미 사용 중인 닉네임입니다."
                }));
                setSuccessMessage("");
            } else {
                setIsNicknameDuplicate(false);
                setErrors(prev => {
                    const newErrors = {...prev}
                    delete newErrors.nickname;

                    return newErrors;
                });
                setSuccessMessage("사용 가능한 닉네임입니다.");
            }
        } catch (error) {
            setIsNicknameDuplicate(true)
            setErrors(prev => ({
                ...prev,
                nickname: "이미 사용 중인 닉네임입니다."
            }));
            setSuccessMessage("");
        }
    }

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newPassword = e.target.value;
        setUpdateData(prev => ({
            ...prev,
            password: newPassword
        })) 
        const passwordRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\-?$%&^])[a-zA-Z0-9!@#\-?$%&^]{8,}$/;
        // 비밀번호 유효성 검사
        if (!newPassword) {
            setErrors(prev => {
                const newErrors = { ...prev };
                delete newErrors.password;
                return newErrors;
            });
        } else if (!passwordRegex.test(newPassword)) {
            setErrors(prev => ({
                ...prev,
                password: "비밀번호는 8자 이상이어야 하며, 영문, 숫자, 특수문자(!@#$%^&-)를 모두 포함해야 합니다."
            }))
        } else {
            setErrors(prev => {
                const newErrors = { ...prev };
                delete newErrors.password;
                return newErrors;
            });
        }

        if (updateData.confirmPassword === newPassword) {
            setErrors(prev => {
                const newErrors = {...prev};
                delete newErrors.confirmPassword;
                return newErrors;
            })
        } else if (updateData.confirmPassword) {
            setErrors(prev => ({
                ...prev,
                confirmPassword: "비밀번호가 일치하지 않습니다."
            }))
        }
    }

    const handleConfirmPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newConfirmPassword = e.target.value;
        setUpdateData(prev => ({
            ...prev,
            confirmPassword: newConfirmPassword
        })) 

        if (newConfirmPassword !== updateData.password) {
            setErrors(prev => ({
                ...prev,
                confirmPassword: "비밀번호가 일치하지 않습니다."
            }))
        } else {
            setErrors(prev => {
                const newErrors = { ...prev };
                delete newErrors.confirmPassword;
                return newErrors;
            });

        }
    }

    const handleNicknameBlur = (e: React.ChangeEvent<HTMLInputElement>) => {
        const nickname = e.target.value.trim();
        if (nickname) {
            checkNicknameDuplicate(nickname)
        }
    }

    const getInputClassName = (fieldName: keyof UpdateRequest) => {
        const rawValue = updateData[fieldName];
        const value = typeof rawValue === "string" ? rawValue.trim() : "";
       
        if (fieldName === "nickname") {
            if (!successMessage) {
                return ""
            }

            if (userInfo.nickname === value || !value ){
                return ""
            } else if (!!errors.nickname || isNicknameDuplicate) {
                return "input-error"
            } else if (!errors.nickname && userInfo.nickname !== value){
                return "input-success"
            }
        }
        
        if (fieldName === "password") {
            if (!!errors.password) {
                return "input-error"
            } else if ( value ){
                return "input-success"
            }
        }


        if (fieldName === "confirmPassword") {
            if (!!errors.confirmPassword) {
                return "input-error"
            } else if (value) {
                return "input-success"
            }
        }
    }
    const isPasswordValid = 
        (updateData.password === "" && updateData.confirmPassword === "") ||
        updateData.password === updateData.confirmPassword

    
    const isFormValid = 
        Object.keys(errors).length === 0 && 
        isPasswordValid

    const handleSumbit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (Object.keys(errors).length > 0) {
            return;
        }

        try {
            const response = await axios.patch('/api/users/me', {
                nickname: updateData.nickname,
                password: updateData.password
            }, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("accessToken")}`
                }
            })

            if (response.status === 200) {
                alert("내 정보가 수정되었습니다.");

                setUpdateData(prev => ({
                    ...prev,
                    password: "",
                    confirmPassword: ""
                })) 
                
                setSuccessMessage("");
                
            }
        } catch (error) {
            alert("정보 수정 중 오류가 발생했습니다.");
        }
    };

    return (
        <div className="main-container">
            <main className="sidebar-main">
                <Sidebar />
                <div className="sidebar-right-wrap">
                    <h2 className="mypage-title">내 정보 수정</h2>
                    <form onSubmit={handleSumbit} className="edit-profile-form">
                        <div className="edit-profile-form-group">
                            <label htmlFor="" className="edit-profile-label">이메일</label>
                            <span className="edit-profile-value">{userInfo.email}</span>
                        </div>
                        <div className="edit-profile-form-group">
                            <label htmlFor="" className="edit-profile-label">비밀번호</label>
                            <div className="edit-profile-input-wrap">
                                <input 
                                    type="password" 
                                    className={`edit-profile-input ${getInputClassName("password")}`}  
                                    onChange={handlePasswordChange}
                                    value={updateData.password}
                                />
                                {errors.password && (
                                    <p className="edit-profile-msg error">{errors.password}</p>
                                )}
                            </div>
                        </div>
                        <div className="edit-profile-form-group">
                            <label htmlFor="" className="edit-profile-label">비밀번호 확인</label>
                            <div>
                                <input 
                                    type="password" 
                                    className={`edit-profile-input ${getInputClassName("confirmPassword")}`}  
                                    onChange={handleConfirmPasswordChange}
                                    value={updateData.confirmPassword}
                                />
                                {errors.confirmPassword && (
                                    <p className="edit-profile-msg error">{errors.confirmPassword}</p>
                                )}
                            </div>
                        </div>
                        <div className="edit-profile-form-group">
                            <label htmlFor="" className="edit-profile-label">닉네임</label>
                            <div>
                                <input 
                                    type="text" 
                                    className={`edit-profile-input ${getInputClassName("nickname")}`} 
                                    value={updateData.nickname}
                                    onChange={handleNicknameChange}
                                    onBlur={handleNicknameBlur}
                                />
                                {errors.nickname && (
                                    <p className="edit-profile-msg error">{errors.nickname}</p>
                                )}
                                {successMessage && (
                                    <p className="edit-profile-msg success">{successMessage}</p>
                                )}
                            </div>
                        </div>
                        <div className="edit-profile-form-group">
                            <label htmlFor="" className="edit-profile-label">생년월일</label>
                            <span className="edit-profile-value">{userInfo?.birthDate || "-"}</span>
                        </div>
                        <button type="submit" disabled={!isFormValid} className="edit-profile-submit-btn">수정하기</button>
                    </form>
                </div>
            </main>

        </div>
    )

}

export default EditProfile