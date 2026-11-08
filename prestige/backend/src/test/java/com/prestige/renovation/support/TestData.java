package com.prestige.renovation.support;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.springframework.test.util.ReflectionTestUtils;

import com.prestige.renovation.entity.AccountStatus;
import com.prestige.renovation.entity.Category;
import com.prestige.renovation.entity.InteriorDesignProject;
import com.prestige.renovation.entity.ProjectStatus;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;

/** Builders for entities used in unit tests. */
public final class TestData {

    public static final ZoneId SGT = ZoneId.of("Asia/Singapore");

    private TestData() {
    }

    /** A clock fixed at the given Singapore local date-time, e.g. "2026-11-10T10:00". */
    public static Clock clockAt(String localDateTime) {
        Instant instant = java.time.LocalDateTime.parse(localDateTime).atZone(SGT).toInstant();
        return Clock.fixed(instant, SGT);
    }

    public static User user(int id, Role role) {
        User u = new User("user" + id + "@test.sg", "$2a$hash", "User " + id, null, role);
        u.setId(id);
        return u;
    }

    public static User suspended(User u) {
        u.changeStatus(AccountStatus.SUSPENDED);
        return u;
    }

    public static Category category(int id, String name, boolean active) {
        Category c = new Category(name, null, active);
        ReflectionTestUtils.setField(c, "id", id);
        return c;
    }

    public static InteriorDesignProject project(int id, User designer, Category category) {
        InteriorDesignProject p = new InteriorDesignProject(designer);
        p.update(category, "Project " + id, "Description", ProjectStatus.COMPLETED, new BigDecimal("50000.00"),
                null, null, "Tampines");
        ReflectionTestUtils.setField(p, "id", id);
        return p;
    }

    public static <T> T withId(T entity, int id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
