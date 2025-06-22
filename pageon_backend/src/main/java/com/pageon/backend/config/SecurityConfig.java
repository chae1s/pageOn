package com.pageon.backend.config;

import com.pageon.backend.repository.UserRepository;
import com.pageon.backend.security.CustomOauth2UserService;
import com.pageon.backend.security.OAuth2SuccessHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomCorsConfigurationSource configurationSource;
    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("handler 작동 확인 : {}", oAuth2SuccessHandler != null);
        http
                .cors(cors -> cors.configurationSource(configurationSource))
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(FormLoginConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("http://localhost:3000")
                        .userInfoEndpoint(
                                userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOauth2UserService)

                        )
                        .failureHandler(((request, response, exception) -> response.sendRedirect("http://localhost:3000/login?error=oauth2")))
                        .successHandler(oAuth2SuccessHandler)
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
                        .permitAll()
                )

        ;

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
