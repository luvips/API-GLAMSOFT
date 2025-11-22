package org.pi.Services;

import org.pi.Models.Empleado;
import org.pi.Repositories.EmpleadoRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class EmpleadoService {
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<Empleado> findAll() throws SQLException {
        return empleadoRepository.findAll();
    }

    public Empleado findById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del empleado debe ser mayor a cero");
        }
        Empleado empleado = empleadoRepository.findById(id);
        if (empleado == null) {
            // En lugar de lanzar excepción, devolvemos null para que el controlador gestione el 404
            return null;
        }
        return empleado;
    }

    public Empleado create(Empleado empleado) throws SQLException, IllegalArgumentException {
        // Aquí se podrían añadir más validaciones de negocio
        if (empleado.getEmail() == null || empleado.getEmail().isEmpty() || !empleado.getEmail().contains("@")) {
            throw new IllegalArgumentException("El email del empleado no es válido.");
        }
        if (empleado.getPassword() == null || empleado.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        // Se asume que el idRol viene y es válido
        return empleadoRepository.save(empleado);
    }

    public boolean update(Empleado empleado) throws SQLException, IllegalArgumentException {
        if (empleado.getIdEmpleado() <= 0) {
            throw new IllegalArgumentException("El ID del empleado es inválido para la actualización.");
        }
        return empleadoRepository.update(empleado);
    }

    public boolean delete(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del empleado debe ser mayor a cero.");
        }
        return empleadoRepository.delete(id);
    }
}
