package com.matdev.menuservice.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "menu_items")
public class MenuItem {
    @Id
    private String id;

    private String tenantId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Categoria categoria;
    private Boolean disponible;
    private String imagenUrl;
    private List<String> ingredientes;
    private List<String> alergenos;
    private Integer tiempoPreparacion; // en minutos

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaModificacion;
}