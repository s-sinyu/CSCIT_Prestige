package com.prestige.renovation.seed;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.prestige.renovation.common.config.AppProperties;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/**
 * Creates the initial User Admin at startup if none exists (PRD 12.4), from APP_ADMIN_EMAIL / APP_ADMIN_PASSWORD.
 * If those variables are missing, creation is skipped with a warning. There is no other way to create a User Admin.
 */
@Component
@Order(1)
public class InitialAdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(InitialAdminSeeder.class);

    private final AppProperties properties;
    private final PasswordEncoder passwordEncoder;

    public InitialAdminSeeder(AppProperties properties, PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (User.anyWithRole(Role.USER_ADMIN)) {
            return;
        }
        String email = properties.admin() == null ? null : properties.admin().email();
        String password = properties.admin() == null ? null : properties.admin().password();
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            log.warn("No User Admin exists and APP_ADMIN_EMAIL / APP_ADMIN_PASSWORD are not set: skipping creation");
            return;
        }
        String normalised = email.trim().toLowerCase(Locale.ROOT);
        if (User.emailTaken(normalised)) {
            log.warn("Cannot create the initial User Admin: email {} is already used by another account", normalised);
            return;
        }
        new User(normalised, passwordEncoder.encode(password), "User Admin", null, Role.USER_ADMIN).save();
        log.info("Created initial User Admin account {}", normalised);
    }
}
