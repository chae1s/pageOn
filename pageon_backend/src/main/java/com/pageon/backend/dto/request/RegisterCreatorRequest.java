package com.pageon.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCreatorRequest {
    @NotBlank(message = "필명을 입력해주세요.")
    private String penName;
    @NotBlank(message = "타입을 선택해주세요.")
    private String creatorType;
}
