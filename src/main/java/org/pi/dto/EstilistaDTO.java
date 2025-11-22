package org.pi.dto;

import java.util.List;

public class EstilistaDTO {
    private int idEstilista;
    private String nombre;
    private String especialidad;
    private String telefono;
    private String email;
    private boolean activo;
    private double valoracionPromedio;
    private int totalValoraciones;
    private List<HorarioDTO> horarios;

    // Getters y Setters
    public int getIdEstilista() { return idEstilista; }
    public void setIdEstilista(int idEstilista) { this.idEstilista = idEstilista; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public double getValoracionPromedio() { return valoracionPromedio; }
    public void setValoracionPromedio(double valoracionPromedio) { this.valoracionPromedio = valoracionPromedio; }
    public int getTotalValoraciones() { return totalValoraciones; }
    public void setTotalValoraciones(int totalValoraciones) { this.totalValoraciones = totalValoraciones; }
    public List<HorarioDTO> getHorarios() { return horarios; }
    public void setHorarios(List<HorarioDTO> horarios) { this.horarios = horarios; }
}
