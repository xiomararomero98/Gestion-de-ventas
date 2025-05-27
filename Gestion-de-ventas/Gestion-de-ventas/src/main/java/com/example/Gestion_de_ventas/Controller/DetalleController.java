package com.example.Gestion_de_ventas.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.DetalleConNombreProducto;
import com.example.Gestion_de_ventas.Service.DetalleService;
import com.example.Gestion_de_ventas.WebClient.ProductoClient;

@RestController
@RequestMapping("/api/v1/detalles")
public class DetalleController {

    @Autowired
    private ProductoClient productoClient;


    @Autowired
    private DetalleService detalleService;

    // GET con venta enriquecida
    @GetMapping
    public ResponseEntity<List<Detalle>> obtenerDetalles() {
        List<Detalle> detalles = detalleService.obtenerDetallesConVentaCompleta();
        if (detalles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(detalles);
    }
    // Obtener detalle por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetallePorId(@PathVariable Long id) {
    Optional<Detalle> detalle = detalleService.obtenerDetallePorId(id);
    if (detalle.isPresent()) {
        return ResponseEntity.ok(detalle.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle no encontrado con ID: " + id);
    }
}



    // Crear nuevo detalle
    @PostMapping
    public ResponseEntity<?> crearDetalle(@RequestBody Detalle detalle) {
        try {
            Detalle nuevo = detalleService.crearDetalle(detalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear el detalle: " + e.getMessage());
        }
    }

    // Actualizar detalle existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDetalle(@PathVariable Long id, @RequestBody Detalle datos) {
        try {
            Detalle actualizado = detalleService.actualizarDetalle(id, datos);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Eliminar detalle
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDetalle(@PathVariable Long id) {
        try {
            detalleService.eliminarDetalle(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el detalle: " + e.getMessage());
        }
    }
}
