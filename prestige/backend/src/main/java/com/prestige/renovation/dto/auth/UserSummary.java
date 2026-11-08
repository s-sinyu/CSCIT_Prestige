package com.prestige.renovation.dto.auth;

import com.prestige.renovation.entity.AccountStatus;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** The caller's own account details (never includes the password hash). */
public record UserSummary(Integer id, String email, String fullName, String phone, Role role, AccountStatus status) {

    public static UserSummary from(User u) {
        return new UserSummary(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(), u.getRole(), u.getStatus());
    }
}
