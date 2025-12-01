package com.final_team4.finalbe.dashboard.service;

import com.final_team4.finalbe.dashboard.dto.DashboardStatusGetResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class DashboardServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("전체 클릭 수를 반환한다")
    @Test
    void getStatus_returnsClickCount() {
        Long categoryId = findAnyCategoryId();
        Long productId = insertProduct(categoryId);
        insertClick(productId, "127.0.0.1");
        insertClick(productId, "127.0.0.2");

        DashboardStatusGetResponseDto result = dashboardService.getStatus();

        assertThat(result.getAllClicks()).isEqualTo(2);
        assertThat(result.getAllViews()).isZero();
        assertThat(result.getVisitors()).isZero();
        assertThat(result.getAverageDwellTime()).isZero();
    }

    private Long findAnyCategoryId() {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM product_category LIMIT 1",
                Long.class
        );
    }

    //product 테이블에 대한 내용이 사전에 필요해서 insert
    private Long insertProduct(Long categoryId) {
        KeyHolder keyHolder = new GeneratedKeyHolder(); // Insert 실행후 DB가 자동생성해주는 PK 값을 받아오기 위한객체
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO product (category_id, name, link, thumbnail, price) VALUES (?, ?, ?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setLong(1, categoryId);
            ps.setString(2, "test-product");
            ps.setString(3, "http://example.com");
            ps.setString(4, null);
            ps.setLong(5, 1000L);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private void insertClick(Long productId, String ip) {
        jdbcTemplate.update(
                "INSERT INTO clicks (product_id, ip) VALUES (?, ?)",
                productId, ip
        );
    }
}