package com.example.Gestion_de_ventas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.Gestion_de_ventas.Model.Detalle;

import jakarta.transaction.Transactional;


public interface DetalleRepository extends JpaRepository<Detalle,Long>{
    List<Detalle> findByVentaId(Long ventaId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Detalle d WHERE d.venta.id = :ventaId")
    void deleteAllByVentaId(Long ventaId);

}


