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
import com.matdev.menuservice.exception.NotFoundException;
import com.matdev.menuservice.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuMapper mapper;
    // CONSTANTE
    private static final String NOTFOUND = "Menu item not found with id: ";

    @Override
    @Transactional
    public MenuDto crearMenu(MenuDto menuDto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Creating menu for tenant: {}", tenantId);

        Menu menu = mapper.toEntity(menuDto);
        menu.setTenantId(tenantId);
        menu.setActivo(true);

        Menu savedMenu = menuRepository.save(menu);
        LOGGER.info("Menu created with id: {}", savedMenu.getId());

        return mapper.toDto(savedMenu);
    }

    @Override
    public MenuDto obtenerMenuPorId(String id) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting menu by id: {} for tenant: {}", id, tenantId);

        Menu menu = menuRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException(NOTFOUND + id));

        return mapper.toDto(menu);
    }

    @Override
    public List<MenuDto> obtenerMenusActivos() {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting active menus for tenant: {}", tenantId);

        List<Menu> menus = menuRepository.findByTenantIdAndActivoTrue(tenantId);
        return menus.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public Page<MenuDto> obtenerMenus(Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting paginated menus for tenant: {}", tenantId);

        Page<Menu> menus = menuRepository.findByTenantId(tenantId, pageable);
        return menus.map(mapper::toDto);
    }

    @Override
    @Transactional
    public MenuDto actualizarMenu(String id, MenuDto menuDto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Updating menu with id: {} for tenant: {}", id, tenantId);

        Menu existingMenu = menuRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException(NOTFOUND + id));

        // Update fields
        existingMenu.setNombre(menuDto.getNombre());
        existingMenu.setDescripcion(menuDto.getDescripcion());
        existingMenu.setActivo(menuDto.getActivo());
        existingMenu.setImagenUrl(menuDto.getImagenUrl());

        Menu updatedMenu = menuRepository.save(existingMenu);
        LOGGER.info("Menu updated with id: {}", updatedMenu.getId());

        return mapper.toDto(updatedMenu);
    }

    @Override
    @Transactional
    public void eliminarMenu(String id) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Deleting menu with id: {} for tenant: {}", id, tenantId);

        Menu menu = menuRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException(NOTFOUND + id));

        menuRepository.delete(menu);
        LOGGER.info("Menu deleted with id: {}", id);
    }

    @Override
    public List<MenuDto> buscarMenusPorNombre(String nombre) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Searching menus by name: {} for tenant: {}", nombre, tenantId);

        List<Menu> menus = menuRepository.findByTenantIdAndNombreContainingIgnoreCase(tenantId, nombre);
        return menus.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }



    // MenuItem operations
    @Override
    @Transactional
    public MenuItemDto crearMenuItem(MenuItemDto menuItemDto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Creating menu item for tenant: {}", tenantId);

        MenuItem menuItem = mapper.toEntity(menuItemDto);
        menuItem.setTenantId(tenantId);
        menuItem.setDisponible(true);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        LOGGER.info("Menu item created with id: {}", savedMenuItem.getId());

        return mapper.toDto(savedMenuItem);
    }

    @Override
    public MenuItemDto obtenerMenuItemPorId(String id) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting menu item by id: {} for tenant: {}", id, tenantId);

        MenuItem menuItem = menuItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException(NOTFOUND + id));

        return mapper.toDto(menuItem);
    }

    @Override
    public List<MenuItemDto> obtenerMenuItemsDisponibles() {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting available menu items for tenant: {}", tenantId);

        List<MenuItem> menuItems = menuItemRepository.findByTenantIdAndDisponibleTrue(tenantId);
        return menuItems.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MenuItemDto> obtenerMenuItems(Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting paginated menu items for tenant: {}", tenantId);

        Page<MenuItem> menuItems = menuItemRepository.findByTenantId(tenantId, pageable);
        return menuItems.map(mapper::toDto);
    }

    @Override
    @Transactional
    public MenuItemDto actualizarMenuItem(String id, MenuItemDto menuItemDto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Updating menu item with id: {} for tenant: {}", id, tenantId);

        MenuItem existingMenuItem = menuItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Menu item not found with id: " + id));

        // Update fields
        existingMenuItem.setNombre(menuItemDto.getNombre());
        existingMenuItem.setDescripcion(menuItemDto.getDescripcion());
        existingMenuItem.setPrecio(menuItemDto.getPrecio());
        existingMenuItem.setCategoria(menuItemDto.getCategoria());
        existingMenuItem.setDisponible(menuItemDto.getDisponible());
        existingMenuItem.setImagenUrl(menuItemDto.getImagenUrl());
        existingMenuItem.setIngredientes(menuItemDto.getIngredientes());
        existingMenuItem.setAlergenos(menuItemDto.getAlergenos());
        existingMenuItem.setTiempoPreparacion(menuItemDto.getTiempoPreparacion());

        MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
        LOGGER.info("Menu item updated with id: {}", updatedMenuItem.getId());

        return mapper.toDto(updatedMenuItem);
    }

    @Override
    @Transactional
    public void eliminarMenuItem(String id) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Deleting menu item with id: {} for tenant: {}", id, tenantId);

        MenuItem menuItem = menuItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Menu item not found with id: " + id));

        menuItemRepository.delete(menuItem);
        LOGGER.info("Menu item deleted with id: {}", id);
    }


    @Override
    public List<MenuItemDto> obtenerMenuItemsPorCategoria(Categoria categoria) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting menu items by category: {} for tenant: {}", categoria, tenantId);

        List<MenuItem> menuItems = menuItemRepository.findByTenantIdAndCategoria(tenantId, categoria);
        return menuItems.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemDto> buscarMenuItemsPorNombre(String nombre) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Searching menu items by name: {} for tenant: {}", nombre, tenantId);

        List<MenuItem> menuItems = menuItemRepository.findByTenantIdAndNombreContainingIgnoreCase(tenantId, nombre);
        return menuItems.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }



    @Override
    public List<MenuItemDto> obtenerMenuItemsPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting menu items by price range: {}-{} for tenant: {}", precioMin, precioMax, tenantId);

        List<MenuItem> menuItems = menuItemRepository.findByTenantIdAndPrecioBetween(tenantId, precioMin, precioMax);
        return menuItems.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaDto> obtenerEstadisticasCategorias() {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Getting category statistics for tenant: {}", tenantId);

        List<MenuItem> allItems = menuItemRepository.findByTenantIdAndDisponibleTrue(tenantId);

        return allItems.stream()
                .collect(Collectors.groupingBy(MenuItem::getCategoria, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> {
                    CategoriaDto dto = new CategoriaDto();
                    dto.setCategoria(entry.getKey());
                    dto.setNombre(entry.getKey().name());
                    dto.setCantidad(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}