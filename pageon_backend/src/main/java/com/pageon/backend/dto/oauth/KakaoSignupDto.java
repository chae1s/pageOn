package com.pageon.backend.dto.oauth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Override
    public String getEmail() {
        Object account = attribute.get("kakao_account");
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> attribute = mapper.convertValue(account, new TypeReference<>() {});

        return attribute.get("email").toString();
    }
}
