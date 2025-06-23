package com.example.Gestion_de_ventas.Controller;
import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Service.DetalleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/detalles")
public class DetalleController {

    @Autowired
    private DetalleService detalleService;

    @GetMapping
    public ResponseEntity<List<Detalle>> getAll() {
        List<Detalle> lista = detalleService.obtenerTodos();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
    Optional<Detalle> detalle = detalleService.obtenerPorId(id);
    if (detalle.isPresent()) {
        return ResponseEntity.ok(detalle.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle no encontrado con ID: " + id);
    }
}


    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<Detalle>> getByVenta(@PathVariable Long ventaId) {
        List<Detalle> lista = detalleService.obtenerPorVenta(ventaId);
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Detalle detalle) {
        try {
            Detalle creado = detalleService.crearDetalle(detalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear detalle: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDetalle(@PathVariable Long id, @RequestBody Detalle datos) {
        try {
            Detalle actualizado = detalleService.actualizarDetalle(id, datos);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }   

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        detalleService.eliminarDetalle(id);
        return ResponseEntity.noContent().build();
    }
}