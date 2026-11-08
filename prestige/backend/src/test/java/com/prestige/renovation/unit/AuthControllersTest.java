package com.prestige.renovation.unit;

import static com.prestige.renovation.support.ApiAssertions.assertApiError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.prestige.renovation.common.config.AppProperties;
import com.prestige.renovation.common.security.JwtService;
import com.prestige.renovation.control.account.ChangePasswordController;
import com.prestige.renovation.control.auth.LoginController;
import com.prestige.renovation.control.auth.RegisterController;
import com.prestige.renovation.dto.auth.ChangePasswordRequest;
import com.prestige.renovation.dto.auth.LoginRequest;
import com.prestige.renovation.dto.auth.RegisterRequest;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;
import com.prestige.renovation.support.EntityMockSupport;
import com.prestige.renovation.support.TestData;

/** Registration, login and password rules (FR-AUTH-1 to 5). */
class AuthControllersTest extends EntityMockSupport {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private final JwtService jwt = new JwtService(new AppProperties("Asia/Singapore",
            new AppProperties.Jwt("unit-test-secret-unit-test-secret-0123456789", 8), null, null, null, null));

    @Test
    void registeringAsPlatformManagementIsRejected() {
        var controller = new RegisterController(encoder);
        var request = new RegisterRequest("pm@test.sg", "Password1", "PM", null, Role.PLATFORM_MANAGEMENT);
        assertApiError(() -> controller.register(request), HttpStatus.BAD_REQUEST);
        verify(users, never()).save(any());
    }

    @Test
    void duplicateEmailIsAConflict() {
        when(users.existsByEmailIgnoreCase("taken@test.sg")).thenReturn(true);
        var request = new RegisterRequest("Taken@Test.sg", "Password1", "Ann", null, Role.CUSTOMER);
        assertApiError(() -> new RegisterController(encoder).register(request), HttpStatus.CONFLICT);
    }

    @Test
    void registrationStoresAHashNotThePassword() {
        when(users.save(any(User.class))).thenAnswer(inv -> TestData.withId(inv.getArgument(0), 7));
        var summary = new RegisterController(encoder)
                .register(new RegisterRequest("new@test.sg", "Password1", " Ann Tan ", "", Role.INTERIOR_DESIGNER));
        assertThat(summary.id()).isEqualTo(7);
        assertThat(summary.fullName()).isEqualTo("Ann Tan");
        assertThat(summary.role()).isEqualTo(Role.INTERIOR_DESIGNER);
        verify(users).save(org.mockito.ArgumentMatchers.argThat(u ->
                !u.getPasswordHash().equals("Password1") && encoder.matches("Password1", u.getPasswordHash())
                        && u.getPhone() == null));
    }

    @Test
    void loginWithWrongPasswordIsUnauthorised() {
        User u = TestData.user(1, Role.CUSTOMER);
        u.setPasswordHash(encoder.encode("Password1"));
        when(users.findByEmailIgnoreCase("user1@test.sg")).thenReturn(Optional.of(u));
        assertApiError(() -> new LoginController(encoder, jwt).login(new LoginRequest("user1@test.sg", "wrong")),
                HttpStatus.UNAUTHORIZED);
    }

    @Test
    void suspendedUserCannotLogIn() {
        User u = TestData.suspended(TestData.user(1, Role.CUSTOMER));
        u.setPasswordHash(encoder.encode("Password1"));
        when(users.findByEmailIgnoreCase("user1@test.sg")).thenReturn(Optional.of(u));
        assertApiError(() -> new LoginController(encoder, jwt).login(new LoginRequest("user1@test.sg", "Password1")),
                HttpStatus.FORBIDDEN);
    }

    @Test
    void deletedUserCannotLogIn() {
        User u = TestData.user(1, Role.CUSTOMER);
        u.setPasswordHash(encoder.encode("Password1"));
        u.softDelete(java.time.Instant.now());
        when(users.findByEmailIgnoreCase("user1@test.sg")).thenReturn(Optional.of(u));
        assertApiError(() -> new LoginController(encoder, jwt).login(new LoginRequest("user1@test.sg", "Password1")),
                HttpStatus.UNAUTHORIZED);
    }

    @Test
    void successfulLoginReturnsTokenWithIdAndRole() {
        User u = TestData.user(3, Role.INTERIOR_DESIGNER);
        u.setPasswordHash(encoder.encode("Password1"));
        when(users.findByEmailIgnoreCase("user3@test.sg")).thenReturn(Optional.of(u));
        var response = new LoginController(encoder, jwt).login(new LoginRequest("user3@test.sg", "Password1"));
        var parsed = jwt.parse(response.token()).orElseThrow();
        assertThat(parsed.id()).isEqualTo(3);
        assertThat(parsed.role()).isEqualTo(Role.INTERIOR_DESIGNER);
        assertThat(response.expiresInSeconds()).isEqualTo(8 * 3600);
    }

    @Test
    void changingPasswordRequiresTheCurrentPassword() {
        User u = TestData.user(1, Role.CUSTOMER);
        u.setPasswordHash(encoder.encode("Password1"));
        when(users.findById(1)).thenReturn(Optional.of(u));
        var controller = new ChangePasswordController(encoder);
        assertApiError(() -> controller.changePassword(1, new ChangePasswordRequest("nope", "NewPass22")),
                HttpStatus.BAD_REQUEST);
        controller.changePassword(1, new ChangePasswordRequest("Password1", "NewPass22"));
        assertThat(encoder.matches("NewPass22", u.getPasswordHash())).isTrue();
    }
}
