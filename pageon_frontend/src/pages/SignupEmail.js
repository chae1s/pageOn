import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Header from "../components/Header";
import "../styles/reset.css";
import "../styles/global.css";
import "./Signup.css";

function SignupEmail() {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
    nickname: "",
    birthDate: "",
  });
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [isCheckingEmail, setIsCheckingEmail] = useState(false);
  const [isEmailDuplicate, setIsEmailDuplicate] = useState(false);
  const [isCheckingNickname, setIsCheckingNickname] = useState(false);
  const [isNicknameDuplicate, setIsNicknameDuplicate] = useState(false);
  const navigate = useNavigate();

  const validate = useCallback((data, checkRequired = false) => {
    const newErrors = {};
    // destructure birthDate instead of birthdate
    const { email, password, confirmPassword, nickname, birthDate } = data;

    // Email validation
    if (checkRequired && !email) {
      newErrors.email = "이메일을 입력해주세요.";
    } else if (email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email)) {
        newErrors.email = "유효한 이메일 형식이 아닙니다.";
      }
    }

    // Password validation
    if (checkRequired && !password) {
      newErrors.password = "비밀번호를 입력해주세요.";
    } else if (password) {
      const passwordRegex =
        /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\-?$%&^])[a-zA-Z0-9!@#\-?$%&^]{8,}$/;
      if (!passwordRegex.test(password)) {
        newErrors.password =
          "비밀번호는 8자 이상이어야 하며, 영문, 숫자, 특수문자(!@-#$%&^)를 모두 포함해야 합니다.";
      }
    }

    // Confirm password validation
    if (checkRequired && !confirmPassword) {
      newErrors.confirmPassword = "비밀번호를 다시 입력해주세요.";
    } else if (confirmPassword && password !== confirmPassword) {
      newErrors.confirmPassword = "비밀번호가 일치하지 않습니다.";
    }

    // Nickname validation
    if (checkRequired && !nickname) {
      newErrors.nickname = "닉네임을 입력해주세요.";
    }

    // BirthDate validation
    if (checkRequired && !birthDate) {
      newErrors.birthDate = "생년월일을 입력해주세요.";
    } else if (birthDate) {
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

  const checkEmailDuplicate = async (email) => {
    if (!email) return;
    
    setIsCheckingEmail(true);
    try {
      const response = await axios.get(`/api/users/check-email?email=${encodeURIComponent(email)}`);
      console.log(response)
      if (response.data.isEmailDuplicate) {
        console.log("이메일 중복")
        setIsEmailDuplicate(true);
        setErrors(prev => ({
          ...prev,
          email: "이미 사용 중인 이메일입니다."
        }));
      } else {
        setIsEmailDuplicate(false);
        setErrors(prev => {
          const newErrors = { ...prev };
          delete newErrors.email;
          return newErrors;
        });
      }
    } catch (error) {
      console.error("Email check error:", error);
      setIsEmailDuplicate(true);
      setErrors(prev => ({
        ...prev,
        email: "이미 사용중인 이메일입니다."
      }));
    } finally {
      setIsCheckingEmail(false);
    }
  };

  const checkNicknameDuplicate = async (nickname) => {
    if (!nickname) return;
    console.log("닉네임")
    setIsCheckingNickname(true);
    try {
      const response = await axios.get(`/api/users/check-nickname?nickname=${encodeURIComponent(nickname)}`);
      console.log(response)
      if (response.data.isNicknameDuplicate) {
        console.log("닉네임 중복")
        setIsNicknameDuplicate(true);
        setErrors(prev => ({
          ...prev,
          nickname: "이미 사용 중인 닉네임입니다."
        }));
      } else {
        setIsNicknameDuplicate(false);
        setErrors(prev => {
          const newErrors = { ...prev };
          delete newErrors.nickname;
          return newErrors;
        });
      }
    } catch (error) {
      console.error("Nickname check error:", error);
      setIsNicknameDuplicate(true);
      setErrors(prev => ({
        ...prev,
        nickname: "이미 사용중인 닉네임입니다."
      }));
    } finally {
      setIsCheckingNickname(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    // If the input name is "birthdate", map it to "birthDate" in formData
    const mappedName = name === "birthdate" ? "birthDate" : name;
    setFormData((prevState) => ({
      ...prevState,
      [mappedName]: value,
    }));
    
    // 이메일이 변경되면 중복 상태 초기화
    if (name === "email") {
      setIsEmailDuplicate(false);
    }
    
    // 닉네임이 변경되면 중복 상태 초기화
    if (name === "nickname") {
      setIsNicknameDuplicate(false);
    }
  };

  const handleEmailBlur = (e) => {
    const email = e.target.value.trim();
    if (email) {
      // 이메일 유효성 검사를 먼저 수행
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (emailRegex.test(email)) {
        checkEmailDuplicate(email);
      }
    }
  };

  const handleNicknameBlur = (e) => {
    const nickname = e.target.value.trim();
    if (nickname) {
      checkNicknameDuplicate(nickname);
    }
  };

  useEffect(() => {
    setErrors(validate(formData));
  }, [formData, validate]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const finalErrors = validate(formData, true);
    setErrors(finalErrors);

    if (Object.keys(finalErrors).length > 0) {
      return;
    }

    setIsLoading(true);

    try {
      const response = await axios.post('/api/users/signup', {
        email: formData.email,
        password: formData.password,
        nickname: formData.nickname,
        birthDate: formData.birthDate,
      });

      console.log("Signup successful:", response.data);
      alert("회원가입이 완료되었습니다.");
      navigate("/");
    } catch (error) {
      console.error("Signup error:", error);
      if (error.response) {
        console.error("Signup failed:", error.response.data);
        alert("회원가입에 실패했습니다. 다시 시도해주세요.");
      } else {
        alert("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  const isPasswordMatch =
    formData.password &&
    formData.confirmPassword &&
    formData.password === formData.confirmPassword;

    const getInputClassName = (fieldName) => {
      // Map "birthdate" to "birthDate" for input className logic
      const mappedFieldName = fieldName === "birthdate" ? "birthDate" : fieldName;
      const value = formData[mappedFieldName]?.trim() ?? "";
    
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
    
      if (errors[mappedFieldName]) return "input-error";
      if (value && !errors[mappedFieldName]) return "input-success";
    
      return "";
    };

  const isFormValid =
    Object.values(formData).every((value) => value.trim() !== "") &&
    Object.keys(errors).length === 0 &&
    !isEmailDuplicate;

  return (
    <div className="signup-page-container">
      <Header />
      <main className="signup-main">
        <div className="signup-form-wrapper">
          <h1 className="signup-title">회원가입</h1>
          <form onSubmit={handleSubmit} className="signup-form" noValidate>
            <div className="form-group">
              <label htmlFor="email">이메일</label>
              <input
                type="email"
                id="email"
                name="email"
                className={`form-input ${getInputClassName("email")}`}
                value={formData.email}
                onChange={handleChange}
                onBlur={handleEmailBlur}
                placeholder="이메일을 입력해주세요"
                required
              />
              {errors.email && (
                <p className="error-message">{errors.email}</p>
              )}
            </div>
            <div className="form-group">
              <label htmlFor="password">비밀번호</label>
              <input
                type="password"
                id="password"
                name="password"
                className={getInputClassName("password")}
                value={formData.password}
                onChange={handleChange}
                placeholder="비밀번호를 입력해주세요"
                required
              />
              {errors.password && (
                <p className="error-message">{errors.password}</p>
              )}
            </div>
            <div className="form-group">
              <label htmlFor="confirmPassword">비밀번호 확인</label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                className={getInputClassName("confirmPassword")}
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="비밀번호를 다시 입력해주세요"
                required
              />
              {errors.confirmPassword && (
                <p className="error-message">{errors.confirmPassword}</p>
              )}
            </div>
            <div className="form-group">
              <label htmlFor="nickname">닉네임</label>
              <input
                type="text"
                id="nickname"
                name="nickname"
                className={getInputClassName("nickname")}
                value={formData.nickname}
                onChange={handleChange}
                onBlur={handleNicknameBlur}
                placeholder="사용하실 닉네임을 입력해주세요"
                required
              />
              {errors.nickname && (
                <p className="error-message">{errors.nickname}</p>
              )}
            </div>
            <div className="form-group">
              <label htmlFor="birthdate">생년월일</label>
              <input
                type="text"
                id="birthdate"
                name="birthdate"
                className={getInputClassName("birthdate")}
                value={formData.birthDate}
                onChange={handleChange}
                placeholder="생년월일 8자리 (YYYYMMDD)"
                maxLength="8"
                required
              />
              {errors.birthDate && (
                <p className="error-message">{errors.birthDate}</p>
              )}
            </div>
            <button
              type="submit"
              className="submit-btn"
              disabled={!isFormValid || isLoading}
            >
              {isLoading ? "가입 중..." : "가입하기"}
            </button>
          </form>
          <div className="login-link">
            <span>이미 계정이 있으신가요?</span>
            <a href="/login">로그인</a>
          </div>
        </div>
      </main>
    </div>
  );
}

export default SignupEmail;