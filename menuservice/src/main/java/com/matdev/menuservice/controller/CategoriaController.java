package com.matdev.menuservice.controller;

import com.matdev.menuservice.domain.model.Categoria;
import com.matdev.menuservice.dto.CategoriaDto;
import com.matdev.menuservice.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoriaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriaController.class);
    private final MenuService menuService;

    /**
     * Listar todas las categorías disponibles
     */
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> listarCategorias() {
        LOGGER.info("GET /api/categorias");

        List<Map<String, String>> categorias = Arrays.stream(Categoria.values())
                .map(cat -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("codigo", cat.name());
                    map.put("nombre", formatearNombre(cat));
                    map.put("descripcion", obtenerDescripcion(cat));
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(categorias);
    }

    /**
     * Obtener estadísticas de categorías
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<List<CategoriaDto>> obtenerEstadisticas() {
        LOGGER.info("GET /api/categorias/estadisticas");
        return ResponseEntity.ok(menuService.obtenerEstadisticasCategorias());
    }

    /**
     * Obtener detalle de una categoría específica
     */
    @GetMapping("/{categoria}")
    public ResponseEntity<Map<String, Object>> obtenerDetalleCategoria(
            @PathVariable Categoria categoria) {
        LOGGER.info("GET /api/categorias/{}", categoria);

        Map<String, Object> detalle = new HashMap<>();
        detalle.put("codigo", categoria.name());
        detalle.put("nombre", formatearNombre(categoria));
        detalle.put("descripcion", obtenerDescripcion(categoria));
        detalle.put("items", menuService.obtenerMenuItemsPorCategoria(categoria));

        return ResponseEntity.ok(detalle);
    }

    private String formatearNombre(Categoria categoria) {
        String nombre = categoria.name().replace("_", " ");
        return nombre.substring(0, 1).toUpperCase() +
                nombre.substring(1).toLowerCase();
    }

    private String obtenerDescripcion(Categoria categoria) {
        switch (categoria) {
            case ENTRADA:
                return "Platos de entrada y aperitivos para comenzar";
            case PLATO_PRINCIPAL:
                return "Platos principales y especialidades de la casa";
            case POSTRE:
                return "Postres, dulces y delicias para terminar";
            case BEBIDA:
                return "Bebidas frías, calientes y refrescantes";
            case SNACK:
                return "Snacks, picoteos y opciones rápidas";
            case ENSALADA:
                return "Ensaladas frescas y saludables";
            case SOPA:
                return "Sopas, cremas y caldos reconfortantes";
            case PIZZA:
                return "Pizzas artesanales con diversos sabores";
            case HAMBURGUESA:
                return "Hamburguesas gourmet y clásicas";
            case PASTA:
                return "Pastas italianas y platos mediterráneos";
            case VEGETARIANO:
                return "Opciones vegetarianas nutritivas";
            case VEGANO:
                return "Platos 100% veganos sin ingredientes animales";
            default:
                return "Categoría de menú";
        }
    }
}
