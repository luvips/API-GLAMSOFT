package org.pi.dto;

import java.time.LocalDateTime;

public class PortafolioDTO {
    private int idPortafolio;
    private String titulo;
    private String descripcion;
    private String urlImagen;
    private String categoria;
    private LocalDateTime fecha;
    private boolean destacado;
    private int visitas;

    // Getters y Setters
    public int getIdPortafolio() { return idPortafolio; }
    public void setIdPortafolio(int idPortafolio) { this.idPortafolio = idPortafolio; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public boolean isDestacado() { return destacado; }
    public void setDestacado(boolean destacado) { this.destacado = destacado; }
    public int getVisitas() { return visitas; }
    public void setVisitas(int visitas) { this.visitas = visitas; }
}
