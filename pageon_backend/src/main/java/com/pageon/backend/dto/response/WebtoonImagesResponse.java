package com.pageon.backend.dto.response;

import com.pageon.backend.entity.WebtoonImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebtoonImagesResponse {

    private Long id;
    private Integer sequence;
    private String imageUrl;

    public static WebtoonImagesResponse fromEntity(WebtoonImage webtoonImage, String signUrl) {
        return new WebtoonImagesResponse(webtoonImage.getId(), webtoonImage.getSequence(), signUrl);
    }

}
