package com.prestige.renovation.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.prestige.renovation.entity.Role;

/** Registration, login, JWT, suspended-user rejection and role-based access (acceptance criteria 1, 2). */
class AuthAndAccessIT extends AbstractIntegrationTest {

    @Test
    void visitorRegistersAsCustomerAndLogsIn() throws Exception {
        postJson("/api/auth/register", null, Map.of("email", "Ann@Test.sg", "password", "Password1",
                "fullName", "Ann Tan", "role", "CUSTOMER"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ann@test.sg"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        var login = postJson("/api/auth/login", null, Map.of("email", "ann@test.sg", "password", "Password1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.role").value("CUSTOMER"));
        String token = body(login).get("token").asText();

        getJson("/api/me", token).andExpect(status().isOk()).andExpect(jsonPath("$.fullName").value("Ann Tan"));
    }

    @Test
    void registeringAsPlatformManagementIsRejected() throws Exception {
        postJson("/api/auth/register", null, Map.of("email", "pm@test.sg", "password", "Password1",
                "fullName", "PM", "role", "PLATFORM_MANAGEMENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.role").exists());
    }

    @Test
    void weakPasswordAndDuplicateEmailAreRejected() throws Exception {
        postJson("/api/auth/register", null, Map.of("email", "x@test.sg", "password", "short",
                "fullName", "X", "role", "CUSTOMER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
        createUser("dup@test.sg", Role.CUSTOMER);
        postJson("/api/auth/register", null, Map.of("email", "DUP@test.sg", "password", "Password1",
                "fullName", "X", "role", "CUSTOMER"))
                .andExpect(status().isConflict());
    }

    @Test
    void suspendedUserWithPreviouslyIssuedTokenIsRejected() throws Exception {
        Login customer = login("c@test.sg", Role.CUSTOMER);
        Login admin = login("admin@test.sg", Role.USER_ADMIN);
        getJson("/api/me", customer.token()).andExpect(status().isOk());

        patchJson("/api/admin/users/" + customer.id() + "/status", admin.token(), Map.of("status", "SUSPENDED"))
                .andExpect(status().isOk());

        getJson("/api/me", customer.token()).andExpect(status().isUnauthorized());
        postJson("/api/auth/login", null, Map.of("email", "c@test.sg", "password", PASSWORD))
                .andExpect(status().isForbidden());
    }

    @Test
    void endpointsEnforceRoles() throws Exception {
        String customer = login("c@test.sg", Role.CUSTOMER).token();
        String designer = login("d@test.sg", Role.INTERIOR_DESIGNER).token();
        String manager = login("m@test.sg", Role.PLATFORM_MANAGEMENT).token();
        String admin = login("a@test.sg", Role.USER_ADMIN).token();

        // Not logged in
        getJson("/api/customer/favourites", null).andExpect(status().isUnauthorized());
        getJson("/api/designer/projects", null).andExpect(status().isUnauthorized());
        getJson("/api/management/reports", null).andExpect(status().isUnauthorized());
        getJson("/api/admin/users", null).andExpect(status().isUnauthorized());
        // Wrong role
        getJson("/api/designer/projects", customer).andExpect(status().isForbidden());
        getJson("/api/customer/favourites", designer).andExpect(status().isForbidden());
        getJson("/api/management/categories", admin).andExpect(status().isForbidden());
        getJson("/api/admin/users", manager).andExpect(status().isForbidden());
        getJson("/api/issue-reports/mine", manager).andExpect(status().isForbidden());
        getJson("/api/issue-reports/mine", admin).andExpect(status().isForbidden());
        // Right role
        getJson("/api/designer/projects", designer).andExpect(status().isOk());
        getJson("/api/customer/favourites", customer).andExpect(status().isOk());
        getJson("/api/management/categories", manager).andExpect(status().isOk());
        getJson("/api/admin/users", admin).andExpect(status().isOk());
        // Public
        getJson("/api/projects", null).andExpect(status().isOk());
        getJson("/api/categories", null).andExpect(status().isOk());
    }

    @Test
    void errorsUseTheStandardJsonShape() throws Exception {
        var result = getJson("/api/admin/users", null).andExpect(status().isUnauthorized());
        var json = body(result);
        assertThat(json.has("timestamp")).isTrue();
        assertThat(json.get("status").asInt()).isEqualTo(401);
        assertThat(json.has("message")).isTrue();
    }

    @Test
    void ownProfileAndPasswordCanBeChanged() throws Exception {
        Login c = login("c@test.sg", Role.CUSTOMER);
        putJson("/api/me", c.token(), Map.of("fullName", "New Name", "phone", "+65 8888 1111"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.fullName").value("New Name"))
                .andExpect(jsonPath("$.email").value("c@test.sg"));
        putJson("/api/me/password", c.token(), Map.of("currentPassword", "wrong1234", "newPassword", "NewPass99"))
                .andExpect(status().isBadRequest());
        putJson("/api/me/password", c.token(), Map.of("currentPassword", PASSWORD, "newPassword", "NewPass99"))
                .andExpect(status().isNoContent());
        postJson("/api/auth/login", null, Map.of("email", "c@test.sg", "password", "NewPass99"))
                .andExpect(status().isOk());
    }

    @Test
    void flywayBuiltTheSchemaFromScratch() {
        Integer applied = jdbc.queryForObject(
                "select count(*) from flyway_schema_history where success = true", Integer.class);
        assertThat(applied).isGreaterThanOrEqualTo(1);
    }
}
