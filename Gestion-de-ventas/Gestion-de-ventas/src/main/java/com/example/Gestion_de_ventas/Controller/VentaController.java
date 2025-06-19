package com.example.Gestion_de_ventas.Controller;

import java.util.List;
import java.util.Optional;

import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Service.VentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<Venta>> getAll() {
        List<Venta> lista = ventaService.obtenerTodasLasVentas();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Venta> venta = ventaService.obtenerVentaPorId(id);
        if (venta.isPresent()) {
            return ResponseEntity.ok(venta.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Venta no encontrada con ID: " + id);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venta>> getByUsuario(@PathVariable Long usuarioId) {
        List<Venta> lista = ventaService.obtenerVentasPorUsuario(usuarioId);
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Venta venta) {
        try {
            Venta creada = ventaService.crearVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear venta: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Venta v) {
        try {
            Venta actualizada = ventaService.actualizarVenta(id, v);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al actualizar venta: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }
}
