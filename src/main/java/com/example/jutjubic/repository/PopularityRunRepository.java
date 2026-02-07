package com.example.jutjubic.repository;

import com.example.jutjubic.model.PopularityRun;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PopularityRunRepository extends CrudRepository<PopularityRun, Long> {

    @Query("select r from PopularityRun r order by r.runAt desc")
    List<PopularityRun> findLatest(Pageable pageable);

    default Optional<PopularityRun> findLatestOne(Pageable pageable) {
        return findLatest(pageable).stream().findFirst();
    }
}
