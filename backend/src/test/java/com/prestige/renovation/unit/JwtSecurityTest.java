package com.prestige.renovation.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.prestige.renovation.common.config.AppProperties;
import com.prestige.renovation.common.security.AuthUser;
import com.prestige.renovation.common.security.JwtAuthenticationFilter;
import com.prestige.renovation.common.security.JwtService;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;
import com.prestige.renovation.support.EntityMockSupport;
import com.prestige.renovation.support.TestData;

/** JWT issuing/verification and suspended-user rejection on every request (FR-AUTH-3, FR-AUTH-4). */
class JwtSecurityTest extends EntityMockSupport {

    private final JwtService jwt = new JwtService(new AppProperties("Asia/Singapore",
            new AppProperties.Jwt("unit-test-secret-unit-test-secret-0123456789", 8), null, null, null, null));

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tokenRoundTrips() {
        String token = jwt.issue(5, "a@b.sg", Role.CUSTOMER);
        assertThat(jwt.parse(token)).contains(new AuthUser(5, "a@b.sg", Role.CUSTOMER));
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwt.issue(5, "a@b.sg", Role.CUSTOMER);
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("A") ? "BB" : "AA");
        assertThat(jwt.parse(tampered)).isEmpty();
        assertThat(jwt.parse("not-a-token")).isEmpty();
    }

    @Test
    void shortSecretIsRefusedAtStartup() {
        assertThatThrownBy(() -> new JwtService(new AppProperties("Asia/Singapore",
                new AppProperties.Jwt("short", 8), null, null, null, null)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void activeUserWithValidTokenIsAuthenticated() throws Exception {
        when(users.findById(5)).thenReturn(Optional.of(TestData.user(5, Role.CUSTOMER)));
        runFilter(jwt.issue(5, "user5@test.sg", Role.CUSTOMER));
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(((AuthUser) auth.getPrincipal()).id()).isEqualTo(5);
        assertThat(auth.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_CUSTOMER");
    }

    @Test
    void suspendedUserWithStillValidTokenIsNotAuthenticated() throws Exception {
        User suspended = TestData.suspended(TestData.user(5, Role.CUSTOMER));
        when(users.findById(5)).thenReturn(Optional.of(suspended));
        runFilter(jwt.issue(5, "user5@test.sg", Role.CUSTOMER));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void deletedUserWithStillValidTokenIsNotAuthenticated() throws Exception {
        User deleted = TestData.user(5, Role.CUSTOMER);
        deleted.softDelete(java.time.Instant.now());
        when(users.findById(5)).thenReturn(Optional.of(deleted));
        runFilter(jwt.issue(5, "user5@test.sg", Role.CUSTOMER));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private void runFilter(String token) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/me");
        request.addHeader("Authorization", "Bearer " + token);
        new JwtAuthenticationFilter(jwt).doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
    }
}
