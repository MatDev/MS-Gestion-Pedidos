package com.matdev.admin_service.domain.repository;

import com.matdev.admin_service.domain.model.ConfiguracionVisual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracionVisualRepository extends JpaRepository<ConfiguracionVisual, UUID> {
    Optional<ConfiguracionVisual> findByTenantId(String tenantId);
}
