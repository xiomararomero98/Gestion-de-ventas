package com.example.Gestion_de_ventas.Controller;


import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Service.VentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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

    @Operation(summary = "Crear una nueva venta", description = "Crea una venta con todos los detalles asociados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Venta creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al crear la venta")
    })
    @PostMapping
    public ResponseEntity<?> crearVenta(@RequestBody Venta venta) {
        try {
            Venta creada = ventaService.crearVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al crear venta: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener todas las ventas", description = "Retorna una lista de todas las ventas registradas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ventas encontradas"),
        @ApiResponse(responseCode = "204", description = "No hay ventas registradas")
    })
    @GetMapping
    public ResponseEntity<List<Venta>> getAll() {
        List<Venta> lista = ventaService.obtenerTodasLasVentas();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener venta por ID", description = "Retorna una venta específica según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta encontrada"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
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

    @Operation(summary = "Obtener ventas por ID de usuario", description = "Muestra todas las ventas realizadas por un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ventas encontradas"),
        @ApiResponse(responseCode = "204", description = "El usuario no tiene ventas registradas")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venta>> getByUsuario(@PathVariable Long usuarioId) {
        List<Venta> ventas = ventaService.obtenerVentasPorUsuario(usuarioId);
        if (ventas.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(ventas);
    }

    @Operation(summary = "Obtener productos de una venta", description = "Retorna el detalle de productos asociados a una venta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos encontrados"),
        @ApiResponse(responseCode = "204", description = "La venta no tiene productos")
    })
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<Detalle>> obtenerProductosPorVenta(@PathVariable Long id) {
        List<Detalle> detalles = ventaService.obtenerProductosPorVenta(id);
        if (detalles.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(detalles);
    }

    @Operation(summary = "Actualizar una venta", description = "Actualiza los datos de una venta existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Venta datos) {
        try {
            Venta actualizada = ventaService.actualizarVenta(id, datos);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar una venta", description = "Elimina una venta por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Venta eliminada correctamente")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }
}