package com.prestige.renovation.common.security;

import com.prestige.renovation.entity.Role;

/**
 * The authenticated caller, available in boundaries via {@code @AuthenticationPrincipal AuthUser me}.
 * It is null for visitors who are not logged in.
 */
public record AuthUser(Integer id, String email, Role role) {

    public boolean is(Role r) {
        return role == r;
    }
}
