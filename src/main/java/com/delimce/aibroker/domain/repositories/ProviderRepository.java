package com.delimce.aibroker.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.delimce.aibroker.domain.entities.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

}
