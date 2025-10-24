import React, {useState, useEffect} from "react";
import { styled, css } from "styled-components";
import { MainContainer, SidebarMain } from "../../styles/Layout.styles";
import * as M from "./MyPage.styles"
import { UserProfile, UpdateRequest } from "../../types/User";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Sidebar from "../../components/Sidebars/MyPageSidebar";

const EditProfileForm = styled.form`
    display: flex;
    flex-direction: column;
    max-width: 700px;
    gap: 18px;
    margin: 0 auto;
    padding: 0 24px;
    align-items: flex-start;
`

const EditProfileFormGroup = styled.div`
    display: flex;
    align-items: flex-start;
    margin-bottom: 15px;
    gap: 16px;
    min-height: 60px;
    width: 100%;
`

const EditProfileLabel = styled.label`
    flex: 0 0 120px;
    font-weight: 600;
    color: #333;
    margin-top: 12px;
`

const EditProfileValue = styled.span`
    flex: 1;
    color: #666;
    margin-top: 12px;
    cursor: default;
`

const EditProfileInput = styled.input<{validation? : "" | "input-success" | "input-error"}>`
    width: 500px;
    padding: 12px 16px;
    border: 1px solid #ccc;
    border-radius: 8px;
    font-size: 1rem;
    height: 48px;
    box-sizing: border-box;

    ${({validation}) => 
        validation === "input-success" && 
        css`
            border: 1px solid #A0C4FF;
        `
    }

    ${({validation}) => 
        validation === "input-error" && 
        css`
            border: 1px solid #FC5858;
        `
    }

`

const EditProfileMsg = styled.p<{validation? : "success" | "error"}>`
    font-size: 0.85rem;
    margin-top: 8px;
    position: relative;
    left: 0;

    ${({validation}) => 
        validation === "success" && 
        css`
            color: #2563eb;
        `
    }
    ${({validation}) => 
        validation === "error" && 
        css`
            color: var(--error-color);
        `
    }
`

const EditProfileSubmitBtn = styled.button`
    width: 33.33%;
    background: #528efa;
    color: #fff;
    border: none;
    border-radius: 6px;
    padding: 12px 0;
    font-weight: 600;
    font-size: 1.08rem;
    cursor: pointer;
    margin-top: 24px;
    transition: background 0.15s;
    display: block;
    margin-left: auto;
    margin-right: auto;

    &:disabled {
        background-color: var(--accent-color);
    }
`

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
        <MainContainer>
            <SidebarMain>
                <Sidebar />
                <M.SidebarRightWrap>
                    <M.MypageTitle>내 정보 수정</M.MypageTitle>
                    <EditProfileForm onSubmit={handleSumbit} >
                        <EditProfileFormGroup>
                            <EditProfileLabel>이메일</EditProfileLabel>
                            <EditProfileValue>{userInfo.email}</EditProfileValue>
                        </EditProfileFormGroup>
                        <EditProfileFormGroup>
                            <EditProfileLabel htmlFor="" >비밀번호</EditProfileLabel>
                            <div>
                                <EditProfileInput 
                                    type="password" 
                                    validation = {getInputClassName("password")} 
                                    onChange={handlePasswordChange}
                                    value={updateData.password}
                                />
                                {errors.password && (
                                    <EditProfileMsg validation="error">{errors.password}</EditProfileMsg>
                                )}
                            </div>
                        </EditProfileFormGroup>
                        <EditProfileFormGroup>
                            <EditProfileLabel htmlFor="">비밀번호 확인</EditProfileLabel>
                            <div>
                                <EditProfileInput 
                                    type="password" 
                                    validation = {getInputClassName("confirmPassword")} 
                                    onChange={handleConfirmPasswordChange}
                                    value={updateData.confirmPassword}
                                />
                                {errors.confirmPassword && (
                                    <EditProfileMsg validation="error">{errors.confirmPassword}</EditProfileMsg>
                                )}
                            </div>
                        </EditProfileFormGroup>
                        <EditProfileFormGroup>
                            <EditProfileLabel htmlFor="" >닉네임</EditProfileLabel>
                            <div>
                                <EditProfileInput 
                                    type="text" 
                                    validation = {getInputClassName("nickname")} 
                                    value={updateData.nickname}
                                    onChange={handleNicknameChange}
                                    onBlur={handleNicknameBlur}
                                />
                                {errors.nickname && (
                                    <EditProfileMsg validation="error">{errors.nickname}</EditProfileMsg>
                                )}
                                {successMessage && (
                                    <EditProfileMsg validation="success">{successMessage}</EditProfileMsg>
                                )}
                            </div>
                        </EditProfileFormGroup>
                        <EditProfileFormGroup>
                            <EditProfileLabel htmlFor="" >생년월일</EditProfileLabel>
                            <EditProfileValue>{userInfo?.birthDate || "-"}</EditProfileValue>
                        </EditProfileFormGroup>
                        <EditProfileSubmitBtn type="submit" disabled={!isFormValid}>수정하기</EditProfileSubmitBtn>
                    </EditProfileForm>
                </M.SidebarRightWrap>
            </SidebarMain>

        </MainContainer>
    )

}

export default EditProfile