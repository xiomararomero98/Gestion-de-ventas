package com.example.Gestion_de_ventas.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Repository.VentaRepository;
import com.example.Gestion_de_ventas.WebClient.UsuarioClient;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public Venta crearVenta(Venta nuevaVenta) {
        var usuario = usuarioClient.getUsuarioById(nuevaVenta.getUsuarioId());
        if (usuario == null || usuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + nuevaVenta.getUsuarioId());
        }

        return ventaRepository.save(nuevaVenta);
    }

    public Venta actualizarVenta(Long id, Venta datosActualizados) {
        Optional<Venta> optional = ventaRepository.findById(id);
        if (optional.isPresent()) {
            Venta ventaExistente = optional.get();
            ventaExistente.setFechaVenta(datosActualizados.getFechaVenta());
            ventaExistente.setTotal(datosActualizados.getTotal());
            ventaExistente.setUsuarioId(datosActualizados.getUsuarioId());
            return ventaRepository.save(ventaExistente);
        } else {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
    }

    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }
}

