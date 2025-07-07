package com.pageon.backend.config;

import com.opencsv.CSVReader;
import com.pageon.backend.entity.*;
import com.pageon.backend.common.base.enums.CreatorType;
import com.pageon.backend.common.base.enums.Provider;
import com.pageon.backend.common.base.enums.RoleType;
import com.pageon.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
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
@Profile("!test")
@RequiredArgsConstructor
public class InitData implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final KeywordsRepository keywordsRepository;
    private final CreatorRepository creatorRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initRoles();
        initUsers();
        initCreators();
        initCategory();
        initKeywords();

    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_USER"));
            roleRepository.save(new Role("ROLE_CREATOR"));
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
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

    private void initCreators() {
        if (creatorRepository.count() > 0) {
            return;
        }

        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/creators.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            CSVReader csvReader = new CSVReader(inputStreamReader);

            String [] line;
            while ((line = csvReader.readNext()) != null) {
                Users users = userRepository.findByIdAndIsDeletedFalse(Long.valueOf(line[1])).orElseThrow(() -> new RuntimeException("user 없음"));

                Creators creators = new Creators(line[0], users, CreatorType.valueOf(line[2]), Boolean.valueOf(line[3]));

                creatorRepository.save(creators);

            }

        } catch (Exception e) {
            log.error("에러 발생: {}", e);
        }
    }

    private void initCategory() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("장르"));
            categoryRepository.save(new Category("소재"));
            categoryRepository.save(new Category("배경"));
            categoryRepository.save(new Category("분위기"));
            categoryRepository.save(new Category("형식"));
        }
    }

    public void initKeywords() {
        if (keywordsRepository.count() > 0) {
            return;
        }

        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/keywords.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            CSVReader csvReader = new CSVReader(inputStreamReader);

            String [] line;
            while ((line = csvReader.readNext()) != null) {
                Category category = categoryRepository.findById(Long.valueOf(line[0])).orElseThrow(() -> new RuntimeException("카테고리 없음"));

                Keywords keyWords = new Keywords(category, line[1]);

                keywordsRepository.save(keyWords);

            }

        } catch (Exception e) {
            log.error("에러 발생: {}", e);
        }
    }


}
