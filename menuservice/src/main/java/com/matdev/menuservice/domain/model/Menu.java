package com.matdev.menuservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "menus")
@CompoundIndex(name = "tenant_nombre_idx", def = "{'tenantId': 1, 'nombre': 1}")
@CompoundIndex(name = "tenant_activo_idx", def = "{'tenantId': 1, 'activo': 1}")
public class Menu {
    @Id
    private String id;

    @Indexed(name = "tenant_idx")
    private String tenantId;

    private String nombre;
    private String descripcion;

    @Builder.Default
    private Boolean activo = true;

    private String imagenUrl;

    @DBRef
    @Builder.Default
    private List<MenuItem> items = new ArrayList<>();

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaModificacion;
}