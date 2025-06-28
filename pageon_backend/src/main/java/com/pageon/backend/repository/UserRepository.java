package com.pageon.backend.repository;

import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.Provider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    @Query("SELECT u FROM Users u JOIN FETCH u.userRoles ur JOIN FETCH ur.role WHERE u.provider = :provider AND u.providerId = :providerId")
    Optional<Users> findWithRolesByProviderAndProviderId(@Param("provider") Provider provider, @Param("providerId") String providerId);


    @EntityGraph(attributePaths = {
            "userRoles", "userRoles.role"
    })
    Optional<Users> findByEmailAndIsDeletedFalse(String email);

    Optional<Users> findByIdAndIsDeletedFalse(Long id);

}
