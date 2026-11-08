package com.prestige.renovation.common.security;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prestige.renovation.common.config.AppProperties;
import com.prestige.renovation.common.exception.GlobalExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Role checks per endpoint (PRD section 2). Ownership checks happen in the control classes.
 */
@Configuration
public class SecurityConfig {

    private static final String CUSTOMER = "CUSTOMER";
    private static final String DESIGNER = "INTERIOR_DESIGNER";
    private static final String MANAGEMENT = "PLATFORM_MANAGEMENT";
    private static final String ADMIN = "USER_ADMIN";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService, ObjectMapper mapper,
                                                   CorsConfigurationSource corsSource) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(c -> c.configurationSource(corsSource))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Infrastructure
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/error", "/actuator/health/**", "/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Public
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories", "/api/projects", "/api/projects/*",
                        "/api/designers/*/ratings", "/api/files/**").permitAll()
                // Any logged-in user
                .requestMatchers("/api/me", "/api/me/**").authenticated()
                // Customer
                .requestMatchers(HttpMethod.POST, "/api/projects/*/enquiries").hasRole(CUSTOMER)
                .requestMatchers("/api/customer/**").hasRole(CUSTOMER)
                // Interior designer
                .requestMatchers("/api/designer/**").hasRole(DESIGNER)
                // Issue reports (customer and designer)
                .requestMatchers("/api/issue-reports/**", "/api/issue-reports").hasAnyRole(CUSTOMER, DESIGNER)
                // Platform management
                .requestMatchers("/api/management/**").hasRole(MANAGEMENT)
                // User admin
                .requestMatchers("/api/admin/**").hasRole(ADMIN)
                .anyRequest().denyAll())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) ->
                        writeError(res, mapper, HttpStatus.UNAUTHORIZED, "Authentication is required"))
                .accessDeniedHandler((req, res, ex) ->
                        writeError(res, mapper, HttpStatus.FORBIDDEN,
                                "You do not have permission to perform this action")))
            .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(AppProperties properties) {
        CorsConfiguration config = new CorsConfiguration();
        String origins = properties.cors() == null ? "" : properties.cors().allowedOrigin();
        config.setAllowedOrigins(List.of(origins.split("\\s*,\\s*")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Content-Disposition"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private static void writeError(HttpServletResponse res, ObjectMapper mapper, HttpStatus status, String message)
            throws java.io.IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getOutputStream(), GlobalExceptionHandler.body(status, message, Map.of()));
    }
}
