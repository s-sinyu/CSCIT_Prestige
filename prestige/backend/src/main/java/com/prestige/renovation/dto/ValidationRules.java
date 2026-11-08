package com.prestige.renovation.dto;

/** Shared validation constants so request DTOs and the React forms enforce the same rules. */
public final class ValidationRules {

    /** At least 8 characters with at least one letter and one digit (FR-AUTH-2). */
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{8,100}$";
    public static final String PASSWORD_MESSAGE =
            "Password must be at least 8 characters and contain a letter and a digit";
    public static final String PHONE_REGEX = "^$|^[+0-9 ()-]{6,30}$";
    public static final String PHONE_MESSAGE = "Phone may contain digits, spaces, +, - and brackets (6 to 30 characters)";

    private ValidationRules() {
    }
}
