package com.prestige.renovation.control.common;

import com.prestige.renovation.common.exception.ApiException;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/**
 * Helpers shared by control classes. Endpoint security already restricts roles; these checks repeat the rule
 * at service level so business logic never relies on the web layer alone (defence in depth).
 */
public final class ControlSupport {

    private ControlSupport() {
    }

    /** Loads the calling user, who must still be active. */
    public static User requireActiveUser(Integer userId) {
        return User.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> ApiException.unauthorized("Authentication is required"));
    }

    /** Loads the calling user and checks the role. */
    public static User requireRole(Integer userId, Role role) {
        User user = requireActiveUser(userId);
        if (!user.hasRole(role)) {
            throw ApiException.forbidden("You do not have permission to perform this action");
        }
        return user;
    }

    /** Loads an active interior designer by id, or 404 (used for public designer pages). */
    public static User requireActiveDesigner(Integer designerId) {
        return User.findById(designerId)
                .filter(u -> u.hasRole(Role.INTERIOR_DESIGNER) && u.isActive())
                .orElseThrow(() -> ApiException.notFound("Designer not found"));
    }

    /** Trims a string; blank becomes null. */
    public static String clean(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
