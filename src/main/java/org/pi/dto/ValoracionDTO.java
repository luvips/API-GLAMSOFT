package org.pi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ValoracionDTO {
    private int idValoracion;
    private BigDecimal calificacion;
    private String comentario;
    private LocalDateTime fecha;
    private ClienteDTO cliente;
    private ServicioDTO servicio;

    // Getters y Setters
    public int getIdValoracion() { return idValoracion; }
    public void setIdValoracion(int idValoracion) { this.idValoracion = idValoracion; }
    public BigDecimal getCalificacion() { return calificacion; }
    public void setCalificacion(BigDecimal calificacion) { this.calificacion = calificacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public ClienteDTO getCliente() { return cliente; }
    public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }
    public ServicioDTO getServicio() { return servicio; }
    public void setServicio(ServicioDTO servicio) { this.servicio = servicio; }

    // Clases anidadas para la estructura JSON
    public static class ClienteDTO {
        private int idCliente;
        private String nombre;
        public int getIdCliente() { return idCliente; }
        public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    public static class ServicioDTO {
        private int idServicio;
        private String nombre;
        public int getIdServicio() { return idServicio; }
        public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }
}
