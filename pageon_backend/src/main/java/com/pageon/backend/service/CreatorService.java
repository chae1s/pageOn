package com.pageon.backend.service;

import com.pageon.backend.common.enums.CreatorType;
import com.pageon.backend.common.enums.RoleType;
import com.pageon.backend.dto.request.RegisterCreatorRequest;
import com.pageon.backend.entity.Creators;
import com.pageon.backend.entity.Role;
import com.pageon.backend.entity.UserRole;
import com.pageon.backend.entity.Users;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.CreatorRepository;
import com.pageon.backend.repository.RoleRepository;
import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.PrincipalUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatorService {

    private final CreatorRepository creatorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void registerCreator(PrincipalUser principalUser, RegisterCreatorRequest creatorRequest) {
        String email = principalUser.getUsername();
        Users user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Role role = roleRepository.findByRoleType(RoleType.ROLE_CREATOR).orElseThrow(
                () -> new CustomException(ErrorCode.ROLE_NOT_FOUND)
        );

        if (!creatorRequest.getAgreedToAiPolicy()) throw new CustomException(ErrorCode.AI_POLICY_NOT_AGREED);

        Optional<Creators> optionalCreator = creatorRepository.findByUser(user);
        if (optionalCreator.isEmpty()) {
            // userrole에 ROLE_CREATOR 추가
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();

            user.getUserRoles().add(userRole);

            Creators creators = Creators.builder()
                    .user(user)
                    .penName(creatorRequest.getPenName())
                    .creatorType(CreatorType.valueOf(creatorRequest.getCreatorType()))
                    .agreedToAiPolicy(creatorRequest.getAgreedToAiPolicy())
                    .aiPolicyAgreedAt(LocalDateTime.now())
                    .build();

            creatorRepository.save(creators);
        }
    }


}
