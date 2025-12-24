package com.pageon.backend.dto.response;

import com.pageon.backend.entity.Content;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSimpleResponse {
    private Long id;
    private String title;
    private String author;
    private String cover;
    private String contentType;

    public static ContentSimpleResponse fromEntity(Content content) {
        return ContentSimpleResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .author(content.getCreator().getPenName())
                .cover(content.getCover())
                .contentType(content.getDtype())
                .build();
    }
}
