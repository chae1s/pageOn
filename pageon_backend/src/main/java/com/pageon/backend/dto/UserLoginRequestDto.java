package com.pageon.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class UserLoginRequestDto {
    private String email;
    private String password;
}
