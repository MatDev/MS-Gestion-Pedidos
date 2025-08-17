package com.matdev.menuservice.domain.repository;

import com.matdev.menuservice.domain.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends MongoRepository<Menu, String> {
    // Búsquedas por tenant
    Optional<Menu> findByIdAndTenantId(String id, String tenantId);

    List<Menu> findByTenantIdAndActivoTrue(String tenantId);

    Page<Menu> findByTenantId(String tenantId, Pageable pageable);

    List<Menu> findByTenantIdAndNombreContainingIgnoreCase(String tenantId, String nombre);

    // Búsqueda de menus que contengan items de cierta categoría
    @Query("{'tenantId': ?0, 'items': {$exists: true, $ne: []}}")
    List<Menu> findByTenantIdAndItemsExists(String tenantId);

    // Contar menus activos por tenant
    Long countByTenantIdAndActivoTrue(String tenantId);

    // Verificar si existe un menu con el mismo nombre para el tenant
    Boolean existsByTenantIdAndNombreIgnoreCase(String tenantId, String nombre);
}

