
package com.example.Gestion_de_ventas.Model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long id;

    @Column(name = "fecha_venta", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaVenta;

    @Column(nullable = false)
    private Integer total;

    @Column(name = "Usuarios_id_usuarios", nullable = false)
    private Long usuarioId;

    @Column(name = "Direcccion_id_direccion", nullable = false)
    private Long direccionId;

    @Transient
    private String nombreUsuario; // lo puedes completar luego con WebClient

    @Transient
    private String direccionCompleta; // lo mismo
}