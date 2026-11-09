package com.prestige.renovation.common.exception;

import java.time.Instant;
import java.util.Map;

/**
 * JSON error body returned by every failing request:
 * {@code { timestamp, status, error, message, fieldErrors }}.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors) {
}
