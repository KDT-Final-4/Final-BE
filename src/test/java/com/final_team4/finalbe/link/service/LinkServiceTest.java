package com.final_team4.finalbe.link.service;

import com.final_team4.finalbe.link.dto.LinkResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@Transactional
class LinkServiceTest {

    @Autowired
    LinkService linkService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @DisplayName("jobId로 링크를 조회하면 클릭을 기록하고 링크를 반환한다")
    @Test
    void resolveLink_recordsClick() {
        Fixture fixture = prepareLinkedContent("job-123", "http://example.com/content/1");

        LinkResponseDto response = linkService.resolveLink(fixture.jobId(), "198.51.100.1");

        assertThat(response.getLink()).isEqualTo(fixture.link());
        Long clicks = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM clicks WHERE product_id = ? AND ip = ?",
                Long.class,
                fixture.productId(), "198.51.100.1");
        assertThat(clicks).isEqualTo(1L);
    }

    @DisplayName("같은 ip로 두 번 조회해도 클릭은 한 번만 기록된다")
    @Test
    void resolveLink_duplicateIpOnlyOnce() {
        Fixture fixture = prepareLinkedContent("job-dup", "http://example.com/content/dup");

        linkService.resolveLink(fixture.jobId(), "192.0.2.1");
        linkService.resolveLink(fixture.jobId(), "192.0.2.1");

        Long clicks = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM clicks WHERE product_id = ? AND ip = ?",
                Long.class,
                fixture.productId(), "192.0.2.1");
        assertThat(clicks).isEqualTo(1L);
    }

    @DisplayName("존재하지 않는 jobId면 예외를 던진다")
    @Test
    void resolveLink_notFound() {
        try {
            linkService.resolveLink("unknown-job", "127.0.0.1");
            fail("예외가 발생해야 합니다.");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("jobId");
        }
    }

    private Fixture prepareLinkedContent(String jobId, String link) {
        Long userId = insertUser("link-user");
        Long categoryId = findAnyCategoryId();
        String keyword = "trend-" + jobId;
        Long uploadChannelId = insertUploadChannel(userId, "channel-" + jobId);
        Long contentId = insertContent(userId, keyword, uploadChannelId, jobId, link);
        Long productId = insertProduct(categoryId);
        linkProductToContent(productId, contentId);
        return new Fixture(productId, link, jobId);
    }

    private Long insertUser(String name) {
        Long roleId = ensureMarketerRoleId();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String email = name + "+" + System.nanoTime() + "@example.com";

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users (role_id, name, email, password, is_delete) VALUES (?, ?, ?, ?, 0)",
                        new String[]{"id"}
                );
                ps.setLong(1, roleId);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, "password");
                return ps;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Long insertUploadChannel(Long userId, String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO upload_channel (user_id, name, api_key, status) VALUES (?, ?, ?, 1)",
                        new String[]{"id"}
                );
                ps.setLong(1, userId);
                ps.setString(2, name);
                ps.setString(3, "api-key");
                return ps;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Long insertContent(Long userId, String keyword, Long uploadChannelId, String jobId, String link) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO content (user_id, title, body, status, generation_type, created_at, updated_at, job_id, upload_channel_id, link, keyword) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new String[]{"id"}
                );
                ps.setLong(1, userId);
                ps.setString(2, "title-" + jobId);
                ps.setString(3, "body");
                ps.setString(4, "PENDING");
                ps.setString(5, "AUTO");
                ps.setTimestamp(6, timestamp);
                ps.setTimestamp(7, timestamp);
                ps.setString(8, jobId);
                ps.setLong(9, uploadChannelId);
                ps.setString(10, link);
                ps.setString(11, keyword);
                return ps;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Long insertProduct(Long categoryId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO product (category_id, name, link, thumbnail, price) VALUES (?, ?, ?, ?, ?)",
                        new String[]{"id"}
                );
                ps.setLong(1, categoryId);
                ps.setString(2, "product-" + System.nanoTime());
                ps.setString(3, "http://example.com/product");
                ps.setString(4, null);
                ps.setLong(5, 1000L);
                return ps;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void linkProductToContent(Long productId, Long contentId) {
        jdbcTemplate.update(
                "INSERT INTO product_content (product_id, content_id) VALUES (?, ?)",
                productId, contentId
        );
    }

    private Long findAnyCategoryId() {
        List<Long> ids = jdbcTemplate.queryForList(
                "SELECT id FROM product_category FETCH FIRST 1 ROWS ONLY",
                Long.class);
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO product_category (name, description) VALUES (?, ?)",
                        new String[] {"id"});
                ps.setString(1, "test-category");
                ps.setString(2, "for link test");
                return ps;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Long ensureMarketerRoleId() {
        List<Long> roleIds = jdbcTemplate.queryForList(
                "SELECT id FROM role WHERE name = 'MARKETER'",
                Long.class);
        if (!roleIds.isEmpty()) {
            return roleIds.get(0);
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO role (name, description) VALUES (?, ?)",
                        new String[] {"id"});
                ps.setString(1, "MARKETER");
                ps.setString(2, "for link test");
                return ps;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private record Fixture(Long productId, String link, String jobId) {}
}
