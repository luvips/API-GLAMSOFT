package org.pi.Models;

import java.time.LocalDateTime;

public class Portafolio {
    private int idImagen;
    private String titulo;
    private String url;
    private String descripcion;
    private LocalDateTime fechaSubida;
    private int idEstilista;
    private Integer idCategoria;
    private boolean destacado;


    public Portafolio() {
    }

    public Portafolio(int idImagen, String titulo, String url, String descripcion, LocalDateTime fechaSubida, int idEstilista) {
        this.idImagen = idImagen;
        this.titulo = titulo;
        this.url = url;
        this.descripcion = descripcion;
        this.fechaSubida = fechaSubida;
        this.idEstilista = idEstilista;
    }

    public int getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(int idImagen) {
        this.idImagen = idImagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public int getIdEstilista() {
        return idEstilista;
    }

    public void setIdEstilista(int idEstilista) {
        this.idEstilista = idEstilista;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public boolean isDestacado() {
        return destacado;
    }

    public void setDestacado(boolean destacado) {
        this.destacado = destacado;
    }
}
