package com.example.Gestion_de_ventas.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Repository.DetalleRepository;
import com.example.Gestion_de_ventas.WebClient.ProductoClient;

@Service
public class DetalleService {

    @Autowired
    private DetalleRepository detalleRepository;

    @Autowired
    private ProductoClient productoClient;

    public List<Detalle> obtenerTodosLosDetalles() {
        return detalleRepository.findAll();
    }

    public Optional<Detalle> obtenerDetallePorId(Long id) {
        return detalleRepository.findById(id);
    }

    public Detalle crearDetalle(Detalle nuevoDetalle) {
    var producto = productoClient.getProductoById(nuevoDetalle.getProductoId());

    if (producto == null) {
        throw new RuntimeException("Producto no encontrado con ID: " + nuevoDetalle.getProductoId());
    }

    return detalleRepository.save(nuevoDetalle);
}


    public Detalle actualizarDetalle(Long id, Detalle datosActualizados) {
        Optional<Detalle> optional = detalleRepository.findById(id);
        if (optional.isPresent()) {
            Detalle existente = optional.get();
            existente.setCantidad(datosActualizados.getCantidad());
            existente.setSubtotal(datosActualizados.getSubtotal());
            existente.setProductoId(datosActualizados.getProductoId());
            existente.setVenta(datosActualizados.getVenta());
            return detalleRepository.save(existente);
        } else {
            throw new RuntimeException("Detalle no encontrado con ID: " + id);
        }
    }

    public void eliminarDetalle(Long id) {
        detalleRepository.deleteById(id);
    }
}
