package com.matdev.menuservice.service;

import com.matdev.menuservice.domain.model.Categoria;
import com.matdev.menuservice.dto.CategoriaDto;
import com.matdev.menuservice.dto.MenuDto;
import com.matdev.menuservice.dto.MenuItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface MenuService {
    // Menu operations
    MenuDto crearMenu(MenuDto menuDto);
    MenuDto obtenerMenuPorId(String id);
    List<MenuDto> obtenerMenusActivos();
    Page<MenuDto> obtenerMenus(Pageable pageable);
    MenuDto actualizarMenu(String id, MenuDto menuDto);
    void eliminarMenu(String id);
    List<MenuDto> buscarMenusPorNombre(String nombre);

    // MenuItem operations
    MenuItemDto crearMenuItem(MenuItemDto menuItemDto);
    MenuItemDto obtenerMenuItemPorId(String id);
    List<MenuItemDto> obtenerMenuItemsDisponibles();
    Page<MenuItemDto> obtenerMenuItems(Pageable pageable);
    MenuItemDto actualizarMenuItem(String id, MenuItemDto menuItemDto);
    void eliminarMenuItem(String id);
    List<MenuItemDto> obtenerMenuItemsPorCategoria(Categoria categoria);
    List<MenuItemDto> buscarMenuItemsPorNombre(String nombre);
    List<MenuItemDto> obtenerMenuItemsPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax);

    // Category operations
    List<CategoriaDto> obtenerEstadisticasCategorias();
}