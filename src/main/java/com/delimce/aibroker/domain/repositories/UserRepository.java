package com.delimce.aibroker.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delimce.aibroker.domain.entities.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   
    public User findByEmail(String email);
}
