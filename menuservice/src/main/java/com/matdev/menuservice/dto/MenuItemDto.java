package com.matdev.menuservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.matdev.menuservice.domain.model.Categoria;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuItemDto {
    private String id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Categoria categoria;
    private Boolean disponible;
    private String imagenUrl;
    private List<String> ingredientes;
    private List<String> alergenos;
    private Integer tiempoPreparacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
