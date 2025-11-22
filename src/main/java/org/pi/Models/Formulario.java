package org.pi.Models;

public class Formulario {
    private int idFormulario;
    private String nombreFormulario;
    private String descripcion;
    private boolean activo;

    public Formulario() {
    }

    public Formulario(int idFormulario, String nombreFormulario) {
        this.idFormulario = idFormulario;
        this.nombreFormulario = nombreFormulario;
    }

    public Formulario(int idFormulario, String nombreFormulario, String descripcion, boolean activo) {
        this.idFormulario = idFormulario;
        this.nombreFormulario = nombreFormulario;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public int getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(int idFormulario) {
        this.idFormulario = idFormulario;
    }

    public String getNombreFormulario() {
        return nombreFormulario;
    }

    public void setNombreFormulario(String nombreFormulario) {
        this.nombreFormulario = nombreFormulario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
