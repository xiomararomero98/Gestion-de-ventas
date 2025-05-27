package com.example.Gestion_de_ventas.Model;

public class DetalleConNombreProducto {
    private Long id;
    private int cantidad;
    private int subtotal;
    private Long productoId;
    private String nombreProducto;

    public DetalleConNombreProducto(Long id, int cantidad, int subtotal, Long productoId, String nombreProducto) {
        this.id = id;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
}
