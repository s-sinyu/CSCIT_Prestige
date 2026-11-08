package com.prestige.renovation.it;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.prestige.renovation.entity.Role;

/** Admin user management, issue reports and category management (acceptance criteria 9, 10, 11). */
class ManagementAndAdminIT extends AbstractIntegrationTest {

    @Test
    void adminCreatesPlatformManagerWhoCanLogIn() throws Exception {
        Login admin = login("admin@test.sg", Role.USER_ADMIN);
        postJson("/api/admin/users", admin.token(), Map.of("email", "pm@test.sg", "fullName", "Pat Manager",
                "password", "Manager12")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("PLATFORM_MANAGEMENT"));
        postJson("/api/auth/login", null, Map.of("email", "pm@test.sg", "password", "Manager12"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.user.role").value("PLATFORM_MANAGEMENT"));

        getJson("/api/admin/users?role=PLATFORM_MANAGEMENT", admin.token()).andExpect(jsonPath("$.totalElements").value(1));
        getJson("/api/admin/users?q=pat", admin.token()).andExpect(jsonPath("$.totalElements").value(1));

        patchJson("/api/admin/users/" + admin.id() + "/status", admin.token(), Map.of("status", "SUSPENDED"))
                .andExpect(status().isBadRequest());
        deleteReq("/api/admin/users/" + admin.id(), admin.token()).andExpect(status().isBadRequest());
    }

    @Test
    void adminEditsAndSoftDeletesAccounts() throws Exception {
        String admin = login("admin@test.sg", Role.USER_ADMIN).token();
        int customer = createUser("c@test.sg", Role.CUSTOMER);
        createUser("taken@test.sg", Role.CUSTOMER);

        putJson("/api/admin/users/" + customer, admin, Map.of("email", "taken@test.sg", "fullName", "C"))
                .andExpect(status().isConflict());
        putJson("/api/admin/users/" + customer, admin, Map.of("email", "new@test.sg", "fullName", "Chris"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("new@test.sg"));

        deleteReq("/api/admin/users/" + customer, admin).andExpect(status().isNoContent());
        getJson("/api/admin/users?q=chris", admin).andExpect(jsonPath("$.totalElements").value(0));
        getJson("/api/admin/users?q=chris&includeDeleted=true", admin).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].deleted").value(true));
        postJson("/api/auth/login", null, Map.of("email", "new@test.sg", "password", PASSWORD))
                .andExpect(status().isUnauthorized());
        // The deleted user's email stays reserved
        postJson("/api/auth/register", null, Map.of("email", "new@test.sg", "password", "Password1",
                "fullName", "X", "role", "CUSTOMER")).andExpect(status().isConflict());
    }

    @Test
    void issueReportLifecycle() throws Exception {
        String admin = login("admin@test.sg", Role.USER_ADMIN).token();
        Login designer = login("d@test.sg", Role.INTERIOR_DESIGNER);
        String customer = login("c@test.sg", Role.CUSTOMER).token();
        String otherCustomer = login("c2@test.sg", Role.CUSTOMER).token();
        int project = createProject(designer.token(), createCategory("Kitchen"), "Loft", null);

        int reportId = body(postJson("/api/issue-reports", customer, Map.of("type", "IDP_OR_DESIGNER",
                "targetProjectId", project, "title", "Copied photos", "description", "Seen elsewhere"))
                .andExpect(status().isCreated())).get("id").asInt();
        postJson("/api/issue-reports", customer, Map.of("type", "IDP_OR_DESIGNER", "title", "x", "description", "y"))
                .andExpect(status().isBadRequest());
        postJson("/api/issue-reports", designer.token(), Map.of("type", "PLATFORM_ISSUE", "title", "Bug",
                "description", "Upload fails")).andExpect(status().isCreated());

        getJson("/api/issue-reports/mine", customer).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].status").value("OPEN"));
        getJson("/api/issue-reports/mine/" + reportId, otherCustomer).andExpect(status().isNotFound());

        getJson("/api/admin/issue-reports?status=OPEN", admin).andExpect(jsonPath("$.totalElements").value(2));
        getJson("/api/admin/issue-reports?type=PLATFORM_ISSUE", admin).andExpect(jsonPath("$.totalElements").value(1));
        getJson("/api/admin/issue-reports/" + reportId, admin).andExpect(jsonPath("$.reporterEmail").value("c@test.sg"))
                .andExpect(jsonPath("$.targetProjectTitle").value("Loft"));

        patchJson("/api/admin/issue-reports/" + reportId + "/rectify", admin, Map.of()).andExpect(status().isOk())
                .andExpect(jsonPath("$.rectifiedByName").exists());
        patchJson("/api/admin/issue-reports/" + reportId + "/rectify", admin, Map.of()).andExpect(status().isConflict());
        getJson("/api/issue-reports/mine/" + reportId, customer).andExpect(jsonPath("$.status").value("RECTIFIED"))
                .andExpect(jsonPath("$.rectifiedAt").exists());
    }

    @Test
    void categoryManagement() throws Exception {
        String manager = login("m@test.sg", Role.PLATFORM_MANAGEMENT).token();
        String designer = login("d@test.sg", Role.INTERIOR_DESIGNER).token();

        int id = body(postJson("/api/management/categories", manager, Map.of("name", "Kitchen", "description", "K"))
                .andExpect(status().isCreated())).get("id").asInt();
        postJson("/api/management/categories", manager, Map.of("name", "KITCHEN")).andExpect(status().isConflict());
        putJson("/api/management/categories/" + id, manager, Map.of("name", "Kitchens", "description", "All kitchens"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Kitchens"));

        createProject(designer, id, "Loft", null);
        deleteReq("/api/management/categories/" + id, manager).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Deactivate")));

        patchJson("/api/management/categories/" + id + "/active", manager, Map.of("active", false))
                .andExpect(status().isOk());
        getJson("/api/categories", null).andExpect(jsonPath("$.length()").value(0));
        postJson("/api/designer/projects", designer, Map.of("title", "New", "categoryId", id, "status", "CURRENT"))
                .andExpect(status().isBadRequest());
        // Existing IDPs keep and display the inactive category
        getJson("/api/projects", null).andExpect(jsonPath("$.content[0].categoryName").value("Kitchens"));

        int unused = body(postJson("/api/management/categories", manager, Map.of("name", "Unused"))).get("id").asInt();
        deleteReq("/api/management/categories/" + unused, manager).andExpect(status().isNoContent());
    }
}
