package com.example.Gestion_de_ventas.Service;

import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Repository.DetalleRepository;
import com.example.Gestion_de_ventas.Repository.VentaRepository;
import com.example.Gestion_de_ventas.WebClient.ProductoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DetalleServiceTest {

    @InjectMocks
    private DetalleService detalleService;

    @Mock
    private DetalleRepository detalleRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoClient productoClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerTodos() {
        Detalle detalle = new Detalle(1L, 2, 3000, 1L, new Venta(), null);
        when(detalleRepository.findAll()).thenReturn(List.of(detalle));
        when(productoClient.getProductoById(1L)).thenReturn(Map.of("nombre", "Producto A"));

        List<Detalle> result = detalleService.obtenerTodos();
        assertEquals(1, result.size());
        assertEquals("Producto A", result.get(0).getNombreProducto());
    }

    @Test
    void testObtenerPorId() {
        Detalle detalle = new Detalle(1L, 2, 3000, 1L, new Venta(), null);
        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detalle));
        when(productoClient.getProductoById(1L)).thenReturn(Map.of("nombre", "Producto A"));

        Optional<Detalle> result = detalleService.obtenerPorId(1L);
        assertTrue(result.isPresent());
        assertEquals("Producto A", result.get().getNombreProducto());
    }

    @Test
    void testCrearDetalleOk() {
        Venta venta = new Venta();
        venta.setId(1L);
        Detalle detalle = new Detalle(null, 1, 1000, 1L, venta, null);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(productoClient.getProductoById(1L)).thenReturn(Map.of("nombre", "Producto X"));
        when(detalleRepository.save(detalle)).thenReturn(detalle);

        Detalle creado = detalleService.crearDetalle(detalle);
        assertNotNull(creado);
    }

    @Test
    void testActualizarDetalleOk() {
        Detalle existente = new Detalle(1L, 1, 1000, 1L, new Venta(), null);
        Detalle actualizado = new Detalle(null, 2, 2000, 1L, null, null);

        when(detalleRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoClient.getProductoById(1L)).thenReturn(Map.of("nombre", "Producto X"));
        when(detalleRepository.save(any())).thenReturn(existente);

        Detalle result = detalleService.actualizarDetalle(1L, actualizado);
        assertEquals(2, result.getCantidad());
        assertEquals(2000, result.getSubtotal());
    }

    @Test
    void testEliminarDetalle() {
        detalleService.eliminarDetalle(1L);
        verify(detalleRepository, times(1)).deleteById(1L);
    }
}