package com.pageon.backend.config;

import com.opencsv.CSVReader;
import com.pageon.backend.common.enums.Provider;
import com.pageon.backend.common.enums.RoleType;
import com.pageon.backend.entity.Role;
import com.pageon.backend.entity.UserRole;
import com.pageon.backend.entity.Users;
import com.pageon.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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

    @Override
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
            boolean isFirstLine = true;

            csvReader.readNext();       // 첫 줄 skip
            String [] line;
            while ((line = csvReader.readNext()) != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate birthDate = LocalDate.parse(line[3].replaceAll("-", ""), formatter);
                Users users = new Users(
                        line[0],
                        passwordEncoder.encode(line[1]),
                        line[2],
                        birthDate,
                        Integer.valueOf(line[4]),
                        Provider.valueOf(line[5]),
                        Boolean.valueOf(line[7])
                );
                List<RoleType> roleTypes = Arrays.stream(line[8].split(","))
                        .map(String::trim)
                        .map(RoleType::valueOf) // 문자열 -> enum
                        .collect(Collectors.toList());

                roleTypes.forEach(roleType -> {
                    Role role = roleRepository.findByRoleType(roleType).orElseThrow(() -> new RuntimeException("role 없음"));

                    UserRole userRole = UserRole.builder()
                            .user(users)
                            .role(role)
                            .build();

                    users.getUserRoles().add(userRole);
                });

                userRepository.save(users);

            }

        } catch (Exception e) {
            log.error("에러 발생: {}", e);
        }
    }
}
