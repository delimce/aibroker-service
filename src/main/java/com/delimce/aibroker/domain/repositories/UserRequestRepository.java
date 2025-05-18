package com.delimce.aibroker.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delimce.aibroker.domain.entities.Model;
import com.delimce.aibroker.domain.entities.User;
import com.delimce.aibroker.domain.entities.UserRequest;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {

    /**
     * Find all requests made by a specific user
     * 
     * @param user The user who made the requests
     * @return List of UserRequest entities
     */
    List<UserRequest> findByUser(User user);

    /**
     * Find all requests for a specific model
     * 
     * @param model The model used for the requests
     * @return List of UserRequest entities
     */
    List<UserRequest> findByModel(Model model);

    /**
     * Find all requests by a user for a specific model
     * 
     * @param user  The user who made the requests
     * @param model The model used for the requests
     * @return List of UserRequest entities
     */
    List<UserRequest> findByUserAndModel(User user, Model model);

    /**
     * Find all requests made within a date range
     * 
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return List of UserRequest entities
     */
    List<UserRequest> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all requests by a user within a date range
     * 
     * @param user      The user who made the requests
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return List of UserRequest entities
     */
    List<UserRequest> findByUserAndCreatedAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}
