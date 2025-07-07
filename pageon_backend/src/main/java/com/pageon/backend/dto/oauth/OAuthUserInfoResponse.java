package com.pageon.backend.dto.oauth;

import com.pageon.backend.common.enums.Provider;

public interface OAuthUserInfoResponse {
    Provider getProvider();

    String getProviderId();

    String getEmail();

}
