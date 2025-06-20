import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import "../styles/reset.css";
import "../styles/global.css";
import "./Signup.css";

function SignupEmail() {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
    confirmPassword: "",
    nickname: "",
    birthdate: "",
  });
  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

  const validate = useCallback((data, checkRequired = false) => {
    const newErrors = {};
    const { email, password, confirmPassword, nickname, birthdate } = data;

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

    // Birthdate validation
    if (checkRequired && !birthdate) {
      newErrors.birthdate = "생년월일을 입력해주세요.";
    } else if (birthdate) {
      if (birthdate.length !== 8) {
        newErrors.birthdate = "생년월일은 8자리로 입력해주세요.";
      } else {
        const year = birthdate.substring(0, 4);
        const month = birthdate.substring(4, 6);
        const day = birthdate.substring(6, 8);
        const birthDate = new Date(
          parseInt(year, 10),
          parseInt(month, 10) - 1,
          parseInt(day, 10)
        );
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (
          birthDate.getFullYear() !== parseInt(year, 10) ||
          birthDate.getMonth() !== parseInt(month, 10) - 1 ||
          birthDate.getDate() !== parseInt(day, 10)
        ) {
          newErrors.birthdate = "유효하지 않은 생년월일입니다.";
        } else if (birthDate > today) {
          newErrors.birthdate = "유효하지 않은 생년월일입니다.";
        }
      }
    }
    return newErrors;
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  useEffect(() => {
    setErrors(validate(formData));
  }, [formData, validate]);

  const handleSubmit = (e) => {
    e.preventDefault();

    const finalErrors = validate(formData, true);
    setErrors(finalErrors);

    if (Object.keys(finalErrors).length > 0) {
      return;
    }

    // In a real application, you would send this data to a server.
    console.log("Signup data submitted:", formData);
    alert("회원가입이 완료되었습니다.");
    navigate("/");
  };

  const isPasswordMatch =
    formData.password &&
    formData.confirmPassword &&
    formData.password === formData.confirmPassword;

  const getInputClassName = (fieldName) => {
    if (errors[fieldName]) {
      return "input-error";
    }
    if (formData[fieldName] && !errors[fieldName]) {
      return "input-success";
    }
    return "";
  };

  const isFormValid =
    Object.values(formData).every((value) => value.trim() !== "") &&
    Object.keys(errors).length === 0;

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
                value={formData.birthdate}
                onChange={handleChange}
                placeholder="생년월일 8자리 (YYYYMMDD)"
                maxLength="8"
                required
              />
              {errors.birthdate && (
                <p className="error-message">{errors.birthdate}</p>
              )}
            </div>
            <button
              type="submit"
              className="submit-btn"
              disabled={!isFormValid}
            >
              가입하기
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