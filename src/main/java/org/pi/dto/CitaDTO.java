package org.pi.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDTO {
    // Campos principales de la cita
    private int idCita;
    private LocalDateTime fechaHoraCita;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaFinalizacion;
    private String estadoCita;
    private String notas;
    private double precioTotal;

    // Datos del Cliente
    private int idCliente;
    private String nombreCliente;
    private String telefonoCliente;

    // Datos del Estilista
    private int idEstilista;
    private String nombreEstilista;
    private String especialidadEstilista;

    // Lista de Servicios
    private List<ServicioDTO> servicios = new ArrayList<>();

    // Getters y Setters
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }
    public LocalDateTime getFechaHoraCita() { return fechaHoraCita; }
    public void setFechaHoraCita(LocalDateTime fechaHoraCita) { this.fechaHoraCita = fechaHoraCita; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public LocalDateTime getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(LocalDateTime fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }
    public String getEstadoCita() { return estadoCita; }
    public void setEstadoCita(String estadoCita) { this.estadoCita = estadoCita; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }
    public int getIdEstilista() { return idEstilista; }
    public void setIdEstilista(int idEstilista) { this.idEstilista = idEstilista; }
    public String getNombreEstilista() { return nombreEstilista; }
    public void setNombreEstilista(String nombreEstilista) { this.nombreEstilista = nombreEstilista; }
    public String getEspecialidadEstilista() { return especialidadEstilista; }
    public void setEspecialidadEstilista(String especialidadEstilista) { this.especialidadEstilista = especialidadEstilista; }
    public List<ServicioDTO> getServicios() { return servicios; }
    public void setServicios(List<ServicioDTO> servicios) { this.servicios = servicios; }

    public void addServicio(int id, String nombre, double precio, int duracion) {
        this.servicios.add(new ServicioDTO(id, nombre, precio, duracion));
        this.precioTotal += precio; // Auto-calcular precio total
    }

    // Clase interna para representar los servicios dentro de la cita
    public static class ServicioDTO {
        private int idServicio;
        private String nombre;
        private double precio;
        private int duracion;

        public ServicioDTO(int idServicio, String nombre, double precio, int duracion) {
            this.idServicio = idServicio;
            this.nombre = nombre;
            this.precio = precio;
            this.duracion = duracion;
        }

        public int getIdServicio() { return idServicio; }
        public String getNombre() { return nombre; }
        public double getPrecio() { return precio; }
        public int getDuracion() { return duracion; }
    }
}
