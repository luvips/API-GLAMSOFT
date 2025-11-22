package org.pi.dto;

import java.sql.Timestamp;

public class EmpleadoDTO {
    private int idEmpleado;
    private String nombre;
    private String puesto;
    private String telefono;
    private String email;
    private Timestamp fechaContratacion;
    private boolean activo;

    // Getters y Setters
    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Timestamp getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(Timestamp fechaContratacion) { this.fechaContratacion = fechaContratacion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
