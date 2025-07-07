package com.pageon.backend.dto.oauth;

import com.pageon.backend.common.enums.Provider;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GoogleSignupRequest implements OAuthUserInfoResponse {

    private final Map<String, Object> attribute;

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }
}
