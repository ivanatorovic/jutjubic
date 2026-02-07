package com.example.jutjubic.repository;

import com.example.jutjubic.model.VideoDailyViews;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VideoDailyViewsRepository extends JpaRepository<VideoDailyViews, Long> {


    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO video_daily_views(video_id, view_date, views_count)
        VALUES (:videoId, CURRENT_DATE, 1)
        ON CONFLICT (video_id, view_date)
        DO UPDATE SET views_count = video_daily_views.views_count + 1
        """, nativeQuery = true)
    void incrementToday(@Param("videoId") long videoId);


    interface VideoScoreRow {
        Long getVideoId();
        Long getScore();
    }


    @Query(value = """
    SELECT
      video_id AS videoId,
      CAST(SUM(views_count * (7 - (CURRENT_DATE - view_date))) AS bigint) AS score
    FROM video_daily_views
    WHERE view_date >= CURRENT_DATE - 6
      AND view_date <=  CURRENT_DATE
    GROUP BY video_id
    ORDER BY score DESC
    LIMIT 3
    """, nativeQuery = true)
    List<VideoScoreRow> findTop3WeightedLast7Days();

}
