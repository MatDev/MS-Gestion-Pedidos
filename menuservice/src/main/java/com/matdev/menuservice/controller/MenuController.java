package com.matdev.menuservice.controller;

import com.matdev.menuservice.domain.model.Categoria;
import com.matdev.menuservice.dto.CategoriaDto;
import com.matdev.menuservice.dto.MenuDto;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);
    private final MenuService menuService;

    // ================== MENU ENDPOINTS ==================

    /**
     * Crear un nuevo menú
     */
    @PostMapping
    public ResponseEntity<MenuDto> crearMenu(@Valid @RequestBody MenuDto menuDto) {
        LOGGER.info("POST /api/menu - Creating new menu: {}", menuDto.getNombre());
        try {
            MenuDto created = menuService.crearMenu(menuDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            LOGGER.error("Error creating menu: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtener menú por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuDto> obtenerMenuPorId(@PathVariable String id) {
        LOGGER.info("GET /api/menu/{} - Getting menu by id", id);
        MenuDto menu = menuService.obtenerMenuPorId(id);
        return ResponseEntity.ok(menu);
    }

    /**
     * Obtener todos los menús activos
     */
    @GetMapping("/activos")
    public ResponseEntity<List<MenuDto>> obtenerMenusActivos() {
        LOGGER.info("GET /api/menu/activos - Getting active menus");
        List<MenuDto> menus = menuService.obtenerMenusActivos();
        return ResponseEntity.ok(menus);
    }

    /**
     * Obtener menús paginados
     */
    @GetMapping
    public ResponseEntity<Page<MenuDto>> obtenerMenus(
            @PageableDefault(page = 0, size = 10, sort = "nombre") Pageable pageable) {
        LOGGER.info("GET /api/menu - Getting paginated menus, page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<MenuDto> menus = menuService.obtenerMenus(pageable);
        return ResponseEntity.ok(menus);
    }

    /**
     * Actualizar un menú existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuDto> actualizarMenu(
            @PathVariable String id,
            @Valid @RequestBody MenuDto menuDto) {
        LOGGER.info("PUT /api/menu/{} - Updating menu", id);
        MenuDto updated = menuService.actualizarMenu(id, menuDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Eliminar un menú
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMenu(@PathVariable String id) {
        LOGGER.info("DELETE /api/menu/{} - Deleting menu", id);
        menuService.eliminarMenu(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Buscar menús por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<MenuDto>> buscarMenusPorNombre(
            @RequestParam(required = true) String nombre) {
        LOGGER.info("GET /api/menu/buscar - Searching menus by name: {}", nombre);
        if (nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<MenuDto> menus = menuService.buscarMenusPorNombre(nombre);
        return ResponseEntity.ok(menus);
    }

    /**
     * Activar/Desactivar un menú
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<MenuDto> toggleMenuStatus(@PathVariable String id) {
        LOGGER.info("PATCH /api/menu/{}/toggle-status - Toggling menu status", id);
        MenuDto menu = menuService.obtenerMenuPorId(id);
        menu.setActivo(!menu.getActivo());
        MenuDto updated = menuService.actualizarMenu(id, menu);
        return ResponseEntity.ok(updated);
    }
    @PostMapping("/item")
    public ResponseEntity<MenuItemDto> crearMenuItem(@Valid @RequestBody MenuItemDto menuItemDto) {
        LOGGER.info("POST /api/menu/item - Creating new menu item: {}", menuItemDto.getNombre());
        try {
            MenuItemDto created = menuService.crearMenuItem(menuItemDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            LOGGER.error("Error creating menu item: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtener item de menú por ID
     */
    @GetMapping("/item/{id}")
    public ResponseEntity<MenuItemDto> obtenerMenuItemPorId(@PathVariable String id) {
        LOGGER.info("GET /api/menu/item/{} - Getting menu item by id", id);
        MenuItemDto item = menuService.obtenerMenuItemPorId(id);
        return ResponseEntity.ok(item);
    }

    /**
     * Obtener todos los items disponibles
     */
    @GetMapping("/item/disponibles")
    public ResponseEntity<List<MenuItemDto>> obtenerMenuItemsDisponibles() {
        LOGGER.info("GET /api/menu/item/disponibles - Getting available menu items");
        List<MenuItemDto> items = menuService.obtenerMenuItemsDisponibles();
        return ResponseEntity.ok(items);
    }

    /**
     * Obtener items paginados
     */
    @GetMapping("/items")
    public ResponseEntity<Page<MenuItemDto>> obtenerMenuItems(
            @PageableDefault(page = 0, size = 20, sort = "categoria,nombre") Pageable pageable) {
        LOGGER.info("GET /api/menu/items - Getting paginated menu items, page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<MenuItemDto> items = menuService.obtenerMenuItems(pageable);
        return ResponseEntity.ok(items);
    }

    /**
     * Actualizar un item de menú
     */
    @PutMapping("/item/{id}")
    public ResponseEntity<MenuItemDto> actualizarMenuItem(
            @PathVariable String id,
            @Valid @RequestBody MenuItemDto menuItemDto) {
        LOGGER.info("PUT /api/menu/item/{} - Updating menu item", id);
        MenuItemDto updated = menuService.actualizarMenuItem(id, menuItemDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Eliminar un item de menú
     */
    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> eliminarMenuItem(@PathVariable String id) {
        LOGGER.info("DELETE /api/menu/item/{} - Deleting menu item", id);
        menuService.eliminarMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener items por categoría
     */
    @GetMapping("/item/categoria/{categoria}")
    public ResponseEntity<List<MenuItemDto>> obtenerMenuItemsPorCategoria(
            @PathVariable Categoria categoria) {
        LOGGER.info("GET /api/menu/item/categoria/{} - Getting items by category", categoria);
        List<MenuItemDto> items = menuService.obtenerMenuItemsPorCategoria(categoria);
        return ResponseEntity.ok(items);
    }

    /**
     * Buscar items por nombre
     */
    @GetMapping("/item/buscar")
    public ResponseEntity<List<MenuItemDto>> buscarMenuItemsPorNombre(
            @RequestParam(required = true) String nombre) {
        LOGGER.info("GET /api/menu/item/buscar - Searching items by name: {}", nombre);
        if (nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<MenuItemDto> items = menuService.buscarMenuItemsPorNombre(nombre);
        return ResponseEntity.ok(items);
    }

    /**
     * Obtener items por rango de precio
     */
    @GetMapping("/item/precio")
    public ResponseEntity<List<MenuItemDto>> obtenerMenuItemsPorRangoPrecio(
            @RequestParam(required = true) BigDecimal min,
            @RequestParam(required = true) BigDecimal max) {
        LOGGER.info("GET /api/menu/item/precio - Getting items by price range: {}-{}", min, max);

        if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
            return ResponseEntity.badRequest().build();
        }

        List<MenuItemDto> items = menuService.obtenerMenuItemsPorRangoPrecio(min, max);
        return ResponseEntity.ok(items);
    }

    /**
     * Activar/Desactivar disponibilidad de un item
     */
    @PatchMapping("/item/{id}/toggle-disponible")
    public ResponseEntity<MenuItemDto> toggleItemDisponibilidad(@PathVariable String id) {
        LOGGER.info("PATCH /api/menu/item/{}/toggle-disponible - Toggling item availability", id);
        MenuItemDto item = menuService.obtenerMenuItemPorId(id);
        item.setDisponible(!item.getDisponible());
        MenuItemDto updated = menuService.actualizarMenuItem(id, item);
        return ResponseEntity.ok(updated);
    }

    /**
     * Actualizar precio de un item
     */
    @PatchMapping("/item/{id}/precio")
    public ResponseEntity<MenuItemDto> actualizarPrecioItem(
            @PathVariable String id,
            @RequestParam @DecimalMin(value = "0.01") BigDecimal precio) {
        LOGGER.info("PATCH /api/menu/item/{}/precio - Updating item price to: {}", id, precio);
        MenuItemDto item = menuService.obtenerMenuItemPorId(id);
        item.setPrecio(precio);
        MenuItemDto updated = menuService.actualizarMenuItem(id, item);
        return ResponseEntity.ok(updated);
    }

    // ================== CATEGORIAS Y ESTADÍSTICAS ==================

    /**
     * Obtener todas las categorías disponibles
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        LOGGER.info("GET /api/menu/categorias - Getting all categories");
        List<String> categorias = List.of(
                "ENTRADA", "PLATO_PRINCIPAL", "POSTRE", "BEBIDA",
                "SNACK", "ENSALADA", "SOPA", "PIZZA",
                "HAMBURGUESA", "PASTA", "VEGETARIANO", "VEGANO"
        );
        return ResponseEntity.ok(categorias);
    }

    /**
     * Obtener estadísticas por categoría
     */
    @GetMapping("/estadisticas/categorias")
    public ResponseEntity<List<CategoriaDto>> obtenerEstadisticasCategorias() {
        LOGGER.info("GET /api/menu/estadisticas/categorias - Getting category statistics");
        List<CategoriaDto> stats = menuService.obtenerEstadisticasCategorias();
        return ResponseEntity.ok(stats);
    }

    // ================== OPERACIONES ESPECIALES ==================

    /**
     * Agregar item a un menú
     */
    @PostMapping("/{menuId}/item/{itemId}")
    public ResponseEntity<MenuDto> agregarItemAMenu(
            @PathVariable String menuId,
            @PathVariable String itemId) {
        LOGGER.info("POST /api/menu/{}/item/{} - Adding item to menu", menuId, itemId);

        MenuDto menu = menuService.obtenerMenuPorId(menuId);
        MenuItemDto item = menuService.obtenerMenuItemPorId(itemId);

        // Verificar si el item ya está en el menú
        if (menu.getItems() != null &&
                menu.getItems().stream().anyMatch(i -> i.getId().equals(itemId))) {
            return ResponseEntity.badRequest().build();
        }

        if (menu.getItems() == null) {
            menu.setItems(new ArrayList<>());
        }
        menu.getItems().add(item);

        MenuDto updated = menuService.actualizarMenu(menuId, menu);
        return ResponseEntity.ok(updated);
    }

    /**
     * Remover item de un menú
     */
    @DeleteMapping("/{menuId}/item/{itemId}")
    public ResponseEntity<MenuDto> removerItemDeMenu(
            @PathVariable String menuId,
            @PathVariable String itemId) {
        LOGGER.info("DELETE /api/menu/{}/item/{} - Removing item from menu", menuId, itemId);

        MenuDto menu = menuService.obtenerMenuPorId(menuId);

        if (menu.getItems() != null) {
            menu.getItems().removeIf(item -> item.getId().equals(itemId));
            MenuDto updated = menuService.actualizarMenu(menuId, menu);
            return ResponseEntity.ok(updated);
        }

        return ResponseEntity.ok(menu);
    }

    /**
     * Duplicar un menú
     */
    @PostMapping("/{id}/duplicar")
    public ResponseEntity<MenuDto> duplicarMenu(
            @PathVariable String id,
            @RequestParam String nuevoNombre) {
        LOGGER.info("POST /api/menu/{}/duplicar - Duplicating menu with new name: {}", id, nuevoNombre);

        MenuDto original = menuService.obtenerMenuPorId(id);
        MenuDto copia = new MenuDto();
        copia.setNombre(nuevoNombre);
        copia.setDescripcion(original.getDescripcion());
        copia.setActivo(false); // Nuevo menú empieza inactivo
        copia.setImagenUrl(original.getImagenUrl());
        copia.setItems(original.getItems());

        MenuDto created = menuService.crearMenu(copia);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Buscar items por ingrediente
     */
    @GetMapping("/item/ingrediente")
    public ResponseEntity<List<MenuItemDto>> buscarPorIngrediente(
            @RequestParam String ingrediente) {
        LOGGER.info("GET /api/menu/item/ingrediente - Searching items with ingredient: {}", ingrediente);
        // Este método necesitaría ser agregado al servicio
        return ResponseEntity.ok(new ArrayList<>());
    }

    /**
     * Buscar items por alérgeno
     */
    @GetMapping("/item/alergeno")
    public ResponseEntity<List<MenuItemDto>> buscarPorAlergeno(
            @RequestParam String alergeno) {
        LOGGER.info("GET /api/menu/item/alergeno - Searching items with allergen: {}", alergeno);
        // Este método necesitaría ser agregado al servicio
        return ResponseEntity.ok(new ArrayList<>());
    }

    /**
     * Obtener items vegetarianos
     */
    @GetMapping("/item/vegetarianos")
    public ResponseEntity<List<MenuItemDto>> obtenerItemsVegetarianos() {
        LOGGER.info("GET /api/menu/item/vegetarianos - Getting vegetarian items");
        List<MenuItemDto> items = menuService.obtenerMenuItemsPorCategoria(Categoria.VEGETARIANO);
        return ResponseEntity.ok(items);
    }

    /**
     * Obtener items veganos
     */
    @GetMapping("/item/veganos")
    public ResponseEntity<List<MenuItemDto>> obtenerItemsVeganos() {
        LOGGER.info("GET /api/menu/item/veganos - Getting vegan items");
        List<MenuItemDto> items = menuService.obtenerMenuItemsPorCategoria(Categoria.VEGANO);
        return ResponseEntity.ok(items);
    }

    // ================== HEALTH CHECK ==================

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        LOGGER.info("GET /api/menu/health - Health check");
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "menu-service");
        status.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(status);
    }

}
