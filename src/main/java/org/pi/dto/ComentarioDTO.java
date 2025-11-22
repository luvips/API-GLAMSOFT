package org.pi.dto;

import java.time.LocalDateTime;

public class ComentarioDTO {
    private int idComentario;
    private String contenido;
    private LocalDateTime fecha;
    private ClienteDTO cliente;
    private CitaDTO cita;

    // Getters y Setters
    public int getIdComentario() { return idComentario; }
    public void setIdComentario(int idComentario) { this.idComentario = idComentario; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public ClienteDTO getCliente() { return cliente; }
    public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }
    public CitaDTO getCita() { return cita; }
    public void setCita(CitaDTO cita) { this.cita = cita; }

    // Clases anidadas para la estructura JSON
    public static class ClienteDTO {
        private int idCliente;
        private String nombre;
        public int getIdCliente() { return idCliente; }
        public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    public static class CitaDTO {
        private int idCita;
        private String servicio;
        public int getIdCita() { return idCita; }
        public void setIdCita(int idCita) { this.idCita = idCita; }
        public String getServicio() { return servicio; }
        public void setServicio(String servicio) { this.servicio = servicio; }
    }
}
