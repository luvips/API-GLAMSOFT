package org.pi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PromocionDTO {
    private int idPromocion;
    private String nombre;
    private String descripcion;
    private BigDecimal porcentajeDescuento; // Usamos BigDecimal para precisión
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa;
    private List<ServicioPromocionDTO> servicios;

    // Getters y Setters
    public int getIdPromocion() { return idPromocion; }
    public void setIdPromocion(int idPromocion) { this.idPromocion = idPromocion; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public List<ServicioPromocionDTO> getServicios() { return servicios; }
    public void setServicios(List<ServicioPromocionDTO> servicios) { this.servicios = servicios; }
}
