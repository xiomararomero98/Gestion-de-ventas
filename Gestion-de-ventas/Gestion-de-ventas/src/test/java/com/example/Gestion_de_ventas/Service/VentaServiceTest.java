package com.example.Gestion_de_ventas.Service;
import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Repository.DetalleRepository;
import com.example.Gestion_de_ventas.Repository.VentaRepository;
import com.example.Gestion_de_ventas.WebClient.DireccionClient;
import com.example.Gestion_de_ventas.WebClient.ProductoClient;
import com.example.Gestion_de_ventas.WebClient.UsuarioClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VentaServiceTest {

    @InjectMocks
    private VentaService ventaService;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleRepository detalleRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private DireccionClient direccionClient;

    @Mock
    private ProductoClient productoClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearVenta_deberiaGuardarVentaYDetalles() {
        Venta venta = new Venta();
        venta.setUsuarioId(1L);
        venta.setDireccionId(1L);
        Detalle detalle = new Detalle();
        detalle.setCantidad(1);
        detalle.setProductoId(1L);
        venta.setDetalles(List.of(detalle));

        when(usuarioClient.getUsuarioById(1L)).thenReturn(Map.of("nombre", "Juan"));
        when(direccionClient.getDireccionById(1L)).thenReturn(Map.of("calle", "Av", "numeracion", "123"));
        when(productoClient.getProductoById(1L)).thenReturn(Map.of("precio", "10000"));

        when(ventaRepository.save(any())).thenAnswer(invocation -> {
            Venta v = invocation.getArgument(0);
            v.setId(1L);
            return v;
        });

        when(detalleRepository.findByVentaId(1L)).thenReturn(List.of());

        Venta creada = ventaService.crearVenta(venta);

        assertEquals(10000, creada.getTotal());
        verify(detalleRepository, times(1)).save(any());
    }

    @Test
    void obtenerTodasLasVentas_deberiaDevolverLista() {
        Venta venta = new Venta();
        venta.setUsuarioId(1L);
        venta.setDireccionId(1L);

        when(ventaRepository.findAll()).thenReturn(List.of(venta));
        when(usuarioClient.getUsuarioById(1L)).thenReturn(Map.of("nombre", "Juan"));
        when(direccionClient.getDireccionById(1L)).thenReturn(Map.of("calle", "Av", "numeracion", "123"));

        List<Venta> resultado = ventaService.obtenerTodasLasVentas();

        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombreUsuario());
    }

    @Test
    void obtenerVentaPorId_existente() {
        Venta venta = new Venta();
        venta.setUsuarioId(1L);
        venta.setDireccionId(1L);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(usuarioClient.getUsuarioById(1L)).thenReturn(Map.of("nombre", "Juan"));
        when(direccionClient.getDireccionById(1L)).thenReturn(Map.of("calle", "Av", "numeracion", "123"));

        Optional<Venta> resultado = ventaService.obtenerVentaPorId(1L);
        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombreUsuario());
    }

    @Test
    void obtenerVentasPorUsuario_deberiaRetornarVentas() {
        Venta venta = new Venta();
        venta.setUsuarioId(1L);
        venta.setDireccionId(1L);

        when(ventaRepository.findByUsuarioId(1L)).thenReturn(List.of(venta));
        when(usuarioClient.getUsuarioById(1L)).thenReturn(Map.of("nombre", "Juan"));
        when(direccionClient.getDireccionById(1L)).thenReturn(Map.of("calle", "Av", "numeracion", "123"));

        List<Venta> ventas = ventaService.obtenerVentasPorUsuario(1L);
        assertFalse(ventas.isEmpty());
    }

    @Test
    void obtenerProductosPorVenta_deberiaRetornarDetalles() {
        Detalle detalle = new Detalle();
        detalle.setProductoId(1L);

        when(detalleRepository.findByVentaId(1L)).thenReturn(List.of(detalle));
        when(productoClient.getProductoById(1L)).thenReturn(Map.of("nombre", "Perfume A"));

        List<Detalle> detalles = ventaService.obtenerProductosPorVenta(1L);
        assertEquals("Perfume A", detalles.get(0).getNombreProducto());
    }

    @Test
    void actualizarVenta_existente() {
        Venta existente = new Venta();
        existente.setId(1L);

        Venta datos = new Venta();
        datos.setUsuarioId(1L);
        datos.setDireccionId(1L);
        Detalle detalle = new Detalle();
        detalle.setCantidad(1);
        detalle.setProductoId(1L);
        datos.setDetalles(List.of(detalle));

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoClient.getProductoById(1L)).thenReturn(Map.of("precio", "10000"));
        when(ventaRepository.save(any())).thenReturn(existente);

        Venta actualizada = ventaService.actualizarVenta(1L, datos);
        assertEquals(10000, actualizada.getTotal());
    }

    @Test
    void eliminarVenta_existente() {
        Venta venta = new Venta();
        venta.setId(1L);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertDoesNotThrow(() -> ventaService.eliminarVenta(1L));
        verify(detalleRepository).deleteAllByVentaId(1L);
        verify(ventaRepository).deleteById(1L);
    }

    @Test
    void eliminarVenta_inexistente() {
        when(ventaRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> ventaService.eliminarVenta(2L));
        assertTrue(ex.getMessage().contains("Venta no encontrada"));
    }
}