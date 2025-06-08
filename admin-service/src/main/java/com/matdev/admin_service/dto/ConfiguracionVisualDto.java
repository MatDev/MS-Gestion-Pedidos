package com.matdev.admin_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfiguracionVisualDto {
    private UUID id;
    private String colorPrimario;
    private String colorSecundario;
    private String logoUrl;
    private Boolean modoOscuro;
    private String tipografia;
}
