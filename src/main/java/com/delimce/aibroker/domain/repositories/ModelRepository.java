package com.delimce.aibroker.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delimce.aibroker.domain.entities.Model;

public interface ModelRepository extends JpaRepository<Model, Long> {

}
