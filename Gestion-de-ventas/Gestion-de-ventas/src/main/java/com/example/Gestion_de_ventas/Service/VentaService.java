package com.example.Gestion_de_ventas.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Repository.VentaRepository;
import com.example.Gestion_de_ventas.WebClient.DireccionClient;
import com.example.Gestion_de_ventas.WebClient.UsuarioClient;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private DireccionClient direccionClient;

    public List<Venta> obtenerTodasLasVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        for (Venta venta : ventas) {
            cargarDatosExternos(venta);
        }
        return ventas;
    }

    public Optional<Venta> obtenerVentaPorId(Long id) {
        Optional<Venta> optional = ventaRepository.findById(id);
        if (optional.isPresent()) {
            Venta venta = optional.get();
            cargarDatosExternos(venta);
            return Optional.of(venta);
        } else {
            return Optional.empty();
        }
    }

    public Venta crearVenta(Venta nuevaVenta) {
        try {
            var usuario = usuarioClient.getUsuarioById(nuevaVenta.getUsuarioId());
            System.out.println("Respuesta del microservicio Usuario: " + usuario);
        } catch (Exception e) {
            throw new RuntimeException("Fallo al contactar al microservicio de Usuario: " + e.getMessage());
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
            ventaExistente.setDireccionId(datosActualizados.getDireccionId());
            return ventaRepository.save(ventaExistente);
        } else {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
    }

    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }

    // Método reutilizable para cargar usuario y dirección
    private void cargarDatosExternos(Venta venta) {
        try {
            var usuario = usuarioClient.getUsuarioById(venta.getUsuarioId());
            if (usuario != null && usuario.containsKey("nombre")) {
                venta.setNombreUsuario(usuario.get("nombre").toString());
            } else {
                venta.setNombreUsuario("Desconocido");
            }
        } catch (Exception e) {
            venta.setNombreUsuario("Error al obtener usuario");
        }

        try {
            var direccion = direccionClient.getDireccionById(venta.getDireccionId());
            if (direccion != null && direccion.containsKey("calle")) {
                String calle = direccion.get("calle").toString();
                String numero = direccion.get("numeracion") != null ? direccion.get("numeracion").toString() : "";
                venta.setDireccionCompleta(calle + " " + numero);
            } else {
                venta.setDireccionCompleta("Desconocida");
            }
        } catch (Exception e) {
            venta.setDireccionCompleta("Error al obtener dirección");
        }
    }
}
