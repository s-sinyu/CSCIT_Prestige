package com.prestige.renovation.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.prestige.renovation.entity.Role;

/** IDP CRUD with ownership, images, company info and visibility (acceptance criteria 3, 4, 10). */
class DesignerProjectIT extends AbstractIntegrationTest {

    @Test
    void designerManagesOwnProjectAndOthersCannotTouchIt() throws Exception {
        String designerA = login("a@test.sg", Role.INTERIOR_DESIGNER).token();
        String designerB = login("b@test.sg", Role.INTERIOR_DESIGNER).token();
        int kitchen = createCategory("Kitchen");
        int id = createProject(designerA, kitchen, "Bright Kitchen", "45000.50");

        getJson("/api/designer/projects/" + id, designerA).andExpect(status().isOk())
                .andExpect(jsonPath("$.budget").value(45000.50));
        getJson("/api/designer/projects/" + id, designerB).andExpect(status().isNotFound());
        putJson("/api/designer/projects/" + id, designerB, Map.of("title", "Hijack", "categoryId", kitchen,
                "status", "CURRENT")).andExpect(status().isNotFound());
        deleteReq("/api/designer/projects/" + id, designerB).andExpect(status().isNotFound());

        putJson("/api/designer/projects/" + id, designerA, Map.of("title", "Brighter Kitchen", "categoryId", kitchen,
                "status", "CURRENT")).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Brighter Kitchen"));

        getJson("/api/projects?categoryId=" + kitchen, null).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Brighter Kitchen"));
    }

    @Test
    void endDateBeforeStartDateIsRejected() throws Exception {
        String designer = login("a@test.sg", Role.INTERIOR_DESIGNER).token();
        int kitchen = createCategory("Kitchen");
        postJson("/api/designer/projects", designer, Map.of("title", "T", "categoryId", kitchen, "status", "CURRENT",
                "startDate", "2026-05-10", "endDate", "2026-05-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.endDateValid").exists());
    }

    @Test
    void deletedProjectDisappearsButFavouritesAndEnquiriesStay() throws Exception {
        String designer = login("a@test.sg", Role.INTERIOR_DESIGNER).token();
        Login customer = login("c@test.sg", Role.CUSTOMER);
        int id = createProject(designer, createCategory("Kitchen"), "Loft", "1000");
        postJson("/api/customer/favourites/" + id, customer.token(), Map.of()).andExpect(status().isNoContent());
        postJson("/api/projects/" + id + "/enquiries", customer.token(), Map.of("message", "Hi"))
                .andExpect(status().isCreated());

        deleteReq("/api/designer/projects/" + id, designer).andExpect(status().isNoContent());

        getJson("/api/projects/" + id, null).andExpect(status().isNotFound());
        getJson("/api/projects", null).andExpect(jsonPath("$.totalElements").value(0));
        getJson("/api/designer/projects", designer).andExpect(jsonPath("$.totalElements").value(0));
        getJson("/api/customer/favourites", customer.token()).andExpect(jsonPath("$.totalElements").value(0));
        getJson("/api/designer/enquiries", designer).andExpect(jsonPath("$.totalElements").value(1));
        assertThat(jdbc.queryForObject("select count(*) from favourites", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("select deleted from projects where id = ?", Boolean.class, id)).isTrue();
    }

    @Test
    void imagesUploadValidateAndServe() throws Exception {
        String designer = login("a@test.sg", Role.INTERIOR_DESIGNER).token();
        int id = createProject(designer, createCategory("Kitchen"), "Loft", null);

        var first = perform(multipart("/api/designer/projects/" + id + "/images")
                .file(new MockMultipartFile("file", "a.png", "image/png", pngBytes())), designer)
                .andExpect(status().isCreated()).andExpect(jsonPath("$.cover").value(true));
        var second = perform(multipart("/api/designer/projects/" + id + "/images")
                .file(new MockMultipartFile("file", "b.png", "image/png", pngBytes())), designer)
                .andExpect(status().isCreated()).andExpect(jsonPath("$.cover").value(false));
        perform(multipart("/api/designer/projects/" + id + "/images")
                .file(new MockMultipartFile("file", "evil.png", "image/png", "<svg onload=alert(1)>".getBytes())), designer)
                .andExpect(status().isBadRequest());
        perform(multipart("/api/designer/projects/" + id + "/images")
                .file(new MockMultipartFile("file", "doc.pdf", "application/pdf", pngBytes())), designer)
                .andExpect(status().isBadRequest());

        String url = body(first).get("url").asText();
        mvc.perform(get(url)).andExpect(status().isOk()).andExpect(header().string("Content-Type", "image/png"));
        mvc.perform(get("/api/files/..%2F..%2Fetc%2Fpasswd")).andExpect(status().is4xxClientError());

        int secondId = body(second).get("id").asInt();
        putJson("/api/designer/projects/" + id + "/images/" + secondId + "/cover", designer, Map.of())
                .andExpect(status().isOk());
        getJson("/api/projects", null).andExpect(jsonPath("$.content[0].coverImageUrl")
                .value(body(second).get("url").asText()));

        deleteReq("/api/designer/projects/" + id + "/images/" + secondId, designer).andExpect(status().isNoContent());
        getJson("/api/designer/projects/" + id, designer).andExpect(jsonPath("$.images.length()").value(1))
                .andExpect(jsonPath("$.images[0].cover").value(true));
    }

    @Test
    void companyInformationCrudAndPublicDisplay() throws Exception {
        String designer = login("a@test.sg", Role.INTERIOR_DESIGNER).token();
        getJson("/api/designer/company", designer).andExpect(status().isNotFound());
        postJson("/api/designer/company", designer, Map.of("companyName", "Kaze Studio", "registrationNumber",
                "2024ABC12K", "websiteUrl", "https://kaze.sg")).andExpect(status().isCreated());
        postJson("/api/designer/company", designer, Map.of("companyName", "Again")).andExpect(status().isConflict());
        putJson("/api/designer/company", designer, Map.of("companyName", "Kaze Studio", "websiteUrl", "not a url"))
                .andExpect(status().isBadRequest());
        putJson("/api/designer/profile", designer, Map.of("bio", "Hello", "yearsOfExperience", 12,
                "specialisation", "Japandi")).andExpect(status().isOk());

        int id = createProject(designer, createCategory("Kitchen"), "Loft", null);
        getJson("/api/projects/" + id, null).andExpect(jsonPath("$.designer.company.companyName").value("Kaze Studio"))
                .andExpect(jsonPath("$.designer.profile.yearsOfExperience").value(12))
                .andExpect(jsonPath("$.favourited").doesNotExist());
        getJson("/api/projects?designerName=kaze", null).andExpect(jsonPath("$.totalElements").value(1));

        deleteReq("/api/designer/company", designer).andExpect(status().isNoContent());
        getJson("/api/designer/projects", designer).andExpect(jsonPath("$.totalElements").value(1));
        // Without a company, the designer's full name is matched instead
        getJson("/api/projects?designerName=kaze", null).andExpect(jsonPath("$.totalElements").value(0));
        getJson("/api/projects?designerName=a@test", null).andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void suspendingDesignerHidesTheirContentUntilReactivated() throws Exception {
        Login designer = login("a@test.sg", Role.INTERIOR_DESIGNER);
        String admin = login("admin@test.sg", Role.USER_ADMIN).token();
        int id = createProject(designer.token(), createCategory("Kitchen"), "Loft", null);

        patchJson("/api/admin/users/" + designer.id() + "/status", admin, Map.of("status", "SUSPENDED"))
                .andExpect(status().isOk());
        getJson("/api/projects", null).andExpect(jsonPath("$.totalElements").value(0));
        getJson("/api/projects/" + id, null).andExpect(status().isNotFound());
        getJson("/api/designers/" + designer.id() + "/ratings", null).andExpect(status().isNotFound());

        patchJson("/api/admin/users/" + designer.id() + "/status", admin, Map.of("status", "ACTIVE"))
                .andExpect(status().isOk());
        getJson("/api/projects/" + id, null).andExpect(status().isOk());
    }
}
