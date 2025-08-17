package com.matdev.menuservice.dto;

import com.matdev.menuservice.domain.model.Categoria;
import lombok.Data;

@Data
public class CategoriaDto {
    private Categoria categoria;
    private String nombre;
    private String descripcion;
    private Long cantidad;
}