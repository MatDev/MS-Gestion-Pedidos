package com.matdev.admin_service.controller;

import com.matdev.admin_service.dto.ConfiguracionVisualDto;
import com.matdev.admin_service.dto.ModuloDto;
import com.matdev.admin_service.dto.SucursalDto;
import com.matdev.admin_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    @GetMapping("/sucursal")
    public ResponseEntity<SucursalDto> getSucursal() {
        LOGGER.info("GET /admin/sucursal");
        return ResponseEntity.ok(adminService.obtenerSucursal());
    }

    @PostMapping("/sucursal")
    public ResponseEntity<SucursalDto> saveSucursal(@RequestBody SucursalDto dto) {
        LOGGER.info("POST /admin/sucursal");
        return ResponseEntity.ok(adminService.guardarSucursal(dto));
    }

    @GetMapping("/config")
    public ResponseEntity<ConfiguracionVisualDto> getConfig() {
        LOGGER.info("GET /admin/config");
        return ResponseEntity.ok(adminService.obtenerConfiguracion());
    }

    @PostMapping("/config")
    public ResponseEntity<ConfiguracionVisualDto> saveConfig(@RequestBody ConfiguracionVisualDto dto) {
        LOGGER.info("POST /admin/config");
        return ResponseEntity.ok(adminService.guardarConfiguracion(dto));
    }

    @GetMapping("/modulos")
    public ResponseEntity<List<ModuloDto>> getModulos() {
        LOGGER.info("GET /admin/modulos");
        return ResponseEntity.ok(adminService.obtenerModulos());
    }

    @PostMapping("/modulos")
    public ResponseEntity<List<ModuloDto>> saveModulos(@RequestBody List<ModuloDto> modulos) {
        LOGGER.info("POST /admin/modulos");
        return ResponseEntity.ok(adminService.guardarModulos(modulos));
    }
}
