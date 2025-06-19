package com.example.Gestion_de_ventas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Gestion_de_ventas.Model.Venta;

public interface VentaRepository extends JpaRepository<Venta,Long>{
     List<Venta> findByUsuarioId(Long usuarioId);

}