package com.matdev.menuservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "menu_items")
@CompoundIndex(name = "tenant_categoria_idx", def = "{'tenantId': 1, 'categoria': 1}")
@CompoundIndex(name = "tenant_disponible_idx", def = "{'tenantId': 1, 'disponible': 1}")
@CompoundIndex(name = "tenant_precio_idx", def = "{'tenantId': 1, 'precio': 1}")
public class MenuItem {
    @Id
    private String id;

    @Indexed(name = "tenant_idx")
    private String tenantId;

    private String nombre;
    private String descripcion;

    private BigDecimal precio;

    @Indexed
    private Categoria categoria;

    @Builder.Default
    private Boolean disponible = true;

    private String imagenUrl;

    @Builder.Default
    private List<String> ingredientes = new ArrayList<>();

    @Builder.Default
    private List<String> alergenos = new ArrayList<>();

    private Integer tiempoPreparacion; // en minutos

    private Integer calorias;

    private Boolean esVegetariano;
    private Boolean esVegano;
    private Boolean sinGluten;

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaModificacion;
}