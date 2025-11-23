package org.pi.dto;

public class ServicioDTO {
    private int idServicio;
    private String nombre;
    private String descripcion;
    private double precio;
    private int duracion;
    private String categoria;
    private boolean activo;
    private String imagenURL; // Campo añadido
    private double valoracionPromedio;
    private int totalValoraciones;

    // Getters y Setters
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getImagenURL() { return imagenURL; } // Getter añadido
    public void setImagenURL(String imagenURL) { this.imagenURL = imagenURL; } // Setter añadido
    public double getValoracionPromedio() { return valoracionPromedio; }
    public void setValoracionPromedio(double valoracionPromedio) { this.valoracionPromedio = valoracionPromedio; }
    public int getTotalValoraciones() { return totalValoraciones; }
    public void setTotalValoraciones(int totalValoraciones) { this.totalValoraciones = totalValoraciones; }
}
