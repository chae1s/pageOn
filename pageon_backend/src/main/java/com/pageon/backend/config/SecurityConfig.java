package com.pageon.backend.config;

import com.pageon.backend.security.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomCorsConfigurationSource configurationSource;
    private final CustomOauth2UserService customOauth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtProvider jwtProvider;




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(configurationSource))
                .csrf(csrfConfig -> csrfConfig.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/users/signup", "/api/users/check-email", "/api/users/check-nickname",
                                "/api/users/login", "/api/users/find-password", "/api/auth/refresh",
                                "/api/webnovels", "/api/webnovels/*", "/api/webtoons", "/api/webtoons/*", "/api/episodes/**", "/api/webnovels/daily/*", "/api/webtoons/daily/*",
                                "/api/keywords", "/api/search/**"
                        ).permitAll()
                        .requestMatchers("/api/webnovels/*/likes", "/api/webtoons/*/likes").authenticated()
                        .requestMatchers("/api/**").authenticated()
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
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("API Authentication failed: {}", authException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)

        ;

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return builder.build();
    }
}
