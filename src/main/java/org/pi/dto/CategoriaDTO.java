package org.pi.dto;

import java.util.List;

public class CategoriaDTO {
    private int idCategoria;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private List<ServicioSimpleDTO> servicios;

    // Getters y Setters
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public List<ServicioSimpleDTO> getServicios() { return servicios; }
    public void setServicios(List<ServicioSimpleDTO> servicios) { this.servicios = servicios; }

    // DTO anidado para la lista de servicios
    public static class ServicioSimpleDTO {
        private int idServicio;
        private String nombre;
        private double precio;

        public ServicioSimpleDTO(int idServicio, String nombre, double precio) {
            this.idServicio = idServicio;
            this.nombre = nombre;
            this.precio = precio;
        }

        public int getIdServicio() { return idServicio; }
        public String getNombre() { return nombre; }
        public double getPrecio() { return precio; }
    }
}
