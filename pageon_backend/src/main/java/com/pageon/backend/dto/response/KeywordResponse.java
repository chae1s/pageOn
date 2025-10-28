package com.pageon.backend.dto.response;

import com.pageon.backend.entity.Keyword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordResponse {

    private Long id;
    private String name;

    public static KeywordResponse fromEntity(Keyword keyword) {
        KeywordResponse keywordResponse = new KeywordResponse();
        keywordResponse.setId(keyword.getId());
        keywordResponse.setName(keyword.getName());

        return keywordResponse;
    }

}
