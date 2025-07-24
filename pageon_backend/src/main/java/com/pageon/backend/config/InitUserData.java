package com.pageon.backend.config;

import com.opencsv.CSVReader;
import com.pageon.backend.common.enums.Gender;
import com.pageon.backend.common.enums.OAuthProvider;
import com.pageon.backend.common.enums.RoleType;
import com.pageon.backend.entity.Role;
import com.pageon.backend.entity.UserRole;
import com.pageon.backend.entity.User;
import com.pageon.backend.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(2)
@Profile("!test")
@RequiredArgsConstructor
public class InitUserData implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        initUsers();

    }

    private void initUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/users.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            CSVReader csvReader = new CSVReader(inputStreamReader);

            String [] line;
            while ((line = csvReader.readNext()) != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate birthDate = LocalDate.parse(line[3], formatter);

                User user = User.builder()
                        .email(line[0])
                        .password(passwordEncoder.encode(line[1]))
                        .nickname(line[2])
                        .birthDate(birthDate)
                        .pointBalance(Integer.valueOf(line[4]))
                        .oAuthProvider(OAuthProvider.EMAIL)
                        .gender(Gender.valueOf(line[5]))
                        .termsAgreed(true)
                        .build();

                List<RoleType> roleTypes = Arrays.stream(line[6].split(","))
                        .map(String::trim)
                        .map(RoleType::valueOf) // 문자열 -> enum
                        .toList();

                roleTypes.forEach(roleType -> {
                    Role role = roleRepository.findByRoleType(roleType).orElseThrow(() -> new RuntimeException("role 없음"));

                    UserRole userRole = UserRole.builder()
                            .user(user)
                            .role(role)
                            .build();

                    user.getUserRoles().add(userRole);
                });

                userRepository.save(user);

            }

        } catch (Exception e) {
            log.error("에러 발생: {}", e);
        }
    }
}
