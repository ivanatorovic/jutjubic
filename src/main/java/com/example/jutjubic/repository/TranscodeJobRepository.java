package com.example.jutjubic.repository;

import com.example.jutjubic.model.TranscodeJob;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface TranscodeJobRepository extends JpaRepository<TranscodeJob, UUID> {

    @Modifying
    @Transactional
    @Query("""
  update TranscodeJob j
     set j.status = :newStatus,
         j.consumerId = :consumerId,
         j.startedAt = CURRENT_TIMESTAMP,
         j.errorMessage = null,
         j.finishedAt = null
   where j.jobId = :jobId
     and j.status = :expectedStatus
""")
    int claim(@Param("jobId") UUID jobId,
              @Param("consumerId") String consumerId,
              @Param("expectedStatus") TranscodeJob.Status expectedStatus,
              @Param("newStatus") TranscodeJob.Status newStatus);

}
