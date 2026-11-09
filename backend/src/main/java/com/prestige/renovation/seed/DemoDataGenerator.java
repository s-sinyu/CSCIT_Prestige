package com.prestige.renovation.seed;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.prestige.renovation.common.config.AppProperties;

/**
 * Generates a realistic, deterministic test dataset (spec: "about 100 records of each data type").
 *
 * <p>Runs when {@code app.seed.enabled=true}: the {@code dev} profile loads a small set (size 10) and the
 * {@code seed} profile loads the full set (size 100) into whatever database is configured, e.g. the demo server.
 * It is a test-data tool, not part of the BCE domain: it bulk-inserts with JDBC batches so loading a remote
 * database takes seconds rather than minutes. It does nothing if the demo data is already present.</p>
 *
 * <p>All demo accounts use the password from {@code app.seed.password} (default {@code Password123}).</p>
 */
@Component
@Order(2)
public class DemoDataGenerator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataGenerator.class);
    static final String DOMAIN = "@demo.prestige.sg";

    private static final String[] PROPERTY_TYPES = {"HDB 3-Room", "HDB 4-Room", "HDB 5-Room", "HDB Executive",
        "Condominium", "Landed Terrace", "Semi-Detached", "Bungalow", "Shophouse", "Office", "Retail", "F&B"};
    private static final String[] SPACES = {"Full Home", "Kitchen", "Bathroom", "Living Room", "Bedroom",
        "Study", "Balcony", "Carpentry", "Lighting"};
    private static final String[] STYLES = {"Scandinavian", "Japandi", "Modern Luxe", "Industrial", "Minimalist",
        "Contemporary", "Mid-Century", "Tropical", "Classic", "Wabi-Sabi", "Coastal", "Eclectic"};
    private static final String[] FIRST = {"Aaron", "Bella", "Chloe", "Daniel", "Elaine", "Farhan", "Grace", "Hui Min",
        "Isaac", "Jasmine", "Kumar", "Li Ting", "Marcus", "Nadia", "Oliver", "Priya", "Qi Wei", "Rachel", "Sean",
        "Tanya", "Umar", "Vanessa", "Wei Jie", "Xin Yi", "Yusof", "Zoe", "Benjamin", "Charmaine", "Dinesh", "Esther"};
    private static final String[] LAST = {"Tan", "Lim", "Lee", "Ng", "Ong", "Wong", "Goh", "Chua", "Chan", "Koh",
        "Teo", "Ang", "Yeo", "Tay", "Ho", "Low", "Sim", "Pillai", "Rahman", "Kaur", "Fernandez", "Ismail"};
    private static final String[] COMPANY_WORDS = {"Atelier", "Studio", "Interiors", "Design House", "Space Co",
        "Living", "Concepts", "Workshop", "Collective", "Habitat"};
    private static final String[] COMPANY_PREFIX = {"Linea", "Oak & Ivory", "Kaze", "Monolith", "Haven", "Nook",
        "Arcadia", "Terra", "Lumen", "Verde", "Solace", "Ember", "Willow", "Granite", "Marble Bay", "Kin", "Aurum",
        "Folio", "Northlight", "Sable"};
    private static final String[] TOWNS = {"Tampines", "Punggol", "Bishan", "Toa Payoh", "Jurong East", "Bukit Timah",
        "Sengkang", "Clementi", "Queenstown", "Ang Mo Kio", "Woodlands", "Pasir Ris", "Bedok", "Novena", "Tiong Bahru"};
    private static final String[] ENQUIRY_LINES = {
        "Hi, we just collected the keys to our flat and love this look. Could we arrange a consultation?",
        "What would a similar renovation cost for a smaller unit? Our budget is flexible.",
        "Is the carpentry in this project custom made? We would like something similar for our study.",
        "Could you share how long this project took from start to handover?",
        "We are interested in this style for our new condo. Are you available next month?"};
    private static final String[] RATING_COMMENTS = {
        "Very professional and responsive throughout the project.", "Great eye for detail, would recommend.",
        "Good design but the timeline slipped a little.", "Excellent space planning for our small flat.",
        "Communication could be better, but the result looks lovely.", null};

    private final AppProperties properties;
    private final JdbcTemplate jdbc;
    private final TransactionTemplate tx;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final ApplicationContext context;

    public DemoDataGenerator(AppProperties properties, JdbcTemplate jdbc, TransactionTemplate tx,
                             PasswordEncoder passwordEncoder, Clock clock, ApplicationContext context) {
        this.properties = properties;
        this.jdbc = jdbc;
        this.tx = tx;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.Seed seed = properties.seed();
        if (seed == null || !seed.enabled()) {
            return;
        }
        Integer existing = jdbc.queryForObject("select count(*) from users where email like ?", Integer.class,
                "%" + DOMAIN);
        if (existing != null && existing > 0) {
            log.info("Demo data already present ({} demo accounts); skipping generation", existing);
        } else {
            int size = Math.max(seed.size(), 5);
            long started = System.currentTimeMillis();
            tx.executeWithoutResult(status -> generate(size, seed.password()));
            log.info("Generated demo dataset of size {} in {} ms", size, System.currentTimeMillis() - started);
        }
        if (seed.exitAfterSeed()) {
            System.exit(SpringApplication.exit(context, () -> 0));
        }
    }

    /** Inserts the whole dataset. Package-visible so it can be exercised by tests. */
    void generate(int n, String password) {
        Random rnd = new Random(42);
        Instant now = clock.instant();
        LocalDate today = LocalDate.now(clock);
        String hash = passwordEncoder.encode(password == null ? "Password123" : password);

        // ------------------------------------------------------------ categories
        int categoryId = nextId("categories");
        List<Integer> categoryIds = new ArrayList<>();
        List<Object[]> categories = new ArrayList<>();
        Set<String> existingNames = new HashSet<>(jdbc.queryForList("select lower(name) from categories", String.class));
        outer:
        for (String space : SPACES) {
            for (String type : PROPERTY_TYPES) {
                if (categories.size() >= n) {
                    break outer;
                }
                String name = type + " " + space;
                if (!existingNames.add(name.toLowerCase())) {
                    continue;
                }
                boolean active = (categories.size() + 1) % 15 != 0;
                categories.add(new Object[] {categoryId, name, space + " renovation for " + type + " properties",
                    active, ts(now.minus(Duration.ofDays(200))), ts(now)});
                categoryIds.add(categoryId++);
            }
        }
        batch("insert into categories (id, name, description, active, created_at, updated_at) values (?,?,?,?,?,?)",
                categories);
        List<Integer> activeCategoryIds = new ArrayList<>();
        for (Object[] c : categories) {
            if ((Boolean) c[3]) {
                activeCategoryIds.add((Integer) c[0]);
            }
        }

        // ------------------------------------------------------------ users
        int userId = nextId("users");
        List<Object[]> users = new ArrayList<>();
        List<Integer> customers = new ArrayList<>();
        List<Integer> designers = new ArrayList<>();
        List<String> designerNames = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            String name = person(rnd);
            users.add(userRow(userId, String.format("customer%03d%s", i, DOMAIN), hash, name, phone(rnd), "CUSTOMER",
                    i % 25 == 0 ? "SUSPENDED" : "ACTIVE", daysAgo(now, rnd.nextInt(180))));
            customers.add(userId++);
        }
        for (int i = 1; i <= n; i++) {
            String name = person(rnd);
            users.add(userRow(userId, String.format("designer%03d%s", i, DOMAIN), hash, name, phone(rnd),
                    "INTERIOR_DESIGNER", i % 30 == 0 ? "SUSPENDED" : "ACTIVE", daysAgo(now, 30 + rnd.nextInt(150))));
            designers.add(userId++);
            designerNames.add(name);
        }
        int managers = Math.max(2, n / 20);
        for (int i = 1; i <= managers; i++) {
            users.add(userRow(userId++, String.format("manager%02d%s", i, DOMAIN), hash, person(rnd), phone(rnd),
                    "PLATFORM_MANAGEMENT", "ACTIVE", daysAgo(now, 150 + rnd.nextInt(30))));
        }
        batch("insert into users (id, email, password_hash, full_name, phone, role, status, deleted, created_at, updated_at) "
                + "values (?,?,?,?,?,?,?,false,?,?)", users);

        // ------------------------------------------------------------ designer profiles and companies (+ logos)
        int profileId = nextId("designer_profiles");
        int companyId = nextId("designer_companies");
        List<Object[]> profiles = new ArrayList<>();
        List<Object[]> companies = new ArrayList<>();
        List<Object[]> files = new ArrayList<>();
        for (int i = 0; i < designers.size(); i++) {
            int d = designers.get(i);
            String style = STYLES[rnd.nextInt(STYLES.length)];
            profiles.add(new Object[] {profileId++, d,
                "I help homeowners create calm, functional spaces with a " + style.toLowerCase() + " touch.",
                2 + rnd.nextInt(25), style + " residential interiors", ts(now), ts(now)});
            String logoKey = UUID.randomUUID() + ".png";
            files.add(new Object[] {logoKey, "image/png", logo(rnd)});
            String company = COMPANY_PREFIX[i % COMPANY_PREFIX.length] + " "
                    + COMPANY_WORDS[(i / COMPANY_PREFIX.length + i) % COMPANY_WORDS.length]
                    + (i >= COMPANY_PREFIX.length * COMPANY_WORDS.length ? " " + (i + 1) : "");
            String slug = company.toLowerCase().replaceAll("[^a-z0-9]+", "");
            companies.add(new Object[] {companyId++, d, company, String.format("2024%05dK", 10000 + i),
                "Award-winning " + style.toLowerCase() + " design studio serving homes across Singapore.",
                (10 + rnd.nextInt(200)) + " " + TOWNS[rnd.nextInt(TOWNS.length)] + " Avenue, Singapore",
                "hello@" + slug + ".sg", phone(rnd), "https://www." + slug + ".sg", logoKey, ts(now), ts(now)});
        }
        insertFiles(files, now);
        batch("insert into designer_profiles (id, designer_id, bio, years_of_experience, specialisation, created_at, updated_at) "
                + "values (?,?,?,?,?,?,?)", profiles);
        batch("insert into designer_companies (id, designer_id, company_name, registration_number, description, address, "
                + "contact_email, contact_phone, website_url, logo_key, created_at, updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?)",
                companies);

        // ------------------------------------------------------------ projects and images
        int projectId = nextId("projects");
        int imageId = nextId("project_images");
        List<Object[]> projects = new ArrayList<>();
        List<Object[]> images = new ArrayList<>();
        List<Object[]> imageFiles = new ArrayList<>();
        List<int[]> visibleProjects = new ArrayList<>(); // [projectId, designerId]
        for (int i = 0; i < n * 3; i++) {
            int designerIndex = i < designers.size() ? i : rnd.nextInt(designers.size());
            int designer = designers.get(designerIndex);
            int category = activeCategoryIds.get(rnd.nextInt(activeCategoryIds.size()));
            String style = STYLES[rnd.nextInt(STYLES.length)];
            String town = TOWNS[rnd.nextInt(TOWNS.length)];
            boolean completed = rnd.nextInt(10) < 7;
            LocalDate start = today.minusDays(30 + rnd.nextInt(400));
            LocalDate end = completed ? start.plusDays(30 + rnd.nextInt(120)) : null;
            BigDecimal budget = rnd.nextInt(10) == 0 ? null
                    : BigDecimal.valueOf(8000 + rnd.nextInt(140) * 1000L).setScale(2);
            boolean deleted = i % 40 == 39;
            Instant created = daysAgo(now, rnd.nextInt(120)).toInstant();
            projects.add(new Object[] {projectId, designer, category,
                style + " " + categoryName(categories, category).replace("Full Home", "Home") + " in " + town,
                "A " + style.toLowerCase() + " makeover in " + town + " with custom carpentry, warm lighting and "
                    + "practical storage. The brief was to keep the space open while adding character.",
                completed ? "COMPLETED" : "CURRENT", budget, Date.valueOf(start), end == null ? null : Date.valueOf(end),
                town + ", Singapore", deleted, deleted ? ts(now) : null, Timestamp.from(created), Timestamp.from(created)});
            int count = 1 + rnd.nextInt(2);
            for (int k = 0; k < count; k++) {
                String key = UUID.randomUUID() + ".jpg";
                imageFiles.add(new Object[] {key, "image/jpeg", roomImage(rnd)});
                images.add(new Object[] {imageId++, projectId, key, k + 1, k == 0, Timestamp.from(created)});
            }
            if (!deleted) {
                visibleProjects.add(new int[] {projectId, designer});
            }
            projectId++;
        }
        batch("insert into projects (id, designer_id, category_id, title, description, status, budget, start_date, end_date, "
                + "location, deleted, deleted_at, created_at, updated_at) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", projects);
        insertFiles(imageFiles, now);
        batch("insert into project_images (id, project_id, file_key, sort_order, cover, created_at) values (?,?,?,?,?,?)",
                images);

        // ------------------------------------------------------------ favourites (unique customer/project pairs)
        List<Object[]> favourites = new ArrayList<>();
        Set<Long> favPairs = new HashSet<>();
        while (favourites.size() < n * 5) {
            int c = customers.get(rnd.nextInt(customers.size()));
            int p = visibleProjects.get(rnd.nextInt(visibleProjects.size()))[0];
            if (favPairs.add(((long) c << 32) | p)) {
                favourites.add(new Object[] {c, p, ts(daysAgo(now, rnd.nextInt(90)).toInstant())});
            }
        }
        batch("insert into favourites (customer_id, project_id, saved_at) values (?,?,?)", favourites);

        // ------------------------------------------------------------ views (one per customer/project/day)
        int viewId = nextId("project_views");
        List<Object[]> views = new ArrayList<>();
        Set<String> viewKeys = new HashSet<>();
        while (views.size() < n * 20) {
            int c = customers.get(rnd.nextInt(customers.size()));
            int p = visibleProjects.get(rnd.nextInt(visibleProjects.size()))[0];
            int ago = rnd.nextInt(90);
            if (viewKeys.add(c + ":" + p + ":" + ago)) {
                LocalDate day = today.minusDays(ago);
                Instant at = day.atTime(9 + rnd.nextInt(12), rnd.nextInt(60)).atZone(clock.getZone()).toInstant();
                views.add(new Object[] {viewId++, p, c, Date.valueOf(day), Timestamp.from(at)});
            }
        }
        batch("insert into project_views (id, project_id, customer_id, view_date, viewed_at) values (?,?,?,?,?)", views);

        // ------------------------------------------------------------ enquiries
        int enquiryId = nextId("enquiries");
        List<Object[]> enquiries = new ArrayList<>();
        String[] statuses = {"NEW", "NEW", "READ", "REPLIED"};
        for (int i = 0; i < n * 3; i++) {
            int c = customers.get(rnd.nextInt(customers.size()));
            int p = visibleProjects.get(rnd.nextInt(visibleProjects.size()))[0];
            String status = statuses[rnd.nextInt(statuses.length)];
            Instant created = daysAgo(now, rnd.nextInt(60)).toInstant();
            Timestamp read = "NEW".equals(status) ? null : Timestamp.from(created.plus(Duration.ofHours(5)));
            Timestamp replied = "REPLIED".equals(status) ? Timestamp.from(created.plus(Duration.ofHours(20))) : null;
            enquiries.add(new Object[] {enquiryId++, p, c, ENQUIRY_LINES[rnd.nextInt(ENQUIRY_LINES.length)], status,
                read, replied, Timestamp.from(created), Timestamp.from(created)});
        }
        batch("insert into enquiries (id, project_id, customer_id, message, status, read_at, replied_at, created_at, updated_at) "
                + "values (?,?,?,?,?,?,?,?,?)", enquiries);

        // ------------------------------------------------------------ availability (today .. +60 days)
        int availabilityId = nextId("availability");
        List<Object[]> availability = new ArrayList<>();
        for (int d : designers) {
            Set<Integer> offsets = new HashSet<>();
            while (offsets.size() < 10) {
                offsets.add(rnd.nextInt(61));
            }
            for (int off : offsets) {
                availability.add(new Object[] {availabilityId++, d, Date.valueOf(today.plusDays(off)),
                    rnd.nextInt(4) == 0 ? "AVAILABLE" : "UNAVAILABLE", ts(now)});
            }
        }
        batch("insert into availability (id, designer_id, calendar_date, status, updated_at) values (?,?,?,?,?)",
                availability);

        // ------------------------------------------------------------ ratings (respecting the uniqueness rules)
        int ratingId = nextId("ratings");
        List<Object[]> ratings = new ArrayList<>();
        Set<String> ratingKeys = new HashSet<>();
        while (ratings.size() < n * 3) {
            int c = customers.get(rnd.nextInt(customers.size()));
            int[] vp = visibleProjects.get(rnd.nextInt(visibleProjects.size()));
            boolean general = rnd.nextBoolean();
            String key = general ? "g:" + c + ":" + vp[1] : "p:" + c + ":" + vp[0];
            if (!ratingKeys.add(key)) {
                continue;
            }
            Instant created = daysAgo(now, rnd.nextInt(90)).toInstant();
            ratings.add(new Object[] {ratingId++, c, vp[1], general ? null : vp[0], 2 + rnd.nextInt(4),
                RATING_COMMENTS[rnd.nextInt(RATING_COMMENTS.length)], Timestamp.from(created), Timestamp.from(created)});
        }
        batch("insert into ratings (id, customer_id, designer_id, project_id, score, comment, created_at, updated_at) "
                + "values (?,?,?,?,?,?,?,?)", ratings);

        // ------------------------------------------------------------ issue reports
        int reportId = nextId("issue_reports");
        Integer adminId = jdbc.query("select id from users where role = 'USER_ADMIN' order by id limit 1",
                rs -> rs.next() ? rs.getInt(1) : null);
        List<Object[]> reports = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            boolean byCustomer = rnd.nextBoolean();
            int reporter = byCustomer ? customers.get(rnd.nextInt(customers.size()))
                    : designers.get(rnd.nextInt(designers.size()));
            boolean platform = rnd.nextInt(3) == 0;
            int[] vp = visibleProjects.get(rnd.nextInt(visibleProjects.size()));
            boolean rectified = adminId != null && rnd.nextInt(10) < 4;
            Instant created = daysAgo(now, rnd.nextInt(90)).toInstant();
            reports.add(new Object[] {reportId++, reporter, platform ? "PLATFORM_ISSUE" : "IDP_OR_DESIGNER",
                platform ? null : vp[0], platform ? null : vp[1],
                platform ? "Search page loads slowly" : "Project photos look like stock images",
                platform ? "The search results sometimes take a long time to appear on my phone."
                         : "Some photos in this project appear on other websites. Please check if they are genuine.",
                rectified ? "RECTIFIED" : "OPEN", Timestamp.from(created),
                rectified ? Timestamp.from(created.plus(Duration.ofDays(2))) : null, rectified ? adminId : null});
        }
        batch("insert into issue_reports (id, reporter_id, type, target_project_id, target_designer_id, title, description, "
                + "status, created_at, rectified_at, rectified_by_id) values (?,?,?,?,?,?,?,?,?,?,?)", reports);

        // ------------------------------------------------------------ move identity sequences past inserted ids
        for (String table : List.of("categories", "users", "designer_profiles", "designer_companies", "projects",
                "project_images", "project_views", "enquiries", "availability", "ratings", "issue_reports")) {
            jdbc.execute("select setval(pg_get_serial_sequence('" + table + "', 'id'), "
                    + "(select coalesce(max(id), 1) from " + table + "))");
        }
        log.info("Inserted {} categories, {} users, {} projects, {} images, {} favourites, {} views, {} enquiries, "
                + "{} availability records, {} ratings, {} issue reports", categories.size(), users.size(),
                projects.size(), images.size(), favourites.size(), views.size(), enquiries.size(),
                availability.size(), ratings.size(), reports.size());
    }

    // ---------------------------------------------------------------- helpers

    private int nextId(String table) {
        Integer max = jdbc.queryForObject("select coalesce(max(id), 0) from " + table, Integer.class);
        return (max == null ? 0 : max) + 1;
    }

    private void batch(String sql, List<Object[]> rows) {
        for (int i = 0; i < rows.size(); i += 500) {
            jdbc.batchUpdate(sql, rows.subList(i, Math.min(rows.size(), i + 500)));
        }
    }

    private void insertFiles(List<Object[]> files, Instant now) {
        List<Object[]> rows = new ArrayList<>();
        for (Object[] f : files) {
            byte[] data = (byte[]) f[2];
            rows.add(new Object[] {f[0], f[1], data.length, data, ts(now)});
        }
        // Smaller batches: each row carries an image.
        for (int i = 0; i < rows.size(); i += 50) {
            jdbc.batchUpdate("insert into stored_files (file_key, content_type, size_bytes, data, created_at) "
                    + "values (?,?,?,?,?)", rows.subList(i, Math.min(rows.size(), i + 50)));
        }
    }

    private static Object[] userRow(int id, String email, String hash, String name, String phone, String role,
                                    String status, java.time.ZonedDateTime created) {
        return new Object[] {id, email, hash, name, phone, role, status, Timestamp.from(created.toInstant()),
            Timestamp.from(created.toInstant())};
    }

    private java.time.ZonedDateTime daysAgo(Instant now, int days) {
        return now.atZone(clock.getZone()).minusDays(days).minusMinutes(days * 37L % 600);
    }

    private static Timestamp ts(Instant i) {
        return Timestamp.from(i);
    }

    private static String person(Random rnd) {
        return FIRST[rnd.nextInt(FIRST.length)] + " " + LAST[rnd.nextInt(LAST.length)];
    }

    private static String phone(Random rnd) {
        return "+65 " + (rnd.nextBoolean() ? "8" : "9") + String.format("%03d %04d", rnd.nextInt(1000), rnd.nextInt(10000));
    }

    private static String categoryName(List<Object[]> categories, int id) {
        for (Object[] c : categories) {
            if ((Integer) c[0] == id) {
                return (String) c[1];
            }
        }
        return "Renovation";
    }

    /** A simple abstract "room" picture (wall, floor, window, sofa, lamp) in a random palette. No fonts needed. */
    static byte[] roomImage(Random rnd) {
        int w = 640;
        int h = 420;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float hue = rnd.nextFloat();
        Color wall = Color.getHSBColor(hue, 0.12f + rnd.nextFloat() * 0.15f, 0.88f);
        Color floor = Color.getHSBColor((hue + 0.08f) % 1f, 0.35f, 0.55f + rnd.nextFloat() * 0.2f);
        Color accent = Color.getHSBColor((hue + 0.5f) % 1f, 0.45f, 0.6f);
        g.setPaint(new GradientPaint(0, 0, wall.brighter(), 0, h * 0.65f, wall));
        g.fillRect(0, 0, w, (int) (h * 0.65));
        g.setPaint(new GradientPaint(0, h * 0.65f, floor, 0, h, floor.darker()));
        g.fillRect(0, (int) (h * 0.65), w, h);
        int wx = 60 + rnd.nextInt(300);
        g.setColor(new Color(220, 235, 245));
        g.fillRect(wx, 50, 180, 140);
        g.setColor(Color.WHITE);
        g.fillRect(wx + 87, 50, 6, 140);
        g.fillRect(wx, 117, 180, 6);
        int sx = 40 + rnd.nextInt(280);
        g.setColor(accent);
        g.fillRoundRect(sx, 230, 300, 70, 20, 20);
        g.setColor(accent.darker());
        g.fillRoundRect(sx, 200, 300, 45, 20, 20);
        g.fillRect(sx + 15, 300, 10, 25);
        g.fillRect(sx + 275, 300, 10, 25);
        g.setColor(new Color(60, 60, 60));
        g.fillRect(w - 110, 120, 6, 180);
        g.setColor(new Color(250, 225, 160));
        g.fillOval(w - 140, 90, 66, 50);
        g.setColor(floor.darker().darker());
        g.fillOval(sx + 60, 330, 200, 30);
        g.dispose();
        return encode(img, "jpg");
    }

    /** A simple geometric logo. */
    static byte[] logo(Random rnd) {
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = Color.getHSBColor(rnd.nextFloat(), 0.55f, 0.55f);
        g.setColor(c);
        g.fillOval(10, 10, 180, 180);
        g.setColor(Color.WHITE);
        switch (rnd.nextInt(3)) {
            case 0 -> g.fillRect(65, 65, 70, 70);
            case 1 -> g.fillPolygon(new int[] {100, 145, 55}, new int[] {50, 140, 140}, 3);
            default -> g.fillOval(60, 60, 80, 80);
        }
        g.dispose();
        return encode(img, "png");
    }

    private static byte[] encode(BufferedImage img, String format) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, format, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
