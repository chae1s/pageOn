package com.pageon.backend.config;

import com.opencsv.CSVReader;
import com.pageon.backend.entity.*;
import com.pageon.backend.common.enums.ContentType;
import com.pageon.backend.exception.CustomException;
import com.pageon.backend.exception.ErrorCode;
import com.pageon.backend.repository.*;
import com.pageon.backend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@Order(3)
@Profile("!test")
@RequiredArgsConstructor
public class InitContentData implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final KeywordRepository keywordRepository;
    private final CreatorRepository creatorRepository;
    private final WebnovelRepository webnovelRepository;
    private final FileUploadService fileUploadService;
    private final WebtoonRepository webtoonRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initCreators();
        initCategory();
        initKeywords();
        initWebnovels();
        initWebtoons();

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
                User users = userRepository.findByIdAndIsDeletedFalse(Long.valueOf(line[1])).orElseThrow(() -> new RuntimeException("user 없음"));

                Creator creators = new Creator(line[0], users, ContentType.valueOf(line[2]), Boolean.valueOf(line[3]));

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
            categoryRepository.save(new Category("카테고리 미배정"));
        }
    }

    public void initKeywords() {
        if (keywordRepository.count() > 0) {
            return;
        }

        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/keywords.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            CSVReader csvReader = new CSVReader(inputStreamReader);

            String [] line;
            while ((line = csvReader.readNext()) != null) {
                Category category = categoryRepository.findById(Long.valueOf(line[0])).orElseThrow(() -> new RuntimeException("카테고리 없음"));

                Keyword keyWords = new Keyword(category, line[1]);

                keywordRepository.save(keyWords);

            }

        } catch (Exception e) {
            log.error("에러 발생: {}", e.getMessage());
        }
    }

    public void initWebnovels() {
        if (webnovelRepository.count() > 0) {
            return;
        }
        InputStream inputStream = getClass().getResourceAsStream("/data/webnovels.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        CSVReader csvReader = new CSVReader(inputStreamReader);

        String[] line;
        int i = 1;
        try {
            while ((line = csvReader.readNext()) != null) {

                Creator creator = creatorRepository.findById(Long.parseLong(line[3])).orElseThrow(() -> new CustomException(ErrorCode.CREATER_NOT_FOUND));
                File file = new File(String.format("/Users/user/Desktop/project/pageon_images/webnovels/%s", line[4]));
                String s3Url = fileUploadService.localFileUpload(file, String.format("webnovels/%d", i++));

                Webnovel webnovel = new Webnovel(
                        line[0],
                        line[2],
                        separateKeywords(line[1]),
                        creator,
                        s3Url,
                        line[5],
                        line[7],
                        Long.parseLong(line[8])
                );

                webnovelRepository.save(webnovel);


            }
        } catch (Exception e) {
            log.error("에러 발생: {}", e.getMessage());
            throw new RuntimeException();
        }


    }

    public void initWebtoons() {
        if (webtoonRepository.count() > 20) {
            return;
        }
        InputStream inputStream = getClass().getResourceAsStream("/data/webtoons.csv");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        CSVReader csvReader = new CSVReader(inputStreamReader);

        String[] line;
        int i = 1;
        try {
            while ((line = csvReader.readNext()) != null) {

                Creator creator = creatorRepository.findById(Long.parseLong(line[3])).orElseThrow(() -> new CustomException(ErrorCode.CREATER_NOT_FOUND));
                File file = new File(String.format("/Users/user/Desktop/project/pageon_images/webtoons/%s", line[4]));
                String s3Url = fileUploadService.localFileUpload(file, String.format("webtoons/%d", i++));

                Webtoon webtoon = new Webtoon(
                        line[0],
                        line[2],
                        separateKeywords(line[1]),
                        creator,
                        s3Url,
                        line[7],
                        line[5],
                        Long.parseLong(line[6])
                );

                webtoonRepository.save(webtoon);


            }
        } catch (Exception e) {
            log.error("에러 발생: {}", e.getMessage());
            throw new RuntimeException();
        }


    }

    private Set<Keyword> separateKeywords(String line) {
        String[] words = line.split(",");
        Set<Keyword> keywords = new HashSet<>();
        Category category = categoryRepository.findById(6L).orElseThrow(() -> new RuntimeException());
        for (int i = 0; i < words.length; i++) {
            Optional<Keyword> optionalKeyword = keywordRepository.findByName(words[i]);

            if (optionalKeyword.isPresent()) {
                keywords.add(optionalKeyword.get());
            } else {
                Keyword keyword = new Keyword(category, words[i]);

                keywordRepository.save(keyword);
            }


        }

        return keywords;
    }


}
