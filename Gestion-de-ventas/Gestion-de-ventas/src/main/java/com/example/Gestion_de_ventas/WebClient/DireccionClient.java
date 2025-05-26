package com.example.Gestion_de_ventas.WebClient;

import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Component
public class DireccionClient {

    private final WebClient webClient;

    public DireccionClient(@Value("${direccion-service.url}") String direccionServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(direccionServiceUrl).build();
    }

    public Map<String, Object> getDireccionById(Long id) {
        return this.webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Dirección no encontrada")))
                .bodyToMono(Map.class)
                .block();
    }
}

