package com.matdev.menuservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.matdev.menuservice.domain.model.Categoria;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuItemDto {

    private String id;

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 6, fraction = 2, message = "El precio debe tener máximo 6 dígitos enteros y 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "La categoría es requerida")
    private Categoria categoria;

    private Boolean disponible;
    private String imagenUrl;
    private List<String> ingredientes;
    private List<String> alergenos;

    @Min(value = 1, message = "El tiempo de preparación debe ser al menos 1 minuto")
    @Max(value = 180, message = "El tiempo de preparación no puede exceder 180 minutos")
    private Integer tiempoPreparacion;

    @Min(value = 0, message = "Las calorías no pueden ser negativas")
    private Integer calorias;

    private Boolean esVegetariano;
    private Boolean esVegano;
    private Boolean sinGluten;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}