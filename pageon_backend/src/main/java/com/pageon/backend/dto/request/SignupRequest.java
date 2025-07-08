package com.pageon.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\\-?$%&^])[a-zA-Z0-9!@#\\-?$%&^]{8,}$",
            message = "비밀번호는 8자 이상이어야 하며, 영문, 숫자, 특수문자(!@-#$%&^)를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotBlank(message = "생년월일을 입력해주세요.")
    @Pattern(
            regexp = "^\\d{8}$",
            message = "생년월일은 8자리 숫자(YYYYMMDD) 형식이어야 합니다."
    )
    private String birthDate;
    private Boolean termsAgreed;
}
