package com.matdev.admin_service.service.impl;

import com.matdev.admin_service.context.TenantContext;
import com.matdev.admin_service.domain.model.ConfiguracionVisual;
import com.matdev.admin_service.domain.model.Modulo;
import com.matdev.admin_service.domain.model.Sucursal;
import com.matdev.admin_service.domain.repository.ConfiguracionVisualRepository;
import com.matdev.admin_service.domain.repository.ModuloRepository;
import com.matdev.admin_service.domain.repository.SucursalRepository;
import com.matdev.admin_service.dto.ConfiguracionVisualDto;
import com.matdev.admin_service.dto.ModuloDto;
import com.matdev.admin_service.dto.SucursalDto;
import com.matdev.admin_service.mapper.AdminMapper;
import com.matdev.admin_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final SucursalRepository sucursalRepository;
    private final ConfiguracionVisualRepository configuracionVisualRepository;
    private final ModuloRepository moduloRepository;
    private final AdminMapper mapper;

    @Override
    public SucursalDto obtenerSucursal() {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Fetching branch info for tenant {}", tenantId);
        return sucursalRepository.findByTenantId(tenantId)
                .map(mapper::toDto)
                .orElseGet(SucursalDto::new);
    }

    @Override
    @Transactional
    public SucursalDto guardarSucursal(SucursalDto dto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Saving branch info for tenant {}", tenantId);
        Sucursal entity = mapper.toEntity(dto);
        entity.setTenantId(tenantId);
        Sucursal saved = sucursalRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public ConfiguracionVisualDto obtenerConfiguracion() {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Fetching config for tenant {}", tenantId);
        return configuracionVisualRepository.findByTenantId(tenantId)
                .map(mapper::toDto)
                .orElseGet(ConfiguracionVisualDto::new);
    }

    @Override
    @Transactional
    public ConfiguracionVisualDto guardarConfiguracion(ConfiguracionVisualDto dto) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Saving config for tenant {}", tenantId);
        ConfiguracionVisual entity = mapper.toEntity(dto);
        entity.setTenantId(tenantId);
        ConfiguracionVisual saved = configuracionVisualRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public List<ModuloDto> obtenerModulos() {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Fetching modules for tenant {}", tenantId);
        return moduloRepository.findAllByTenantId(tenantId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ModuloDto> guardarModulos(List<ModuloDto> modulos) {
        String tenantId = TenantContext.getTenantId();
        LOGGER.info("Saving modules for tenant {}", tenantId);
        List<Modulo> entities = modulos.stream()
                .map(mapper::toEntity)
                .peek(m -> m.setTenantId(tenantId))
                .collect(Collectors.toList());
        return moduloRepository.saveAll(entities)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
