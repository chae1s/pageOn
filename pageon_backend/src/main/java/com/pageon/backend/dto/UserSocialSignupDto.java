package com.pageon.backend.dto;

import com.pageon.backend.entity.enums.Provider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSocialSignupDto {
    private String providerId;
    private Provider provider;

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotBlank(message = "생년월일을 입력해주세요.")
    @Pattern(
            regexp = "^\\d{8}$",
            message = "생년월일은 8자리 숫자(YYYYMMDD) 형식이어야 합니다."
    )
    private String birthDate;
}
