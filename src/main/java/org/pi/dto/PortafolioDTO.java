package org.pi.dto;

import java.time.LocalDateTime;

public class PortafolioDTO {
    // CAMBIO: De idPortafolio a idImagen
    private int idImagen;
    private String titulo;
    private String descripcion;
    private String urlImagen;
    private String categoria;
    private LocalDateTime fecha;
    private boolean destacado;
    private int visitas;

    // Getters y Setters actualizados
    public int getIdImagen() { return idImagen; } // Renombrado
    public void setIdImagen(int idImagen) { this.idImagen = idImagen; } // Renombrado

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