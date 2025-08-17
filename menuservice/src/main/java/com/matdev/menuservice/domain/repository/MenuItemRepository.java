package com.matdev.menuservice.domain.repository;

import com.matdev.menuservice.domain.model.Categoria;
import com.matdev.menuservice.domain.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends MongoRepository<MenuItem, String> {
    // Búsquedas básicas por tenant
    Optional<MenuItem> findByIdAndTenantId(String id, String tenantId);

    List<MenuItem> findByTenantIdAndDisponibleTrue(String tenantId);

    Page<MenuItem> findByTenantId(String tenantId, Pageable pageable);

    // Búsquedas por categoría
    List<MenuItem> findByTenantIdAndCategoria(String tenantId, Categoria categoria);

    List<MenuItem> findByTenantIdAndCategoriaAndDisponibleTrue(String tenantId, Categoria categoria);

    // Búsquedas por nombre
    List<MenuItem> findByTenantIdAndNombreContainingIgnoreCase(String tenantId, String nombre);

    // Búsquedas por precio
    List<MenuItem> findByTenantIdAndPrecioBetween(String tenantId, BigDecimal precioMin, BigDecimal precioMax);

    @Query("{'tenantId': ?0, 'precio': {$gte: ?1, $lte: ?2}, 'disponible': true}")
    List<MenuItem> findAvailableByTenantIdAndPriceRange(String tenantId, BigDecimal min, BigDecimal max);

    // Búsquedas por ingredientes/alergenos
    List<MenuItem> findByTenantIdAndIngredientesContaining(String tenantId, String ingrediente);

    List<MenuItem> findByTenantIdAndAlergenosContaining(String tenantId, String alergeno);

    // Estadísticas
    Long countByTenantIdAndCategoria(String tenantId, Categoria categoria);

    Long countByTenantIdAndDisponibleTrue(String tenantId);

    // Verificar existencia
    Boolean existsByTenantIdAndNombreIgnoreCase(String tenantId, String nombre);

    // Items ordenados por precio
    List<MenuItem> findByTenantIdAndDisponibleTrueOrderByPrecioAsc(String tenantId);

    List<MenuItem> findByTenantIdAndDisponibleTrueOrderByPrecioDesc(String tenantId);
}