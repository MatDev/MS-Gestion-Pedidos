package com.matdev.admin_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SucursalDto {
    private UUID id;
    private String nombre;
    private String direccion;
    private String telefono;
}
