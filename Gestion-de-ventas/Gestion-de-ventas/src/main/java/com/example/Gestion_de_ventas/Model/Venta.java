package com.example.Gestion_de_ventas.Model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo de Venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    @Schema(description = "ID de la venta", example = "1")
    private Long id;

    @Column(name = "fecha_venta", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "Fecha de la venta", example = "2023-10-01T12:00:00Z")
    private Date fechaVenta;

    @Column(nullable = false)
    @Schema(description = "Total de la venta", example = "150.000")
    private Integer total;

    @Column(name = "Usuarios_id_usuarios", nullable = false)
    @Schema(description = "ID del usuario que realizó la venta", example = "1")
    private Long usuarioId;

    @Column(name = "Direcccion_id_direccion", nullable = false)
    @Schema(description = "ID de la dirección de envío", example = "1")
    private Long direccionId;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Schema(description = "Lista de detalles de la venta")
    private List<Detalle> detalles;

    @Transient
    @Schema(description = "Nombre del usuario que realizó la venta", example = "Juan Perez")
    private String nombreUsuario;

    @Transient
    @Schema(description = "Dirección completa de envío", example = "Calle Falsa 123, Ciudad, País")
    private String direccionCompleta;
}
