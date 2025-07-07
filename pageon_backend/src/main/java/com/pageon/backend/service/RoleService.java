package com.pageon.backend.service;

import com.pageon.backend.entity.Role;
import com.pageon.backend.entity.UserRole;
import com.pageon.backend.entity.Users;
import com.pageon.backend.common.base.enums.RoleType;
import com.pageon.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public void assignDefaultRole(Users users) {
        Role role = roleRepository.findByRoleType(RoleType.ROLE_USER).orElseThrow(() -> new RuntimeException("기본 권한이 없습니다."));

        UserRole userRole = UserRole.builder()
                .user(users)
                .role(role)
                .build();

        users.getUserRoles().add(userRole);
    }
}
