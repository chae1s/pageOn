package com.pageon.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentSimpleResponse {
    private Long id;
    private String title;
    private String author;
    private String cover;
    private String contentType;

    public static ContentSimpleResponse fromEntity(Long id, String title, String author, String cover, String contentType) {
        ContentSimpleResponse contentSimpleResponse = new ContentSimpleResponse();
        contentSimpleResponse.setId(id);
        contentSimpleResponse.setTitle(title);
        contentSimpleResponse.setAuthor(author);
        contentSimpleResponse.setCover(cover);
        contentSimpleResponse.setContentType(contentType);
        return contentSimpleResponse;
    }
}
