package org.pi.Models;

public class Empleado extends Usuario {
    private int idEmpleado;
    private String puesto;
    private String imagenPerfil;
    // Los campos nombre, telefono, email, etc., se heredan de Usuario.

    public Empleado() {
        super();
    }

    public Empleado(int idUsuario, String nombre, String email, String telefono, String password, int idRol, boolean activo, int idEmpleado, String puesto, String imagenPerfil) {
        super(idUsuario, nombre, email, telefono, password, idRol, activo);
        this.idEmpleado = idEmpleado;
        this.puesto = puesto;
        this.imagenPerfil = imagenPerfil;
    }

    // Getters y Setters para los campos específicos de Empleado
    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }
}
