package com.matdev.admin_service.service;

import com.matdev.admin_service.dto.ConfiguracionVisualDto;
import com.matdev.admin_service.dto.ModuloDto;
import com.matdev.admin_service.dto.SucursalDto;

import java.util.List;

public interface AdminService {
    SucursalDto obtenerSucursal();
    SucursalDto guardarSucursal(SucursalDto dto);

    ConfiguracionVisualDto obtenerConfiguracion();
    ConfiguracionVisualDto guardarConfiguracion(ConfiguracionVisualDto dto);

    List<ModuloDto> obtenerModulos();
    List<ModuloDto> guardarModulos(List<ModuloDto> modulos);
}
