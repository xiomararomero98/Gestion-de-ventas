package com.example.Gestion_de_ventas.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Detalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long id;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private int subtotal;

    @Column(name = "Producto_id_producto", nullable = false)
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Venta_id_venta", nullable = false)
    @JsonBackReference
    private Venta venta;

    @Transient
    private String nombreProducto;
}
