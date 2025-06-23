package com.example.Gestion_de_ventas.Controller;


import com.example.Gestion_de_ventas.Model.Detalle;
import com.example.Gestion_de_ventas.Service.DetalleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DetalleController.class)
public class DetalleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DetalleService detalleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAll() throws Exception {
        Detalle detalle = new Detalle();
        when(detalleService.obtenerTodos()).thenReturn(List.of(detalle));

        mockMvc.perform(get("/api/v1/detalles"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByIdFound() throws Exception {
        Detalle detalle = new Detalle();
        when(detalleService.obtenerPorId(1L)).thenReturn(Optional.of(detalle));

        mockMvc.perform(get("/api/v1/detalles/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(detalleService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/detalles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearDetalleOk() throws Exception {
        Detalle detalle = new Detalle();
        when(detalleService.crearDetalle(any())).thenReturn(detalle);

        mockMvc.perform(post("/api/v1/detalles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isCreated());
    }

    @Test
    void testActualizarDetalle() throws Exception {
        Detalle detalle = new Detalle();
        when(detalleService.actualizarDetalle(eq(1L), any())).thenReturn(detalle);

        mockMvc.perform(put("/api/v1/detalles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalle)))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarDetalle() throws Exception {
        mockMvc.perform(delete("/api/v1/detalles/1"))
                .andExpect(status().isNoContent());

        verify(detalleService, times(1)).eliminarDetalle(1L);
    }
}