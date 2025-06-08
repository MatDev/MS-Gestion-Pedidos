package com.matdev.admin_service.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class ConfiguracionVisual {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String tenantId;

    private String colorPrimario;
    private String colorSecundario;
    private String logoUrl;
    private Boolean modoOscuro;
    private String tipografia;
}
