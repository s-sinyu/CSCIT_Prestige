package com.prestige.renovation.dto.auth;

public record LoginResponse(String token, long expiresInSeconds, UserSummary user) {
}
