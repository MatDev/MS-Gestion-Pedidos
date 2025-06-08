package com.matdev.admin_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.matdev.admin_service.domain.model.Modulo.NombreModulo;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModuloDto {
    private UUID id;
    private NombreModulo nombre;
    private Boolean habilitado;
}
