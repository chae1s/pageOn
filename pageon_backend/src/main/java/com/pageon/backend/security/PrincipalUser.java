package com.pageon.backend.security;

import com.pageon.backend.dto.oauth.OAuthUserInfoResponse;
import com.pageon.backend.entity.User;
import com.pageon.backend.common.enums.OAuthProvider;
import com.pageon.backend.common.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PrincipalUser implements UserDetails, OAuth2User {
    private final User users;
    private final OAuthUserInfoResponse oAuthUserInfoResponse;

    public PrincipalUser(User user) {
        this.users = user;
        this.oAuthUserInfoResponse = null;
    }

    public User getUsers() {
        return users;
    }
    @Override
    public Map<String, Object> getAttributes() {
        if (oAuthUserInfoResponse == null) {
            return Map.of();
        }
        return Map.of(
                "email", oAuthUserInfoResponse.getEmail(),
                "provider", oAuthUserInfoResponse.getOAuthProvider(),
                "providerID", oAuthUserInfoResponse.getProviderId()
        );
    }

    @Override
    public String getName() {
        return this.users.getEmail();
    }

    public OAuthProvider getProvider() {
        return oAuthUserInfoResponse.getOAuthProvider();
    }

    public String getProviderId() {
        return oAuthUserInfoResponse.getProviderId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.users.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleType().name())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.users.getPassword();
    }

    @Override
    public String getUsername() {
        return this.users.getEmail();
    }

    public Long getId() {
        return this.users.getId();
    }

    public List<RoleType> getRoleType() {
        return this.users.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleType()).collect(Collectors.toList());
    }

}
