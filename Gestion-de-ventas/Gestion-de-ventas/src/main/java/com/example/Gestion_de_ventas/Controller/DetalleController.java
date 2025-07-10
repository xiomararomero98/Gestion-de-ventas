package com.example.Gestion_de_ventas.Controller;
import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Service.DetalleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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

    @Operation(summary = "Obtener todos los detalles", description = "Devuelve una lista con todos los detalles registrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalles obtenidos correctamente"),
        @ApiResponse(responseCode = "204", description = "No hay detalles registrados")
    })
    @GetMapping
    public ResponseEntity<List<Detalle>> getAll() {
        List<Detalle> lista = detalleService.obtenerTodos();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener detalle por ID", description = "Devuelve un detalle específico según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle encontrado"),
        @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Detalle> detalle = detalleService.obtenerPorId(id);
        if (detalle.isPresent()) {
            return ResponseEntity.ok(detalle.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle no encontrado con ID: " + id);
        }
    }

    @Operation(summary = "Obtener detalles por ID de venta", description = "Devuelve todos los detalles asociados a una venta específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalles obtenidos correctamente")
    })
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<Detalle>> getByVenta(@PathVariable Long ventaId) {
        List<Detalle> lista = detalleService.obtenerPorVenta(ventaId);
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Crear un nuevo detalle", description = "Crea un nuevo registro de detalle de venta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Detalle creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Error al crear detalle")
    })
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Detalle detalle) {
        try {
            Detalle creado = detalleService.crearDetalle(detalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear detalle: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar un detalle", description = "Actualiza un detalle existente según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDetalle(@PathVariable Long id, @RequestBody Detalle datos) {
        try {
            Detalle actualizado = detalleService.actualizarDetalle(id, datos);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar un detalle", description = "Elimina un detalle de venta por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Detalle eliminado correctamente")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        detalleService.eliminarDetalle(id);
        return ResponseEntity.noContent().build();
    }
}