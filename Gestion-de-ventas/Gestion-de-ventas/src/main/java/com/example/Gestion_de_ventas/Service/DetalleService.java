package com.example.Gestion_de_ventas.Service;


import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Repository.DetalleRepository;
import com.example.Gestion_de_ventas.Repository.VentaRepository;
import com.example.Gestion_de_ventas.WebClient.ProductoClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleService {

    @Autowired
    private DetalleRepository detalleRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoClient productoClient;

    public List<Detalle> obtenerTodos() {
        List<Detalle> detalles = detalleRepository.findAll();
        detalles.forEach(this::cargarNombreProducto);
        return detalles;
    }

    public Optional<Detalle> obtenerPorId(Long id) {
        Optional<Detalle> detalle = detalleRepository.findById(id);
        detalle.ifPresent(this::cargarNombreProducto);
        return detalle;
    }

    public List<Detalle> obtenerPorVenta(Long ventaId) {
        List<Detalle> detalles = detalleRepository.findByVentaId(ventaId);
        detalles.forEach(this::cargarNombreProducto);
        return detalles;
    }

    public Detalle crearDetalle(Detalle detalle) {
        Optional<Venta> ventaOpt = ventaRepository.findById(detalle.getVenta().getId());
        if (ventaOpt.isEmpty()) throw new RuntimeException("Venta no encontrada");

        if (!productoExiste(detalle.getProductoId())) {
            throw new RuntimeException("Producto no encontrado");
        }

        detalle.setVenta(ventaOpt.get());
        return detalleRepository.save(detalle);
    }

    public Detalle actualizarDetalle(Long id, Detalle datos) {
        return detalleRepository.findById(id).map(d -> {
            d.setCantidad(datos.getCantidad());
            d.setSubtotal(datos.getSubtotal());

            if (!productoExiste(datos.getProductoId())) {
                throw new RuntimeException("Producto no válido");
            }

            d.setProductoId(datos.getProductoId());
            return detalleRepository.save(d);
        }).orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
    }

    public void eliminarDetalle(Long id) {
        detalleRepository.deleteById(id);
    }

    private boolean productoExiste(Long productoId) {
        try {
            var producto = productoClient.getProductoById(productoId);
            return producto != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void cargarNombreProducto(Detalle detalle) {
        try {
            var producto = productoClient.getProductoById(detalle.getProductoId());
            if (producto != null && producto.containsKey("nombre")) {
                detalle.setNombreProducto(producto.get("nombre").toString());
            } else {
                detalle.setNombreProducto("Desconocido");
            }
        } catch (Exception e) {
            detalle.setNombreProducto("Desconocido");
        }
    }
}