package com.example.Gestion_de_ventas.Controller;

import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // POST /ventas → proceso completo de compra
    @PostMapping
    public ResponseEntity<?> crearVenta(@RequestBody Venta venta) {
        try {
            Venta creada = ventaService.crearVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al crear venta: " + e.getMessage());
        }
    }

    // GET /ventas
    @GetMapping
    public ResponseEntity<List<Venta>> getAll() {
        List<Venta> lista = ventaService.obtenerTodasLasVentas();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
    Optional<Venta> venta = ventaService.obtenerVentaPorId(id);
    if (venta.isPresent()) {
        return ResponseEntity.ok(venta.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Venta no encontrada con ID: " + id);
    }
}

    // GET /ventas/usuario/{id}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venta>> getByUsuario(@PathVariable Long usuarioId) {
        List<Venta> ventas = ventaService.obtenerVentasPorUsuario(usuarioId);
        if (ventas.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(ventas);
    }

    // GET /ventas/{id}/productos
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<Detalle>> obtenerProductosPorVenta(@PathVariable Long id) {
        List<Detalle> detalles = ventaService.obtenerProductosPorVenta(id);
        if (detalles.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(detalles);
    }

    // PUT /ventas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Venta datos) {
        try {
            Venta actualizada = ventaService.actualizarVenta(id, datos);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // DELETE /ventas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }
}