package com.matdev.admin_service.mapper;

import com.matdev.admin_service.domain.model.ConfiguracionVisual;
import com.matdev.admin_service.domain.model.Modulo;
import com.matdev.admin_service.domain.model.Sucursal;
import com.matdev.admin_service.dto.ConfiguracionVisualDto;
import com.matdev.admin_service.dto.ModuloDto;
import com.matdev.admin_service.dto.SucursalDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {
    private final ModelMapper mapper = new ModelMapper();

    public SucursalDto toDto(Sucursal entity) {
        return mapper.map(entity, SucursalDto.class);
    }

    public Sucursal toEntity(SucursalDto dto) {
        return mapper.map(dto, Sucursal.class);
    }

    public ConfiguracionVisualDto toDto(ConfiguracionVisual entity) {
        return mapper.map(entity, ConfiguracionVisualDto.class);
    }

    public ConfiguracionVisual toEntity(ConfiguracionVisualDto dto) {
        return mapper.map(dto, ConfiguracionVisual.class);
    }

    public ModuloDto toDto(Modulo entity) {
        return mapper.map(entity, ModuloDto.class);
    }

    public Modulo toEntity(ModuloDto dto) {
        return mapper.map(dto, Modulo.class);
    }
}
