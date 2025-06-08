package com.matdev.admin_service.domain.repository;

import com.matdev.admin_service.domain.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, UUID> {
    List<Modulo> findAllByTenantId(String tenantId);
}
