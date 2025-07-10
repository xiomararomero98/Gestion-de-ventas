package com.example.Gestion_de_ventas.Controller;

import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Model.Venta;
import com.example.Gestion_de_ventas.Service.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentaController.class)
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCrearVenta() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setUsuarioId(2L);
        venta.setDireccionId(3L);
        venta.setTotal(10000);
        venta.setFechaVenta(new Date());

        Mockito.when(ventaService.crearVenta(Mockito.any(Venta.class))).thenReturn(venta);

        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetAllVentas() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setUsuarioId(2L);
        venta.setDireccionId(3L);
        venta.setTotal(10000);
        venta.setFechaVenta(new Date());

        Mockito.when(ventaService.obtenerTodasLasVentas()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetVentaPorId() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);

        Mockito.when(ventaService.obtenerVentaPorId(1L)).thenReturn(Optional.of(venta));

        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetVentaPorUsuario() throws Exception {
        Venta venta = new Venta();
        venta.setUsuarioId(2L);

        Mockito.when(ventaService.obtenerVentasPorUsuario(2L)).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/v1/ventas/usuario/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(2L));
    }

    @Test
    void testGetProductosPorVenta() throws Exception {
        Detalle detalle = new Detalle();
        detalle.setId(1L);
        detalle.setProductoId(5L);
        detalle.setCantidad(2);
        detalle.setSubtotal(2000);

        Mockito.when(ventaService.obtenerProductosPorVenta(1L)).thenReturn(List.of(detalle));

        mockMvc.perform(get("/api/v1/ventas/1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testActualizarVenta() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setUsuarioId(2L);
        venta.setDireccionId(3L);
        venta.setTotal(9999);

        Mockito.when(ventaService.actualizarVenta(Mockito.eq(1L), Mockito.any(Venta.class))).thenReturn(venta);

        mockMvc.perform(put("/api/v1/ventas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(9999));
    }

    @Test
    void testEliminarVenta() throws Exception {
        mockMvc.perform(delete("/api/v1/ventas/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(ventaService).eliminarVenta(1L);
    }

    @Test
    void testActualizarVentaNotFound() throws Exception {
    Venta venta = new Venta();
    venta.setTotal(5000);

    Mockito.when(ventaService.actualizarVenta(Mockito.eq(999L), Mockito.any(Venta.class)))
           .thenThrow(new RuntimeException("Venta no encontrada"));

    mockMvc.perform(put("/api/v1/ventas/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(venta)))
            .andExpect(status().isNotFound());
}
}