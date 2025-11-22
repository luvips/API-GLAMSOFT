package org.pi.dto;

import java.time.LocalTime;

public class HorarioDTO {
    private int idHorario;
    private String dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstilistaDTO estilista;

    // Constructor para la consulta de estilista
    public HorarioDTO(String dia, LocalTime horaInicio, LocalTime horaFin) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
    
    public HorarioDTO() {}

    // Getters y Setters
    public int getIdHorario() { return idHorario; }
    public void setIdHorario(int idHorario) { this.idHorario = idHorario; }
    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public EstilistaDTO getEstilista() { return estilista; }
    public void setEstilista(EstilistaDTO estilista) { this.estilista = estilista; }

    // DTO anidado para la información del estilista
    public static class EstilistaDTO {
        private int idEstilista;
        private String nombre;
        
        public int getIdEstilista() { return idEstilista; }
        public void setIdEstilista(int idEstilista) { this.idEstilista = idEstilista; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }
}
