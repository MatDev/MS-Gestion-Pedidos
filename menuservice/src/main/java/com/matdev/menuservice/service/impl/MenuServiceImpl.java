package com.matdev.menuservice.service.impl;

import com.matdev.menuservice.Mapper.MenuMapper;
import com.matdev.menuservice.context.TenantContext;
import com.matdev.menuservice.domain.model.Categoria;
import com.matdev.menuservice.domain.model.Menu;
import com.matdev.menuservice.domain.model.MenuItem;
import com.matdev.menuservice.domain.repository.MenuItemRepository;
import com.matdev.menuservice.domain.repository.MenuRepository;
import com.matdev.menuservice.dto.CategoriaDto;
import com.matdev.menuservice.dto.MenuDto;
import com.matdev.menuservice.dto.MenuItemDto;
import com.matdev.menuservice.exception.BadRequestException;
import com.matdev.menuservice.exception.NotFoundException;
import com.matdev.menuservice.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuServiceImpl.class);
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuMapper mapper;

    // ==================== MENU OPERATIONS ====================

    @Override
    @Transactional
    public MenuDto crearMenu(MenuDto menuDto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Creating menu for tenant: {}", tenantId);

        // Validar que no exista un menu con el mismo nombre
        if (menuRepository.existsByTenantIdAndNombreIgnoreCase(tenantId, menuDto.getNombre())) {
            throw new BadRequestException("Ya existe un menú con el nombre: " + menuDto.getNombre());
        }

        Menu menu = mapper.toEntity(menuDto);
        menu.setTenantId(tenantId);
        menu.setActivo(true);

        Menu savedMenu = menuRepository.save(menu);
        LOGGER.info("Menu created successfully with id: {}", savedMenu.getId());

        return mapper.toDto(savedMenu);
    }

    @Override
    @Cacheable(value = "menus", key = "#id + '_' + #root.method.name")
    public MenuDto obtenerMenuPorId(String id) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting menu by id: {} for tenant: {}", id, tenantId);

        Menu menu = menuRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Menu no encontrado con id: " + id));

        return mapper.toDto(menu);
    }

    @Override
    public List<MenuDto> obtenerMenusActivos() {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting active menus for tenant: {}", tenantId);

        return menuRepository.findByTenantIdAndActivoTrue(tenantId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MenuDto> obtenerMenus(Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting paginated menus for tenant: {}", tenantId);

        return menuRepository.findByTenantId(tenantId, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    @CacheEvict(value = "menus", key = "#id + '_obtenerMenuPorId'")
    public MenuDto actualizarMenu(String id, MenuDto menuDto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Updating menu with id: {} for tenant: {}", id, tenantId);

        Menu existingMenu = menuRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Menu no encontrado con id: " + id));

        // Validar nombre único si cambió
        if (!existingMenu.getNombre().equals(menuDto.getNombre()) &&
                menuRepository.existsByTenantIdAndNombreIgnoreCase(tenantId, menuDto.getNombre())) {
            throw new BadRequestException("Ya existe un menú con el nombre: " + menuDto.getNombre());
        }

        existingMenu.setNombre(menuDto.getNombre());
        existingMenu.setDescripcion(menuDto.getDescripcion());
        existingMenu.setActivo(menuDto.getActivo());
        existingMenu.setImagenUrl(menuDto.getImagenUrl());

        Menu updatedMenu = menuRepository.save(existingMenu);
        LOGGER.info("Menu updated successfully with id: {}", updatedMenu.getId());

        return mapper.toDto(updatedMenu);
    }

    @Override
    @Transactional
    @CacheEvict(value = "menus", key = "#id + '_obtenerMenuPorId'")
    public void eliminarMenu(String id) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Deleting menu with id: {} for tenant: {}", id, tenantId);

        Menu menu = menuRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Menu no encontrado con id: " + id));

        menuRepository.delete(menu);
        LOGGER.info("Menu deleted successfully with id: {}", id);
    }

    @Override
    public List<MenuDto> buscarMenusPorNombre(String nombre) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Searching menus by name: {} for tenant: {}", nombre, tenantId);

        return menuRepository.findByTenantIdAndNombreContainingIgnoreCase(tenantId, nombre)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // ==================== MENU ITEM OPERATIONS ====================

    @Override
    @Transactional
    public MenuItemDto crearMenuItem(MenuItemDto menuItemDto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Creating menu item for tenant: {}", tenantId);

        // Validar que no exista un item con el mismo nombre
        if (menuItemRepository.existsByTenantIdAndNombreIgnoreCase(tenantId, menuItemDto.getNombre())) {
            throw new BadRequestException("Ya existe un item con el nombre: " + menuItemDto.getNombre());
        }

        // Validar precio
        if (menuItemDto.getPrecio() == null || menuItemDto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El precio debe ser mayor a 0");
        }

        MenuItem menuItem = mapper.toEntity(menuItemDto);
        menuItem.setTenantId(tenantId);
        menuItem.setDisponible(true);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        LOGGER.info("Menu item created successfully with id: {}", savedMenuItem.getId());

        return mapper.toDto(savedMenuItem);
    }

    @Override
    @Cacheable(value = "menuItems", key = "#id + '_' + #root.method.name")
    public MenuItemDto obtenerMenuItemPorId(String id) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting menu item by id: {} for tenant: {}", id, tenantId);

        MenuItem menuItem = menuItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Menu item no encontrado con id: " + id));

        return mapper.toDto(menuItem);
    }

    @Override
    public List<MenuItemDto> obtenerMenuItemsDisponibles() {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting available menu items for tenant: {}", tenantId);

        return menuItemRepository.findByTenantIdAndDisponibleTrue(tenantId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MenuItemDto> obtenerMenuItems(Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting paginated menu items for tenant: {}", tenantId);

        return menuItemRepository.findByTenantId(tenantId, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    @CacheEvict(value = "menuItems", key = "#id + '_obtenerMenuItemPorId'")
    public MenuItemDto actualizarMenuItem(String id, MenuItemDto menuItemDto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Updating menu item with id: {} for tenant: {}", id, tenantId);

        MenuItem existingItem = menuItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Menu item no encontrado con id: " + id));

        // Validar nombre único si cambió
        if (!existingItem.getNombre().equals(menuItemDto.getNombre()) &&
                menuItemRepository.existsByTenantIdAndNombreIgnoreCase(tenantId, menuItemDto.getNombre())) {
            throw new BadRequestException("Ya existe un item con el nombre: " + menuItemDto.getNombre());
        }

        // Validar precio
        if (menuItemDto.getPrecio() != null && menuItemDto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El precio debe ser mayor a 0");
        }

        // Actualizar campos
        existingItem.setNombre(menuItemDto.getNombre());
        existingItem.setDescripcion(menuItemDto.getDescripcion());
        existingItem.setPrecio(menuItemDto.getPrecio());
        existingItem.setCategoria(menuItemDto.getCategoria());
        existingItem.setDisponible(menuItemDto.getDisponible());
        existingItem.setImagenUrl(menuItemDto.getImagenUrl());
        existingItem.setIngredientes(menuItemDto.getIngredientes());
        existingItem.setAlergenos(menuItemDto.getAlergenos());
        existingItem.setTiempoPreparacion(menuItemDto.getTiempoPreparacion());

        MenuItem updatedItem = menuItemRepository.save(existingItem);
        LOGGER.info("Menu item updated successfully with id: {}", updatedItem.getId());

        return mapper.toDto(updatedItem);
    }

    @Override
    @Transactional
    @CacheEvict(value = "menuItems", key = "#id + '_obtenerMenuItemPorId'")
    public void eliminarMenuItem(String id) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Deleting menu item with id: {} for tenant: {}", id, tenantId);

        MenuItem menuItem = menuItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Menu item no encontrado con id: " + id));

        menuItemRepository.delete(menuItem);
        LOGGER.info("Menu item deleted successfully with id: {}", id);
    }

    @Override
    public List<MenuItemDto> obtenerMenuItemsPorCategoria(Categoria categoria) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting menu items by category: {} for tenant: {}", categoria, tenantId);

        return menuItemRepository.findByTenantIdAndCategoriaAndDisponibleTrue(tenantId, categoria)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemDto> buscarMenuItemsPorNombre(String nombre) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Searching menu items by name: {} for tenant: {}", nombre, tenantId);

        return menuItemRepository.findByTenantIdAndNombreContainingIgnoreCase(tenantId, nombre)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemDto> obtenerMenuItemsPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting menu items by price range: {}-{} for tenant: {}", precioMin, precioMax, tenantId);

        if (precioMin.compareTo(precioMax) > 0) {
            throw new BadRequestException("El precio mínimo no puede ser mayor al precio máximo");
        }

        return menuItemRepository.findAvailableByTenantIdAndPriceRange(tenantId, precioMin, precioMax)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaDto> obtenerEstadisticasCategorias() {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting category statistics for tenant: {}", tenantId);

        // Obtener todos los items disponibles
        List<MenuItem> allItems = menuItemRepository.findByTenantIdAndDisponibleTrue(tenantId);

        // Agrupar por categoría y contar
        Map<Categoria, Long> categoryCount = allItems.stream()
                .collect(Collectors.groupingBy(MenuItem::getCategoria, Collectors.counting()));

        // Crear DTOs con estadísticas
        return Arrays.stream(Categoria.values())
                .map(categoria -> {
                    CategoriaDto dto = new CategoriaDto();
                    dto.setCategoria(categoria);
                    dto.setNombre(formatCategoryName(categoria));
                    dto.setCantidad(categoryCount.getOrDefault(categoria, 0L));
                    dto.setDescripcion(getCategoryDescription(categoria));
                    return dto;
                })
                .filter(dto -> dto.getCantidad() > 0) // Solo categorías con items
                .sorted((a, b) -> b.getCantidad().compareTo(a.getCantidad())) // Ordenar por cantidad
                .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================

    private String formatCategoryName(Categoria categoria) {
        String name = categoria.name().replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    private String getCategoryDescription(Categoria categoria) {
        switch (categoria) {
            case ENTRADA:
                return "Platos de entrada y aperitivos";
            case PLATO_PRINCIPAL:
                return "Platos principales del menú";
            case POSTRE:
                return "Postres y dulces";
            case BEBIDA:
                return "Bebidas frías y calientes";
            case SNACK:
                return "Snacks y picoteos";
            case ENSALADA:
                return "Ensaladas frescas";
            case SOPA:
                return "Sopas y cremas";
            case PIZZA:
                return "Variedad de pizzas";
            case HAMBURGUESA:
                return "Hamburguesas gourmet";
            case PASTA:
                return "Pastas y platos italianos";
            case VEGETARIANO:
                return "Opciones vegetarianas";
            case VEGANO:
                return "Opciones veganas";
            default:
                return "";
        }
    }
}