package com.example.Gestion_de_ventas.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo de Detalle de Venta")
public class Detalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    @Schema(description = "ID del detalle de la venta", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Cantidad de productos en el detalle", example = "2")
    private int cantidad;

    @Column(nullable = false)
    @Schema(description = "Subtotal del detalle de la venta", example = "30000")
    private int subtotal;

    @Column(name = "Producto_id_producto", nullable = false)
    @Schema(description = "ID del producto asociado al detalle", example = "1")
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Venta_id_venta", nullable = false)
    @JsonBackReference
    @Schema(description = "Venta asociada al detalle")
    private Venta venta;

    @Transient
    @Schema(description = "Nombre del producto asociado al detalle", example = "Producto A")
    private String nombreProducto;
}
