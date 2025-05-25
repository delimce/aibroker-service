package com.delimce.aibroker.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delimce.aibroker.domain.entities.RequestMetric;
import com.delimce.aibroker.domain.entities.UserRequest;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestMetricRepository extends JpaRepository<RequestMetric, Long> {

    /**
     * Find metrics by user request
     * 
     * @param userRequest The user request for which to find metrics
     * @return The metrics for the user request
     */
    RequestMetric findByUserRequest(UserRequest userRequest);

    /**
     * Find metrics within a date range
     * 
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return List of RequestMetric entities
     */
    List<RequestMetric> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
