package com.prestige.renovation.dto.admin;

import java.time.Instant;

import com.prestige.renovation.entity.AccountStatus;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

public record AdminUserResponse(Integer id, String email, String fullName, String phone, Role role,
                                AccountStatus status, boolean deleted, Instant deletedAt,
                                Instant createdAt, Instant updatedAt) {

    public static AdminUserResponse from(User u) {
        return new AdminUserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(), u.getRole(),
                u.getStatus(), u.isDeleted(), u.getDeletedAt(), u.getCreatedAt(), u.getUpdatedAt());
    }
}
