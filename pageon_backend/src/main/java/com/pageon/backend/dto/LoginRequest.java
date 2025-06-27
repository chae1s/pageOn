package com.pageon.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}
