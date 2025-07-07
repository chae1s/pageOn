package com.pageon.backend.service;

import com.pageon.backend.entity.Role;
import com.pageon.backend.entity.Users;
import com.pageon.backend.entity.enums.RoleType;
import com.pageon.backend.repository.RoleRepository;
import com.pageon.backend.security.PrincipalUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
@Transactional
@ActiveProfiles("test")
@DisplayName("roleService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;
    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();

    }

    @Test
    @DisplayName("기본 권한이 존재하면 User에 UserRole 추가")
    void assignDefaultRole_shouldAddUserRole() {
        // given
        Role role = Role.builder()
                .id(1L)
                .roleType(RoleType.ROLE_USER)
                .build();

        Users user = Users.builder()
                .email("test@mail.com")
                .userRoles(new ArrayList<>())
                .build();

        when(roleRepository.findByRoleType(RoleType.ROLE_USER)).thenReturn(Optional.of(role));

        //when
        roleService.assignDefaultRole(user);


        // then
        assertEquals(1, user.getUserRoles().size());
        assertEquals(role, user.getUserRoles().get(0).getRole());

    }

}