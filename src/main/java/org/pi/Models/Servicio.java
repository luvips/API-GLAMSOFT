package org.pi.Models;

public class Servicio {
    private int idServicio;
    private String imagenURL;
    private String nombreServicio;
    private int duracionMinutos;
    private double precio;
    private String descripcion;
    private int idCategoria;
    private Integer idFormulario; // Cambiado a Integer para permitir nulos
    private boolean activo;

    public Servicio() {
    }

    public Servicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public Servicio(String imagenURL, String nombreServicio, int duracionMinutos, double precio, String descripcion) {
        this.imagenURL = imagenURL;
        this.nombreServicio = nombreServicio;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public Servicio(int idServicio, String imagenURL,
                    String nombreServicio, int duracionMinutos, double precio, String descripcion, int idCategoria, Integer idFormulario, boolean activo) {
        this.idServicio = idServicio;
        this.imagenURL = imagenURL;
        this.nombreServicio = nombreServicio;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.idFormulario = idFormulario;
        this.activo = activo;
    }

    public Servicio(int idServicio, String nombreServicio, String descripcion) {
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.descripcion = descripcion;
    }

    public Servicio(String nombreServicio, double precio, String descripcion) {
        this.nombreServicio = nombreServicio;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Integer getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(Integer idFormulario) {
        this.idFormulario = idFormulario;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
