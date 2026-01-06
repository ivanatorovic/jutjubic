package com.example.jutjubic.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VideoRepositoryCustomImpl implements VideoRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    public VideoRepositoryCustomImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int incrementViewCount(Long videoId) {

        return jdbcTemplate.update(
                "UPDATE videos SET view_count = view_count + 1 WHERE id = ?",
                videoId
        );
    }
}
