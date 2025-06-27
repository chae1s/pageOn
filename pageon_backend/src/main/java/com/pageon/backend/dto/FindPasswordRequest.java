package com.pageon.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FindPasswordRequest {
    private String email;

}
