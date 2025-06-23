package com.example.Gestion_de_ventas.Service;
import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Repository.DetalleRepository;
import com.example.Gestion_de_ventas.Repository.VentaRepository;
import com.example.Gestion_de_ventas.WebClient.DireccionClient;
import com.example.Gestion_de_ventas.WebClient.ProductoClient;
import com.example.Gestion_de_ventas.WebClient.UsuarioClient;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleRepository detalleRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private DireccionClient direccionClient;

    @Autowired
    private ProductoClient productoClient;

    public Venta crearVenta(Venta venta) {
        // Validar usuario
        var usuario = usuarioClient.getUsuarioById(venta.getUsuarioId());
        if (usuario == null || usuario.isEmpty()) throw new RuntimeException("Usuario no encontrado");

        // Validar dirección
        var direccion = direccionClient.getDireccionById(venta.getDireccionId());
        if (direccion == null || direccion.isEmpty()) throw new RuntimeException("Dirección no encontrada");

        // Calcular total y subtotal para cada detalle
        int total = 0;
        for (Detalle d : venta.getDetalles()) {
            var producto = productoClient.getProductoById(d.getProductoId());
            if (producto == null || producto.isEmpty()) {
                throw new RuntimeException("Producto no válido con ID: " + d.getProductoId());
            }

            int precio = Integer.parseInt(producto.get("precio").toString());
            int subtotal = d.getCantidad() * precio;
            d.setSubtotal(subtotal);
            total += subtotal;
        }

        // Asignar total calculado a la venta
        venta.setTotal(total);

        // Guardar venta sin detalles primero para generar ID
        List<Detalle> detallesTemp = venta.getDetalles();
        venta.setDetalles(null);
        Venta ventaGuardada = ventaRepository.save(venta);

        // Guardar detalles con referencia a la venta guardada
        for (Detalle d : detallesTemp) {
            d.setVenta(ventaGuardada);
            detalleRepository.save(d);
        }

        // Actualizar lista de detalles en la venta guardada
        ventaGuardada.setDetalles(detalleRepository.findByVentaId(ventaGuardada.getId()));

        // Cargar datos externos como nombre usuario, dirección, nombre producto en detalles
        cargarDatosExternos(ventaGuardada);

        return ventaGuardada;
    }

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

    public List<Detalle> obtenerProductosPorVenta(Long ventaId) {
        List<Detalle> detalles = detalleRepository.findByVentaId(ventaId);
        for (Detalle d : detalles) {
            try {
                var producto = productoClient.getProductoById(d.getProductoId());
                d.setNombreProducto(producto.get("nombre").toString());
            } catch (Exception e) {
                d.setNombreProducto("Desconocido");
            }
        }
        return detalles;
    }

    public Venta actualizarVenta(Long id, Venta datos) {
    return ventaRepository.findById(id).map(v -> {
        v.setFechaVenta(datos.getFechaVenta());
        v.setUsuarioId(datos.getUsuarioId());
        v.setDireccionId(datos.getDireccionId());

        // Primero borramos detalles antiguos
        detalleRepository.deleteAllByVentaId(v.getId());

        int total = 0;
        // Guardamos los detalles nuevos recalculando subtotal
        for (Detalle nuevoDetalle : datos.getDetalles()) {
            // Obtener el producto con WebClient
            var producto = productoClient.getProductoById(nuevoDetalle.getProductoId());
            if (producto == null || producto.isEmpty()) {
                throw new RuntimeException("Producto no válido con ID: " + nuevoDetalle.getProductoId());
            }
            int precio = Integer.parseInt(producto.get("precio").toString());

            // Calcular subtotal
            int subtotal = nuevoDetalle.getCantidad() * precio;
            nuevoDetalle.setSubtotal(subtotal);

            total += subtotal;

            // Asignar venta para la relación
            nuevoDetalle.setVenta(v);

            detalleRepository.save(nuevoDetalle);
        }

        v.setTotal(total);

        Venta ventaActualizada = ventaRepository.save(v);

        // Cargar datos externos para nombres, etc.
        cargarDatosExternos(ventaActualizada);

        return ventaActualizada;
    }).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
}


    
    public void eliminarVenta(Long id) {
        Optional<Venta> ventaOptional = ventaRepository.findById(id);
        if (ventaOptional.isPresent()) {
            detalleRepository.deleteAllByVentaId(id);
            ventaRepository.deleteById(id);
        } else {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
    }

    private void cargarDatosExternos(Venta venta) {
        try {
            var usuario = usuarioClient.getUsuarioById(venta.getUsuarioId());
            venta.setNombreUsuario(usuario.get("nombre").toString());
        } catch (Exception e) {
            venta.setNombreUsuario("Desconocido");
        }

        try {
            var direccion = direccionClient.getDireccionById(venta.getDireccionId());
            venta.setDireccionCompleta(direccion.get("calle") + " " + direccion.get("numeracion"));
        } catch (Exception e) {
            venta.setDireccionCompleta("Desconocida");
        }

        if (venta.getDetalles() != null) {
            for (Detalle detalle : venta.getDetalles()) {
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
    }
}