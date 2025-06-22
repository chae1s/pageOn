package com.pageon.backend.repository;

import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    Optional<Users> findByProviderAndProviderId(Provider provider, Long providerId);

}
