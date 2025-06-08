package com.matdev.admin_service.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    private NombreModulo nombre;
    private Boolean habilitado;

    public enum NombreModulo {
        MESAS, PEDIDOS, CUENTA, ESTADISTICAS
    }
}
