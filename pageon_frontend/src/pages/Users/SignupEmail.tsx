import React, {useState, useEffect, useCallback} from "react";
import styled from "styled-components";
import * as U from "./Users.styles"
import { MainContainer, NoSidebarMain } from "../../styles/Layout.styles";
import { SignupRequest } from "../../types/User";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const SignupNessesary = styled.div`
    margin-left: 5px;
    margin-bottom: 5px;
    font-size: 13px;
`

const NessesaryCheck = styled.span`
    color: #fc5858;
    margin-right: 3px;
`

const UsersFormBirthDate = styled.div`
    flex: 0 0 65%;
`

const UsersFormGenderCheckbox = styled.div`
    width: 100%;
`

const UsersFromGroupGenderLabel = styled.div`
    display: block;
    font-size: 0.9rem;
    font-weight: 600;
    margin-bottom: 8px;
    color: #444;
`

const UsersFormGroupGender = styled.div`
    flex: 0 0 35%;
    display: flex;
`

const UsersFormGenderLabelLeft = styled.label<{selected: boolean}>`
    width: 50%;
    height: 47px;
    overflow: hidden;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1;
    background-color: ${({ selected }) => (selected ? "#A0C4FF !important" : "#fff")};
    border-top-left-radius: 3px;
    border-bottom-left-radius: 3px;
    border: 1px solid ${({ selected }) => (selected ? "#A0C4FF" : "#ccc")};
    color: ${({ selected }) => (selected ? "#FFF" : "#444")};
    border-right: none;

`

const UsersFormGenderLabelRight = styled.label<{selected: boolean}>`
    width: 50%;
    height: 47px;
    overflow: hidden;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1;
    background-color: ${({ selected }) => (selected ? "#A0C4FF !important" : "#fff")};
    border-top-right-radius: 3px;
    border-bottom-right-radius: 3px;
    border: 1px solid ${({ selected }) => (selected ? "#A0C4FF" : "#ccc")};
    color: ${({ selected }) => (selected ? "#FFF" : "#444")};
`

const UsersTerms = styled.div`
    background: #f8f9fa;
    border: 1px solid #e5e5e5;
    border-radius: 4px;
    padding: 16px 14px 10px 14px;
    margin-top: 10px;
    margin-bottom: 18px;
    font-size: 0.97rem;
    color: #444;
`

const GenderInput = styled.input`
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #ccc;
    border-radius: 3px;
    font-size: 1rem;
    box-sizing: border-box;
    position: absolute;
    width: 1px;
    height: 1px;
    margin: -1px;
    padding: 0px;
    overflow: hidden;
    border: 0;
`

const TermsContent = styled.div`
    margin-bottom: 8px;
    padding: 10px;
    background: #fff;
    border-radius: 3px;
    border: 1px solid #eee;
    font-size: 0.92rem;
    color: #666;
    max-height: 170px;
    overflow-y: auto;
    line-height: 1.5;
`

const TermsLabel = styled.label`
    display: flex;
    align-items: flex-start;
    font-size: 0.9rem;
    cursor: pointer;
    margin-top: 8px;
    justify-content: center;
`

const TermsInput = styled.input`
    border: 1px solid #ccc !important;
    background: #fff !important;
    box-sizing: border-box;
    width: 15px;
    height: 15px;
    margin-top: 5px;
    margin-right: 15px;
    accent-color: var(--accent-color);
`

function SignupEmail() {
    const [formData, setFormData] = useState<SignupRequest>({
        email: "",
        password: "",
        confirmPassword: "",
        nickname: "",
        termsAgreed: false,
        birthDate: "",
        gender: ""
    });

    type ErrorMap = Record<string, string>;

    const [errors, setErrors] = useState<ErrorMap>({});
    const [isEmailDuplicate, setIsEmailDuplicate] = useState<boolean>(false);
    const [isNicknameDuplicate, setIsNicknameDuplicate] = useState<boolean>(false);
    const navigate = useNavigate();

    

    const validate = useCallback((data: SignupRequest, checkRequired = false) => {
        const newErrors: ErrorMap = {};

        const {email, password, confirmPassword, nickname, birthDate} = data;

        // 이메일 유효성 검사
        if (checkRequired && !email) {
            newErrors.email = "이메일을 입력해주세요.";
        } else if (email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                newErrors.email = "유효한 이메일 형식이 아닙니다.";
            }
        }

        // 비밀번호 유효성 검사
        if (checkRequired && !password) {
            newErrors.password = "비밀번호를 입력해주세요.";
        } else if (password) {
            const passwordRegex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\-?$%&^])[a-zA-Z0-9!@#\-?$%&^]{8,}$/;
            if (!passwordRegex.test(password)) {
                newErrors.password = "비밀번호는 8자 이상이어야 하며, 영문, 숫자, 특수문자(!@#$%^&-)를 모두 포함해야 합니다.";
            }
        }

        // 비밀번호 일치 확인
        if (checkRequired && !confirmPassword) {
            newErrors.confirmPassword = "비밀번호를 다시 입력해주세요.";
        } else if (confirmPassword && confirmPassword !== password) {
            newErrors.confirmPassword = "비밀번호가 일치하지 않습니다.";
        }

        // 닉네임 유효성 검사
        if (checkRequired && !nickname) {
            newErrors.nickname = "닉네임을 입력해주세요."
        }

        if (birthDate) {
            if (birthDate.length !== 8) {
              newErrors.birthDate = "생년월일은 8자리로 입력해주세요.";
            } else {
              const year = birthDate.substring(0, 4);
              const month = birthDate.substring(4, 6);
              const day = birthDate.substring(6, 8);
              const birthDateObj = new Date(
                parseInt(year, 10),
                parseInt(month, 10) - 1,
                parseInt(day, 10)
              );
              const today = new Date();
              today.setHours(0, 0, 0, 0);
      
              if (
                birthDateObj.getFullYear() !== parseInt(year, 10) ||
                birthDateObj.getMonth() !== parseInt(month, 10) - 1 ||
                birthDateObj.getDate() !== parseInt(day, 10)
              ) {
                newErrors.birthDate = "유효하지 않은 생년월일입니다.";
              } else if (birthDateObj > today) {
                newErrors.birthDate = "유효하지 않은 생년월일입니다.";
              }
            }
          }

        return newErrors;
        
    }, []);

    const checkEmailDuplicate = async (email:string) => {
        if (!email) return;


        try {
            const response = await axios.get(`/api/users/check-email?email=${encodeURIComponent(email)}`);

            if (response.data.isEmailDuplicate) {
                setIsEmailDuplicate(true);
                setErrors(prev => ({
                    ...prev,
                    email: "이미 사용 중인 이메일입니다."
                }));
            } else {
                setIsEmailDuplicate(false);
                setErrors(prev => {
                    const newErrors = {...prev}
                    delete newErrors.email;

                    return newErrors;
                });
            }
        } catch (error) {
            setIsEmailDuplicate(true)
            setErrors(prev => ({
                ...prev,
                email: "이미 사용 중인 이메일입니다."
            }));
        } 
    };

    const checkNicknameDuplicate = async (nickname:string) => {
        if (!nickname) return;

        try {
            const response = await axios.get(`/api/users/check-nickname?nickname=${encodeURIComponent(nickname)}`);

            if (response.data.isNicknameDuplicate) {
                setIsNicknameDuplicate(true);
                setErrors(prev => ({
                    ...prev,
                    nickname: "이미 사용 중인 닉네임입니다."
                }));
            } else {
                setIsNicknameDuplicate(false);
                setErrors(prev => {
                    const newErrors = {...prev}
                    delete newErrors.nickname;

                    return newErrors;
                });
            }
        } catch (error) {
            setIsNicknameDuplicate(true)
            setErrors(prev => ({
                ...prev,
                nickname: "이미 사용 중인 닉네임입니다."
            }));
        } 
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value, type, checked} = e.target;

        setFormData((prevstate) => ({
            ...prevstate,
            [name]: type === "checkbox" ? checked : value,
        }))

        if (name === "email") {
            setIsEmailDuplicate(false);
        }

        if (name === "nickname") {
            setIsNicknameDuplicate(false);
        }

    };

    const handleEmailBlur = (e: React.ChangeEvent<HTMLInputElement>) => {
        const email = e.target.value.trim();

        if (email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (emailRegex.test(email)) {
              checkEmailDuplicate(email);
            }
        }
    }

    const handleNicknameBlur = (e: React.ChangeEvent<HTMLInputElement>) => {
        const nickname = e.target.value.trim();
        if (nickname) {
        checkNicknameDuplicate(nickname);
        }
    }

    const handleGenderChange = (selectedGender: "MALE" | "FEMALE") => {
        setFormData(prev => ({
            ...prev,
            gender: prev.gender === selectedGender ? "" : selectedGender
        }))

    }

    useEffect(() => {
        setErrors(validate(formData));
    }, [formData, validate]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const finalErrors = validate(formData, true);
        setErrors(finalErrors);

        if (Object.keys(finalErrors).length > 0) {
            return;
        }
        
        try {
            const response = await axios.post('/api/users/signup', {
                email: formData.email,
                password: formData.password,
                nickname: formData.nickname,
                birthDate: formData.birthDate,
                gender: formData.gender,
                termsAgreed: formData.termsAgreed
            });
            console.log("회원가입 성공: ", response.data);
            alert("회원가입이 완료되었습니다.");
            navigate("/");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response) {
                    alert("회원가입에 실패했습니다. 다시 시도해주세요.");
                }
            } else {
                console.error("예상치 못한 에러 발생: ", error);
                alert("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
            }
        } 
        
    }


    const getInputclassName = (fieldName:keyof SignupRequest) => {
        const rawValue = formData[fieldName];
        const value = typeof rawValue === "string" ? rawValue.trim() : "";

        if (fieldName === "email") {
            if (isEmailDuplicate) {
                return "input-error";
            }
            if (errors.email) {
                return "input-error";
            }
            if (value && !errors.email && !isEmailDuplicate) {
                return "input-success";
            }
        }

        if (fieldName === "nickname") {
            if (isNicknameDuplicate) {
                return "input-error";
            }
            if (errors.nickname) {
                return "input-error";
            }
            if (value && !errors.nickname && !isNicknameDuplicate) {
                return "input-success";
            }
        }

        if (errors[fieldName]) return "input-error";
        if (value && !errors[fieldName]) return "input-success";

        return "";
    }

    const isFormValid =
        formData.email.trim() !== "" &&
        formData.password.trim() !== "" &&
        formData.confirmPassword.trim() !== "" &&
        formData.nickname.trim() !== "" &&
        formData.termsAgreed === true &&
        Object.keys(errors).length === 0 &&
        !isEmailDuplicate && !isNicknameDuplicate;


    return (
        <MainContainer>
            <NoSidebarMain>
                <U.UsersFormWrapper>
                    <U.UsersTitle>회원가입</U.UsersTitle>
                    <U.UsersForm onSubmit={handleSubmit}>
                        <U.UsersFormGroup>
                            <U.UsersFormLabel htmlFor="email">이메일 <NessesaryCheck>*</NessesaryCheck></U.UsersFormLabel>
                            <U.UsersFormInput
                                validation={getInputclassName("email")}
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                placeholder="이메일을 입력해주세요."
                                onChange={handleChange}
                                onBlur={handleEmailBlur}
                            />
                            {errors.email && (
                                <U.ErrorMessage>{errors.email}</U.ErrorMessage>
                            )}
                        </U.UsersFormGroup>
                        <U.UsersFormGroup>
                            <U.UsersFormLabel htmlFor="password">비밀번호 <NessesaryCheck>*</NessesaryCheck></U.UsersFormLabel>
                            <U.UsersFormInput
                                validation={getInputclassName("password")}
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="비밀번호를 입력해주세요."
                            />
                            {errors.password && (
                                <U.ErrorMessage>{errors.password}</U.ErrorMessage>
                            )}
                        </U.UsersFormGroup>
                        <U.UsersFormGroup>
                            <U.UsersFormLabel htmlFor="confirmPassword">비밀번호 확인 <NessesaryCheck>*</NessesaryCheck></U.UsersFormLabel>
                            <U.UsersFormInput
                                validation={getInputclassName("confirmPassword")}
                                type="password"
                                id="confirmPassword"
                                name="confirmPassword"
                                onChange={handleChange}
                                value={formData.confirmPassword}
                                placeholder="비밀번호를 다시 입력해주세요."
                                required
                            />
                            {errors.confirmPassword && (
                                <U.ErrorMessage>{errors.confirmPassword}</U.ErrorMessage>
                            )}
                        </U.UsersFormGroup>
                        <U.UsersFormGroup>
                            <U.UsersFormLabel htmlFor="nickname">닉네임 <NessesaryCheck>*</NessesaryCheck></U.UsersFormLabel>
                            <U.UsersFormInput
                                validation={getInputclassName("nickname")} 
                                type="nickname"
                                id="nickname"
                                name="nickname"
                                onChange={handleChange}
                                onBlur={handleNicknameBlur}
                                value={formData.nickname}
                                placeholder="닉네임을 입력해주세요."
                            />
                            {errors.nickname && (
                                <U.ErrorMessage>{errors.nickname}</U.ErrorMessage>
                            )}
                        </U.UsersFormGroup>
                        <U.UsersFormGroup className="users-birthDate-gender">
                            <UsersFormBirthDate>
                                <U.UsersFormLabel htmlFor="birthDate">생년월일</U.UsersFormLabel>
                                <U.UsersFormInput
                                    validation={getInputclassName("birthDate")}
                                    type="text"
                                    id="birthDate"
                                    name="birthDate"
                                    onChange={handleChange}
                                    value={formData.birthDate}
                                    placeholder="생년월일을 입력해주세요."
                                />
                                {errors.birthDate && (
                                    <U.ErrorMessage>{errors.birthDate}</U.ErrorMessage>
                                )}
                            </UsersFormBirthDate>
                            <UsersFormGenderCheckbox>
                                <UsersFromGroupGenderLabel>성별</UsersFromGroupGenderLabel>
                                <UsersFormGroupGender>
                                    <UsersFormGenderLabelLeft selected={formData.gender === "MALE"}>
                                        <GenderInput
                                            type="checkbox"
                                            id="gender"
                                            name="gender"
                                            onChange={() => handleGenderChange("MALE")}
                                            checked={formData.gender === "MALE"}
                                        />
                                        <span>남</span>
                                    </UsersFormGenderLabelLeft>
                                    <UsersFormGenderLabelRight selected={formData.gender === "FEMALE"}>
                                        <GenderInput
                                            type="checkbox"
                                            name="gender"
                                            onChange={() => handleGenderChange("FEMALE")}
                                            checked={formData.gender === "FEMALE"}
                                        />
                                        <span>여</span>
                                    </UsersFormGenderLabelRight>
                                </UsersFormGroupGender>
                            </UsersFormGenderCheckbox>
                        </U.UsersFormGroup>
                        <SignupNessesary><NessesaryCheck>*</NessesaryCheck>는 필수 입력 사항입니다.</SignupNessesary>
                        <UsersTerms>
                            <TermsContent>
                                제 1 조 (목적)<br /><br />
                                이 약관은 pageOn (이하 "회사")가 제공하는 웹소설 및 웹툰 플랫폼(이하 "서비스") 이용과 관련하여 
                                회사와 회원 간의 권리, 의무 및 기타 필요한 사항을 규정함을 목적으로 합니다.
                                <br />
                                <br />
                                제 2 조 (정의)<br />
                                “회원”이란 본 약관에 동의하고, 회사와 서비스 이용 계약을 체결한 자를 말합니다.
                                <br />
                                <br />
                                “콘텐츠”란 회사가 제공하거나 회원이 서비스 내에 게시한 모든 텍스트, 이미지, 웹소설, 웹툰 등을 말합니다.
                                <br />
                                <br />
                                “AI 콘텐츠”란 인공지능(AI) 도구를 활용하여 전부 또는 일부 생성된 콘텐츠를 의미합니다.
                                <br />
                                <br />
                                제 3 조 (약관의 게시 및 개정)<br />
                                회사는 본 약관의 내용을 회원이 쉽게 확인할 수 있도록 서비스 초기화면 또는 연결화면에 게시합니다.
                                <br />
                                <br />
                                회사는 관련 법령을 위반하지 않는 범위에서 본 약관을 개정할 수 있으며, 개정 시 사전 고지합니다.
                                <br />
                                <br />
                                변경된 약관에 동의하지 않을 경우, 회원은 서비스 이용을 중단할 수 있습니다.
                                <br />
                                <br />
                                제 4 조 (회원가입)<br />
                                회원가입은 이용자가 본 약관에 동의하고 회사가 정한 가입 절차를 완료함으로써 성립됩니다.
                                <br />
                                <br />
                                회사는 다음의 경우 회원가입을 거부하거나 이후에 자격을 박탈할 수 있습니다:
                                <br />
                                <br />
                                타인 명의로 가입한 경우
                                <br />
                                <br />
                                허위 정보를 기재한 경우
                                <br />
                                <br />
                                약관을 위반하거나, 서비스 운영에 지장을 초래하는 경우
                                <br />
                                <br />
                                제 5 조 (서비스 이용)<br />
                                회사는 회원에게 웹소설, 웹툰 열람, 평점, 선호 작품 등록 등 다양한 기능을 제공합니다.
                                <br />
                                <br />
                                회사는 서비스의 운영상 필요에 따라 콘텐츠의 형식, 기능, 제공 범위 등을 변경할 수 있습니다.
                                <br />
                                <br />
                                제 6 조 (AI 콘텐츠 관련 고지)<br />
                                본 플랫폼에는 인공지능(AI) 기술을 활용하여 생성된 콘텐츠(이하 “AI 콘텐츠”)가 포함될 수 있습니다.
                                <br />
                                <br />
                                AI 콘텐츠는 현행 저작권법상 보호 범위가 명확하지 않으며, 일부 콘텐츠는 저작권 보호를 받지 않을 수 있습니다.
                                <br />
                                <br />
                                회사는 AI 콘텐츠 여부를 표시하며, 회원은 콘텐츠 이용 시 이를 인지하고 열람하는 데 동의한 것으로 간주됩니다.
                                <br />
                                <br />
                                제 7 조 (지식재산권)<br />
                                회사가 작성한 콘텐츠에 대한 저작권 및 기타 권리는 회사에 귀속됩니다.
                                <br />
                                <br />
                                회원이 작성한 댓글, 평점 등의 콘텐츠에 대한 권리는 해당 회원에게 있으며, 회사는 이를 서비스 운영에 필요한 범위 내에서 사용할 수 있습니다.
                                <br />
                                <br />
                                제 8 조 (회원의 의무)<br />
                                회원은 다음 행위를 해서는 안 됩니다:
                                <br />
                                <br />
                                타인의 정보를 도용하거나 무단으로 사용하는 행위
                                <br />
                                <br />
                                회사 또는 제3자의 권리를 침해하는 행위
                                <br />
                                <br />
                                AI 콘텐츠 또는 일반 콘텐츠의 무단 복제, 배포, 공유 행위
                                <br />
                                <br />
                                기타 불법적이거나 부당한 행위
                                <br />
                                <br />
                                제 9 조 (계약 해지)<br />
                                회원은 언제든지 서비스 내 제공된 기능을 통해 이용계약을 해지할 수 있습니다.
                                <br />
                                <br />
                                회사는 회원이 본 약관을 위반하거나, 서비스 운영에 악영향을 주는 경우 사전 통보 후 계약을 해지할 수 있습니다.
                                <br />
                                <br />
                                제 10 조 (면책조항)<br />
                                회사는 회원의 귀책사유로 인해 발생한 문제에 대해 책임을 지지 않습니다.
                                <br />
                                <br />
                                AI 콘텐츠와 관련하여 발생하는 제3자와의 분쟁 또는 법적 책임은 해당 콘텐츠 등록자 및 당사자에게 있습니다.
                                <br />
                                <br />
                                부칙<br />
                                본 약관은 2025년 7월 7일부터 시행됩니다.
                            </TermsContent>
                            <TermsLabel htmlFor="termsAgreed">
                                <TermsInput
                                    type="checkbox" 
                                    id="termsAgreed"
                                    name="termsAgreed"
                                    checked = {formData.termsAgreed}
                                    required
                                    onChange={handleChange}
                                />
                                <div className="term-message">본 이용약관 및 AI 콘텐츠 관련 고지사항을 <br />모두 확인하였으며, 이에 동의합니다.</div>
                            </TermsLabel>
                        </UsersTerms>
                        <U.SubmitBtn type="submit" disabled={!isFormValid}>
                            회원가입
                        </U.SubmitBtn>
                    </U.UsersForm>
                    <U.UsersLinkWrap>
                        <span>이미 계정이 있으신가요?</span>
                        <U.UsersLink to={"/users/login"}>로그인</U.UsersLink>

                    </U.UsersLinkWrap>
                </U.UsersFormWrapper>
            </NoSidebarMain>

        </MainContainer>
    )
}

export default SignupEmail