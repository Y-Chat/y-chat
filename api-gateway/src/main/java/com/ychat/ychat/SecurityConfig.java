package com.ychat.ychat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    // https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter

    @Value("${ychat.cors.allowed.url}")
    private String corsAllowedUrl;

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors((cors) -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of(corsAllowedUrl));
                    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "HEAD"));
                    configuration.applyPermitDefaultValues();
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", configuration);
                    cors.configurationSource(source);
                })
                .authorizeExchange((e) -> e.anyExchange().permitAll()); // TODO BST Has to be changed later when jwt authentification is enabled

        return http.build();
    }
}
