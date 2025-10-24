package com.pageon.backend.repository;

import com.pageon.backend.entity.User;
import com.pageon.backend.common.enums.OAuthProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    @Query("SELECT u FROM User u JOIN FETCH u.userRoles ur JOIN FETCH ur.role WHERE u.oAuthProvider = :provider AND u.providerId = :providerId")
    Optional<User> findWithRolesByProviderAndProviderId(@Param("oAuthProvider") OAuthProvider oAuthProvider, @Param("providerId") String providerId);


    @EntityGraph(attributePaths = {
            "userRoles", "userRoles.role"
    })
    Optional<User> findByEmailAndDeleted(String email, boolean deleted);

    Optional<User> findByIdAndDeleted(Long id, boolean deleted);

    Boolean existsByPhoneNumberAndIsPhoneVerifiedTrue(String phoneNumber);

    Boolean existsByEmailAndIsPhoneVerifiedTrue(String email);

}
