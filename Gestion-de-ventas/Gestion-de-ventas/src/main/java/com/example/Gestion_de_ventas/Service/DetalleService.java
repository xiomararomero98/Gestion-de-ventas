package com.example.Gestion_de_ventas.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Repository.DetalleRepository;
import com.example.Gestion_de_ventas.Repository.VentaRepository;
import com.example.Gestion_de_ventas.WebClient.DireccionClient;
import com.example.Gestion_de_ventas.WebClient.ProductoClient;
import com.example.Gestion_de_ventas.WebClient.UsuarioClient;

@Service
public class DetalleService {

    @Autowired
    private DetalleRepository detalleRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private DireccionClient direccionClient;



    @Autowired
    private ProductoClient productoClient;

    public List<Detalle> obtenerTodosLosDetalles() {
        return detalleRepository.findAll();
    }

    public Optional<Detalle> obtenerDetallePorId(Long id) {
        return detalleRepository.findById(id);
    }
    public Detalle crearDetalle(Detalle nuevoDetalle) {
    // Validar producto por WebClient
    var producto = productoClient.getProductoById(nuevoDetalle.getProductoId());
    if (producto == null || producto.isEmpty()) {
        throw new RuntimeException("Producto no encontrado con ID: " + nuevoDetalle.getProductoId());
    }

    // Cargar la venta completa desde la BD
    Long idVenta = nuevoDetalle.getVenta().getId();
    Venta ventaCompleta = ventaRepository.findById(idVenta)
        .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + idVenta));

    // Asociar la venta completa al detalle
    nuevoDetalle.setVenta(ventaCompleta);

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

     public List<Detalle> obtenerDetallesConVentaCompleta() {
        List<Detalle> detalles = detalleRepository.findAll();

        for (Detalle d : detalles) {
            Venta venta = d.getVenta();
            if (venta != null) {
                // Obtener nombre del usuario
                var usuario = usuarioClient.getUsuarioById(venta.getUsuarioId());
                if (usuario != null && usuario.containsKey("nombre")) {
                    venta.setNombreUsuario(usuario.get("nombre").toString());
                } else {
                    venta.setNombreUsuario("Desconocido");
                }

                // Obtener dirección completa
                var direccion = direccionClient.getDireccionById(venta.getDireccionId());
                if (direccion != null && direccion.containsKey("calle")) {
                    String calle = direccion.get("calle").toString();
                    String numero = direccion.get("numeracion") != null ? direccion.get("numeracion").toString() : "";
                    venta.setDireccionCompleta(calle + " " + numero);
                } else {
                    venta.setDireccionCompleta("Desconocida");
                }

                d.setVenta(venta); // Asegura que la venta se actualice en el objeto Detalle
            }
        }

        return detalles;
    }
}
