package com.pageon.backend.dto.oauth;

import com.pageon.backend.entity.enums.Provider;

public interface OAuth2Response {
    Provider getProvider();

    String getProviderId();

}
