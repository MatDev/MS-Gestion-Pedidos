package com.matdev.menuservice.controller;

import com.matdev.menuservice.domain.model.Categoria;
import com.matdev.menuservice.dto.MenuItemDto;
import com.matdev.menuservice.service.MenuService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MenuItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuItemController.class);
    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuItemDto> crear(@Valid @RequestBody MenuItemDto dto) {
        LOGGER.info("POST /api/items - Creating item: {}", dto.getNombre());
        MenuItemDto created = menuService.crearMenuItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDto> obtenerPorId(@PathVariable String id) {
        LOGGER.info("GET /api/items/{}", id);
        return ResponseEntity.ok(menuService.obtenerMenuItemPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<MenuItemDto>> listar(
            @PageableDefault(size = 20) Pageable pageable) {
        LOGGER.info("GET /api/items - Page: {}", pageable.getPageNumber());
        return ResponseEntity.ok(menuService.obtenerMenuItems(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemDto> actualizar(
            @PathVariable String id,
            @Valid @RequestBody MenuItemDto dto) {
        LOGGER.info("PUT /api/items/{}", id);
        return ResponseEntity.ok(menuService.actualizarMenuItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        LOGGER.info("DELETE /api/items/{}", id);
        menuService.eliminarMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<MenuItemDto>> listarDisponibles() {
        LOGGER.info("GET /api/items/disponibles");
        return ResponseEntity.ok(menuService.obtenerMenuItemsDisponibles());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<MenuItemDto>> porCategoria(@PathVariable Categoria categoria) {
        LOGGER.info("GET /api/items/categoria/{}", categoria);
        return ResponseEntity.ok(menuService.obtenerMenuItemsPorCategoria(categoria));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuItemDto>> buscar(@RequestParam String q) {
        LOGGER.info("GET /api/items/search?q={}", q);
        return ResponseEntity.ok(menuService.buscarMenuItemsPorNombre(q));
    }

    @GetMapping("/precio-entre")
    public ResponseEntity<List<MenuItemDto>> porRangoPrecio(
            @RequestParam @DecimalMin("0") BigDecimal min,
            @RequestParam @DecimalMin("0") BigDecimal max) {
        LOGGER.info("GET /api/items/precio-entre?min={}&max={}", min, max);
        return ResponseEntity.ok(menuService.obtenerMenuItemsPorRangoPrecio(min, max));
    }

    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<Map<String, Object>> cambiarDisponibilidad(
            @PathVariable String id,
            @RequestParam boolean disponible) {
        LOGGER.info("PATCH /api/items/{}/disponibilidad?disponible={}", id, disponible);

        MenuItemDto item = menuService.obtenerMenuItemPorId(id);
        item.setDisponible(disponible);
        menuService.actualizarMenuItem(id, item);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("disponible", disponible);
        response.put("mensaje", disponible ? "Item habilitado" : "Item deshabilitado");

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/precio")
    public ResponseEntity<Map<String, Object>> actualizarPrecio(
            @PathVariable String id,
            @RequestParam @DecimalMin("0.01") BigDecimal nuevoPrecio) {
        LOGGER.info("PATCH /api/items/{}/precio?nuevoPrecio={}", id, nuevoPrecio);

        MenuItemDto item = menuService.obtenerMenuItemPorId(id);
        BigDecimal precioAnterior = item.getPrecio();
        item.setPrecio(nuevoPrecio);
        menuService.actualizarMenuItem(id, item);

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("precioAnterior", precioAnterior);
        response.put("precioNuevo", nuevoPrecio);
        response.put("mensaje", "Precio actualizado exitosamente");

        return ResponseEntity.ok(response);
    }
}
