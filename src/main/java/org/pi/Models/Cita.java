package org.pi.Models;
import java.time.LocalDateTime;
import java.util.List;

public class Cita {
    private int idCita;
    private String estadoCita;
    private LocalDateTime fechaHoraCita;
    private LocalDateTime fechaSolicitudCita;
    private String notas;
    private int idCliente;
    private int idEstilista;
    // idHorario ha sido eliminado
    private List<Integer> servicios;

    //CONSTRUCTORES
    public Cita() {
    }

    public Cita(int idCita, String estadoCita, LocalDateTime fechaHoraCita,
                LocalDateTime fechaSolicitudCita, String notas, int idCliente, int idEstilista, List<Integer> servicios) {
        this.idCita = idCita;
        this.estadoCita = estadoCita;
        this.fechaHoraCita = fechaHoraCita;
        this.fechaSolicitudCita = fechaSolicitudCita;
        this.notas = notas;
        this.idCliente = idCliente;
        this.idEstilista = idEstilista;
        this.servicios = servicios;
    }

    //GETTERS AND SETTERS

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public String getEstadoCita() {
        return estadoCita;
    }

    public void setEstadoCita(String estadoCita) {
        this.estadoCita = estadoCita;
    }

    public LocalDateTime getFechaHoraCita() {
        return fechaHoraCita;
    }

    public void setFechaHoraCita(LocalDateTime fechaHoraCita) {
        this.fechaHoraCita = fechaHoraCita;
    }

    public LocalDateTime getFechaSolicitudCita() {
        return fechaSolicitudCita;
    }

    public void setFechaSolicitudCita(LocalDateTime fechaSolicitudCita) {
        this.fechaSolicitudCita = fechaSolicitudCita;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdEstilista() {
        return idEstilista;
    }

    public void setIdEstilista(int idEstilista) {
        this.idEstilista = idEstilista;
    }

    public List<Integer> getServicios() {
        return servicios;
    }

    public void setServicios(List<Integer> servicios) {
        this.servicios = servicios;
    }
}
