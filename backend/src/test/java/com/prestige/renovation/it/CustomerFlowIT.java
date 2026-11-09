package com.prestige.renovation.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.prestige.renovation.entity.Role;

/** Search, views, favourites, enquiries, availability and ratings (acceptance criteria 5 to 8, 13, 14). */
class CustomerFlowIT extends AbstractIntegrationTest {

    @Test
    void searchFiltersCombineAndPaginate() throws Exception {
        String designer = login("d@test.sg", Role.INTERIOR_DESIGNER).token();
        int kitchen = createCategory("Kitchen");
        int bath = createCategory("Bathroom");
        createProject(designer, kitchen, "Scandi Kitchen", "20000");
        createProject(designer, kitchen, "Industrial Kitchen", "80000");
        createProject(designer, bath, "Spa Bathroom", "30000");
        createProject(designer, bath, "Tiny Bathroom", null);

        getJson("/api/projects?keyword=KITCHEN", null).andExpect(jsonPath("$.totalElements").value(2));
        getJson("/api/projects?categoryId=" + bath, null).andExpect(jsonPath("$.totalElements").value(2));
        getJson("/api/projects?budgetMin=25000", null).andExpect(jsonPath("$.totalElements").value(2));
        // Projects without a budget are excluded once a budget filter is used
        getJson("/api/projects?budgetMax=50000", null).andExpect(jsonPath("$.totalElements").value(2));
        getJson("/api/projects?keyword=kitchen&budgetMax=50000", null)
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Scandi Kitchen"));
        getJson("/api/projects?status=CURRENT", null).andExpect(jsonPath("$.totalElements").value(0));
        getJson("/api/projects?size=3&page=1", null).andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalPages").value(2));
        getJson("/api/projects?sort=budgetDesc", null).andExpect(jsonPath("$.content[0].title").value("Industrial Kitchen"));
        getJson("/api/projects?budgetMin=50&budgetMax=10", null).andExpect(status().isBadRequest());
    }

    @Test
    void viewsAreCountedOncePerCustomerPerDay() throws Exception {
        String designer = login("d@test.sg", Role.INTERIOR_DESIGNER).token();
        Login customer = login("c@test.sg", Role.CUSTOMER);
        int id = createProject(designer, createCategory("Kitchen"), "Loft", null);

        for (int i = 0; i < 3; i++) {
            getJson("/api/projects/" + id, customer.token()).andExpect(status().isOk());
        }
        getJson("/api/projects/" + id, null).andExpect(status().isOk());       // visitor
        getJson("/api/projects/" + id, designer).andExpect(status().isOk());   // owning designer
        getJson("/api/designer/projects/" + id, designer).andExpect(jsonPath("$.views").value(1));

        // A view on a different day counts again
        jdbc.update("update project_views set view_date = view_date - 1");
        getJson("/api/projects/" + id, customer.token()).andExpect(status().isOk());
        getJson("/api/designer/projects/" + id, designer).andExpect(jsonPath("$.views").value(2));
        assertThat(jdbc.queryForObject("select max(view_date) from project_views", LocalDate.class))
                .isEqualTo(LocalDate.now(ZoneId.of("Asia/Singapore")));
    }

    @Test
    void favouritesAreIdempotentAndDriveTheShortlistFigure() throws Exception {
        String designer = login("d@test.sg", Role.INTERIOR_DESIGNER).token();
        String c1 = login("c1@test.sg", Role.CUSTOMER).token();
        String c2 = login("c2@test.sg", Role.CUSTOMER).token();
        int id = createProject(designer, createCategory("Kitchen"), "Loft", null);

        postJson("/api/customer/favourites/" + id, c1, Map.of()).andExpect(status().isNoContent());
        postJson("/api/customer/favourites/" + id, c1, Map.of()).andExpect(status().isNoContent());
        postJson("/api/customer/favourites/" + id, c2, Map.of()).andExpect(status().isNoContent());
        getJson("/api/customer/favourites", c1).andExpect(jsonPath("$.totalElements").value(1));
        getJson("/api/projects/" + id, c1).andExpect(jsonPath("$.favourited").value(true));
        getJson("/api/designer/projects", designer).andExpect(jsonPath("$.content[0].shortlists").value(2));

        deleteReq("/api/customer/favourites/" + id, c2).andExpect(status().isNoContent());
        getJson("/api/designer/projects", designer).andExpect(jsonPath("$.content[0].shortlists").value(1));
        getJson("/api/designer/dashboard", designer).andExpect(jsonPath("$.totalShortlists").value(1))
                .andExpect(jsonPath("$.totalProjects").value(1));
    }

    @Test
    void enquiryFlowFromCustomerToDesigner() throws Exception {
        String designer = login("d@test.sg", Role.INTERIOR_DESIGNER).token();
        String other = login("o@test.sg", Role.INTERIOR_DESIGNER).token();
        String customer = login("c@test.sg", Role.CUSTOMER).token();
        int id = createProject(designer, createCategory("Kitchen"), "Loft", null);

        int enquiryId = body(postJson("/api/projects/" + id + "/enquiries", customer, Map.of("message", "Is May free?"))
                .andExpect(status().isCreated())).get("id").asInt();
        postJson("/api/projects/" + id + "/enquiries", customer, Map.of("message", "")).andExpect(status().isBadRequest());

        getJson("/api/designer/enquiries?status=NEW", designer).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].customerName").value("Name of c@test.sg"));
        getJson("/api/designer/dashboard", designer).andExpect(jsonPath("$.newEnquiries").value(1));
        getJson("/api/designer/enquiries/" + enquiryId, other).andExpect(status().isNotFound());

        getJson("/api/designer/enquiries/" + enquiryId, designer).andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("c@test.sg"))
                .andExpect(jsonPath("$.customerPhone").value("+65 9123 4567"))
                .andExpect(jsonPath("$.status").value("READ"));
        patchJson("/api/designer/enquiries/" + enquiryId + "/status", designer, Map.of("status", "REPLIED"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("REPLIED"));
        patchJson("/api/designer/enquiries/" + enquiryId + "/status", designer, Map.of("status", "NEW"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void availabilityCalendar() throws Exception {
        Login designer = login("d@test.sg", Role.INTERIOR_DESIGNER);
        String customer = login("c@test.sg", Role.CUSTOMER).token();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Singapore"));
        LocalDate future = today.plusDays(3);

        putJson("/api/designer/availability/" + future, designer.token(), Map.of("status", "UNAVAILABLE"))
                .andExpect(status().isOk());
        putJson("/api/designer/availability/" + today.minusDays(1), designer.token(), Map.of("status", "UNAVAILABLE"))
                .andExpect(status().isBadRequest());

        String month = future.toString().substring(0, 7);
        var result = getJson("/api/customer/designers/" + designer.id() + "/availability?month=" + month, customer)
                .andExpect(status().isOk());
        var days = body(result).get("days");
        assertThat(days.size()).isEqualTo(future.lengthOfMonth());
        for (var day : days) {
            String expected = day.get("date").asText().equals(future.toString()) ? "UNAVAILABLE" : "AVAILABLE";
            assertThat(day.get("status").asText()).isEqualTo(expected);
        }
        getJson("/api/customer/designers/" + designer.id() + "/availability", null).andExpect(status().isUnauthorized());
        getJson("/api/customer/designers/" + designer.id() + "/availability?month=bad", customer)
                .andExpect(status().isBadRequest());
    }

    @Test
    void ratingRules() throws Exception {
        Login designer = login("d@test.sg", Role.INTERIOR_DESIGNER);
        Login customer = login("c@test.sg", Role.CUSTOMER);
        String admin = login("a@test.sg", Role.USER_ADMIN).token();
        int id = createProject(designer.token(), createCategory("Kitchen"), "Loft", null);

        postJson("/api/customer/ratings", customer.token(), Map.of("designerId", designer.id(), "score", 4))
                .andExpect(status().isCreated());
        postJson("/api/customer/ratings", customer.token(), Map.of("designerId", designer.id(), "score", 2))
                .andExpect(status().isConflict());
        int projectRating = body(postJson("/api/customer/ratings", customer.token(),
                Map.of("designerId", designer.id(), "projectId", id, "score", 5, "comment", "Lovely"))
                .andExpect(status().isCreated())).get("id").asInt();
        postJson("/api/customer/ratings", customer.token(), Map.of("designerId", designer.id(), "projectId", id, "score", 1))
                .andExpect(status().isConflict());
        postJson("/api/customer/ratings", designer.token(), Map.of("designerId", designer.id(), "score", 5))
                .andExpect(status().isForbidden());
        postJson("/api/customer/ratings", customer.token(), Map.of("designerId", designer.id(), "score", 6))
                .andExpect(status().isBadRequest());

        getJson("/api/designers/" + designer.id() + "/ratings", null).andExpect(status().isOk())
                .andExpect(jsonPath("$.average").value(4.5)).andExpect(jsonPath("$.count").value(2));
        getJson("/api/designers/" + designer.id() + "/ratings?projectId=" + id, null)
                .andExpect(jsonPath("$.ratings.totalElements").value(1));

        putJson("/api/customer/ratings/" + projectRating, customer.token(), Map.of("score", 3))
                .andExpect(status().isOk()).andExpect(jsonPath("$.score").value(3));
        deleteReq("/api/admin/ratings/" + projectRating, admin).andExpect(status().isNoContent());
        getJson("/api/designers/" + designer.id() + "/ratings", null).andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.average").value(4.0));
    }
}
