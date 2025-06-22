package com.pageon.backend.dto.oauth;

import com.pageon.backend.entity.enums.Provider;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoSignupDto implements OAuth2Response {

    private final Map<String, Object> attribute;

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

}
