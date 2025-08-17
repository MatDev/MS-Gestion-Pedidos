package com.matdev.menuservice.domain.repository;

import com.matdev.menuservice.domain.model.Menu;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends MongoRepository<Menu, String> {
    List<Menu> findByCategoria(String categoria);
    List<Menu> findByDisponible(boolean disponible);
    List<Menu> findByCategoriaAndDisponible(String categoria, boolean disponible);
}

