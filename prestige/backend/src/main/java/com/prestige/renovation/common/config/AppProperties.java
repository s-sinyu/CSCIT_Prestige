package com.prestige.renovation.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed view of the {@code app.*} configuration keys (see application.yml).
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String zone,
        Jwt jwt,
        Cors cors,
        Admin admin,
        Images images,
        Seed seed) {

    public record Jwt(String secret, int expiryHours) { }

    public record Cors(String allowedOrigin) { }

    public record Admin(String email, String password) { }

    public record Images(int maxPerProject, long maxBytes) { }

    public record Seed(boolean enabled, int size, String password, boolean exitAfterSeed) { }
}
