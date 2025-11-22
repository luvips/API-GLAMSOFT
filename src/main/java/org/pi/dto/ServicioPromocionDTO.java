package org.pi.dto;

import java.math.BigDecimal;

public class ServicioPromocionDTO {
    private int idServicio;
    private String nombre;
    private BigDecimal precioOriginal;
    private BigDecimal precioConDescuento;
    private BigDecimal descuento;

    // Getters y Setters
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecioOriginal() { return precioOriginal; }
    public void setPrecioOriginal(BigDecimal precioOriginal) { this.precioOriginal = precioOriginal; }
    public BigDecimal getPrecioConDescuento() { return precioConDescuento; }
    public void setPrecioConDescuento(BigDecimal precioConDescuento) { this.precioConDescuento = precioConDescuento; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
}
