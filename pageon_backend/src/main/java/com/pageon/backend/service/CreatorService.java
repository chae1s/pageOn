package com.pageon.backend.service;

import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.common.enums.RoleType;
import com.pageon.backend.dto.request.RegisterCreatorRequest;
import com.pageon.backend.entity.Creator;
import com.pageon.backend.entity.Role;
import com.pageon.backend.entity.UserRole;
import com.pageon.backend.entity.User;
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
    private final RoleRepository roleRepository;
    private final CommonService commonService;

    @Transactional
    public void registerCreator(PrincipalUser principalUser, RegisterCreatorRequest creatorRequest) {
        String email = principalUser.getUsername();
        User user = commonService.findUserByEmail(principalUser.getUsername());

        Role role = roleRepository.findByRoleType(RoleType.ROLE_CREATOR).orElseThrow(
                () -> new CustomException(ErrorCode.ROLE_NOT_FOUND)
        );

        if (!creatorRequest.getAgreedToAiPolicy()) throw new CustomException(ErrorCode.AI_POLICY_NOT_AGREED);

        Optional<Creator> optionalCreator = creatorRepository.findByUser(user);
        if (optionalCreator.isEmpty()) {
            // userrole에 ROLE_CREATOR 추가
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();

            user.getUserRoles().add(userRole);

            Creator creators = Creator.builder()
                    .user(user)
                    .penName(creatorRequest.getPenName())
                    .contentType(ContentType.valueOf(creatorRequest.getContentType()))
                    .agreedToAiPolicy(creatorRequest.getAgreedToAiPolicy())
                    .aiPolicyAgreedAt(LocalDateTime.now())
                    .build();

            creatorRepository.save(creators);
        }
    }


}
