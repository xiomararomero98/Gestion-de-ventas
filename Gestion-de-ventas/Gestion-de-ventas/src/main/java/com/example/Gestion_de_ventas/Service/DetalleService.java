package com.example.Gestion_de_ventas.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Repository.DetalleRepository;
import com.example.Gestion_de_ventas.Repository.VentaRepository;
import com.example.Gestion_de_ventas.WebClient.ProductoClient;

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
        detalles.forEach(this::enriquecer);
        return detalles;
    }

    public Optional<Detalle> obtenerPorId(Long id) {
        return detalleRepository.findById(id);
    }

    public List<Detalle> obtenerPorVenta(Long ventaId) {
        List<Detalle> detalles = detalleRepository.findByVentaId(ventaId);
        detalles.forEach(this::enriquecer);
        return detalles;
    }

    public Detalle crearDetalle(Detalle detalle) {
        var producto = productoClient.getProductoById(detalle.getProductoId());
        if (producto == null || producto.isEmpty()) throw new RuntimeException("Producto no encontrado");
        Venta venta = ventaRepository.findById(detalle.getVenta().getId())
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        detalle.setVenta(venta);
        return detalleRepository.save(detalle);
    }

    public Detalle actualizarDetalle(Long id, Detalle nuevo) {
        return detalleRepository.findById(id).map(d -> {
            d.setCantidad(nuevo.getCantidad());
            d.setSubtotal(nuevo.getSubtotal());
            d.setProductoId(nuevo.getProductoId());
            d.setVenta(nuevo.getVenta());
            return detalleRepository.save(d);
        }).orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
    }

    public void eliminarDetalle(Long id) { detalleRepository.deleteById(id); }

    private void enriquecer(Detalle d) {
        try {
            var prod = productoClient.getProductoById(d.getProductoId());
            d.setNombreProducto(prod.get("nombre").toString());
        } catch (Exception e) {
            d.setNombreProducto("No disponible");
        }
    }
}
