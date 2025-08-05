package com.pageon.backend.service;

import com.pageon.backend.repository.WebtoonEpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ActiveProfiles("test")
@DisplayName("WebtoonEpisodeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class WebtoonEpisodeServiceTest {
    @InjectMocks
    private WebtoonEpisodeService webtoonEpisodeService;
    @Mock
    private WebtoonEpisodeRepository webtoonEpisodeRepository;

    @BeforeEach
    void setUp() {
        webtoonEpisodeRepository.deleteAll();
    }

}