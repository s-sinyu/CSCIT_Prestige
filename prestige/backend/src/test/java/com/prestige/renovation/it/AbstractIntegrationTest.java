package com.prestige.renovation.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prestige.renovation.common.security.JwtService;
import com.prestige.renovation.entity.Role;
import com.prestige.renovation.entity.User;
import com.prestige.renovation.entity.repository.UserRepository;

/**
 * Base class for integration tests. One PostgreSQL container (Testcontainers) is shared by all integration
 * tests; Flyway builds the schema from scratch; tables are emptied before each test. The developer's own
 * database is never used.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();
    }

    protected static final String PASSWORD = "Password1";

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper json;
    @Autowired
    protected JdbcTemplate jdbc;
    @Autowired
    protected PasswordEncoder encoder;
    @Autowired
    protected JwtService jwt;
    @Autowired
    protected UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbc.execute("TRUNCATE issue_reports, ratings, availability, enquiries, project_views, favourites, "
                + "project_images, projects, stored_files, categories, designer_companies, designer_profiles, users "
                + "RESTART IDENTITY CASCADE");
    }

    // ---------------------------------------------------------------- users and tokens

    /** Creates an active account directly in the database and returns its id. */
    protected int createUser(String email, Role role) {
        return userRepository.save(new User(email, encoder.encode(PASSWORD), "Name of " + email, "+65 9123 4567", role))
                .getId();
    }

    protected String tokenFor(int userId) {
        User u = userRepository.findById(userId).orElseThrow();
        return jwt.issue(u.getId(), u.getEmail(), u.getRole());
    }

    /** Creates a user and returns a bearer token for it. */
    protected Login login(String email, Role role) {
        int id = createUser(email, role);
        return new Login(id, tokenFor(id));
    }

    protected record Login(int id, String token) {
    }

    // ---------------------------------------------------------------- HTTP helpers

    protected ResultActions perform(MockHttpServletRequestBuilder builder, String token) throws Exception {
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return mvc.perform(builder);
    }

    protected ResultActions getJson(String url, String token) throws Exception {
        return perform(get(url), token);
    }

    protected ResultActions postJson(String url, String token, Object body) throws Exception {
        return perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body)), token);
    }

    protected ResultActions putJson(String url, String token, Object body) throws Exception {
        return perform(put(url).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body)), token);
    }

    protected ResultActions patchJson(String url, String token, Object body) throws Exception {
        return perform(patch(url).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body)), token);
    }

    protected ResultActions deleteReq(String url, String token) throws Exception {
        return perform(delete(url), token);
    }

    protected JsonNode body(ResultActions result) throws Exception {
        return json.readTree(result.andReturn().getResponse().getContentAsString());
    }

    // ---------------------------------------------------------------- domain helpers

    protected int createCategory(String name) {
        return jdbc.queryForObject("insert into categories (name, description, active, created_at, updated_at) "
                + "values (?, null, true, now(), now()) returning id", Integer.class, name);
    }

    protected int createProject(String designerToken, int categoryId, String title, String budget) throws Exception {
        Map<String, Object> request = new java.util.HashMap<>();
        request.put("title", title);
        request.put("description", "Description of " + title);
        request.put("categoryId", categoryId);
        request.put("status", "COMPLETED");
        request.put("budget", budget == null ? null : new BigDecimal(budget));
        request.put("location", "Tampines");
        return body(postJson("/api/designer/projects", designerToken, request)).get("id").asInt();
    }

    protected static byte[] pngBytes() throws Exception {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }
}
