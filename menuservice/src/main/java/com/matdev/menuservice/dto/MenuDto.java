package com.matdev.menuservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuDto {
    private String id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private String imagenUrl;
    private List<MenuItemDto> items;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}