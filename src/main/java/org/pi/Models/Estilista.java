package org.pi.Models;

public class Estilista extends Empleado {
    private int idHorario;

    public Estilista() {
        super();
    }

    public Estilista(int idUsuario, String nombre, String email, String telefono, String password, int idRol, boolean activo, int idEmpleado, String puesto, String imagenPerfil, int idHorario) {
        super(idUsuario, nombre, email, telefono, password, idRol, activo, idEmpleado, puesto, imagenPerfil);
        this.idHorario = idHorario;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }
}
