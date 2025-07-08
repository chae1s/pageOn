package com.pageon.backend.config;

import com.opencsv.CSVReader;
import com.pageon.backend.entity.*;
import com.pageon.backend.common.enums.CreatorType;
import com.pageon.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(3)
@Profile("!test")
@RequiredArgsConstructor
public class InitContentData implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final KeywordsRepository keywordsRepository;
    private final CreatorRepository creatorRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initCreators();
        initCategory();
        initKeywords();

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
