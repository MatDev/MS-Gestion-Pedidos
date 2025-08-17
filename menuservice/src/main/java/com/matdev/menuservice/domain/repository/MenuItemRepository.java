package com.matdev.menuservice.domain.repository;

import com.matdev.menuservice.domain.model.Categoria;
import com.matdev.menuservice.domain.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends MongoRepository<MenuItem, String> {
    List<MenuItem> findByTenantIdAndDisponibleTrue(String tenantId);
    Optional<MenuItem> findByIdAndTenantId(String id, String tenantId);
    Page<MenuItem> findByTenantId(String tenantId, Pageable pageable);
    List<MenuItem> findByTenantIdAndCategoria(String tenantId, Categoria categoria);
    List<MenuItem> findByTenantIdAndNombreContainingIgnoreCase(String tenantId, String nombre);
    List<MenuItem> findByTenantIdAndPrecioBetween(String tenantId, java.math.BigDecimal precioMin, java.math.BigDecimal precioMax);
}