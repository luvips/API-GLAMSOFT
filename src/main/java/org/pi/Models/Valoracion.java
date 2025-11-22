package org.pi.Models;

import java.math.BigDecimal;

public class Valoracion {
    private int idValoracion;
    private BigDecimal puntuacion;
    private String comentario;
    private int idCita;
    private int  idCliente;
    private int idServicio;

    public Valoracion() {
    }

    public Valoracion(int idValoracion, BigDecimal puntuacion, String comentario, int idCita, int idCliente, int idServicio) {
        this.idValoracion = idValoracion;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.idCita = idCita;
        this.idCliente = idCliente;
        this.idServicio = idServicio;
    }

    public int getIdValoracion() {
        return idValoracion;
    }

    public void setIdValoracion(int idValoracion) {
        this.idValoracion = idValoracion;
    }

    public BigDecimal getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(BigDecimal puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }
}
