package com.example.jutjubic.repository;

import com.example.jutjubic.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryCustom {
    List<Video> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Video> findTop200ByOrderByCreatedAtDesc();

    @Query(value = """
    SELECT *
    FROM videos v
    WHERE v.geohash IS NOT NULL
      AND (
           v.geohash LIKE CONCAT(:p0, '%')
        OR v.geohash LIKE CONCAT(:p1, '%')
        OR v.geohash LIKE CONCAT(:p2, '%')
        OR v.geohash LIKE CONCAT(:p3, '%')
        OR v.geohash LIKE CONCAT(:p4, '%')
        OR v.geohash LIKE CONCAT(:p5, '%')
        OR v.geohash LIKE CONCAT(:p6, '%')
        OR v.geohash LIKE CONCAT(:p7, '%')
        OR v.geohash LIKE CONCAT(:p8, '%')
      )
    ORDER BY v.created_at DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<Video> findByGeohashPrefixes(
            String p0, String p1, String p2, String p3, String p4,
            String p5, String p6, String p7, String p8,
            int limit
    );

    @Query("""
select v from Video v
where (v.scheduled = false)
   or (v.scheduled = true and v.scheduledAt <= :now)
order by v.id desc
""")
    List<Video> findPublic(LocalDateTime now);



    @Query("""
        select v from Video v
        where v.thumbnailCompressed = false
          and v.thumbnailPath is not null
          and v.createdAt <= :cutoff
    """)
    List<Video> findThumbnailsToCompress(LocalDateTime cutoff);

}
