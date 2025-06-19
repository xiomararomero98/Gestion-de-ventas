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
        ventas.forEach(this::cargarDatosExternos);
        return ventas;
    }

    public Optional<Venta> obtenerVentaPorId(Long id) {
        Optional<Venta> venta = ventaRepository.findById(id);
        venta.ifPresent(this::cargarDatosExternos);
        return venta;
    }

    public List<Venta> obtenerVentasPorUsuario(Long usuarioId) {
        List<Venta> ventas = ventaRepository.findByUsuarioId(usuarioId);
        ventas.forEach(this::cargarDatosExternos);
        return ventas;
    }

    public Venta crearVenta(Venta venta) {
        var usuario = usuarioClient.getUsuarioById(venta.getUsuarioId());
        if (usuario == null || usuario.isEmpty()) throw new RuntimeException("Usuario no encontrado");
        return ventaRepository.save(venta);
    }

    public Venta actualizarVenta(Long id, Venta datos) {
        return ventaRepository.findById(id).map(v -> {
            v.setFechaVenta(datos.getFechaVenta());
            v.setTotal(datos.getTotal());
            v.setUsuarioId(datos.getUsuarioId());
            v.setDireccionId(datos.getDireccionId());
            return ventaRepository.save(v);
        }).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    public void eliminarVenta(Long id) { ventaRepository.deleteById(id); }

    private void cargarDatosExternos(Venta venta) {
        try {
            var usuario = usuarioClient.getUsuarioById(venta.getUsuarioId());
            venta.setNombreUsuario(usuario.get("nombre").toString());
        } catch (Exception e) { venta.setNombreUsuario("Desconocido"); }

        try {
            var direccion = direccionClient.getDireccionById(venta.getDireccionId());
            venta.setDireccionCompleta(direccion.get("calle") + " " + direccion.get("numeracion"));
        } catch (Exception e) { venta.setDireccionCompleta("Desconocida"); }
    }
}
