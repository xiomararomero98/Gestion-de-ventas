package com.example.Gestion_de_ventas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Gestion_de_ventas.Model.Detalle;


public interface DetalleRepository extends JpaRepository<Detalle,Long>{
    List<Detalle> findByVentaId(Long ventaId);
}


