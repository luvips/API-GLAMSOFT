package org.pi.Services;

import org.pi.Models.Empleado;
import org.pi.Repositories.EmpleadoRepository;

import java.sql.SQLException;
import java.util.List;

public class EmpleadoService {
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<Empleado> findAll() throws SQLException {
        return empleadoRepository.findAll();
    }

    public Empleado findById(int id) throws SQLException {
        return empleadoRepository.findById(id);
    }

    public Empleado create(Empleado empleado) throws SQLException {
        // Aquí se podrían añadir más validaciones de negocio
        return empleadoRepository.save(empleado);
    }

    public boolean update(int id, Empleado empleado) throws SQLException {
        empleado.setIdEmpleado(id);
        return empleadoRepository.update(empleado);
    }

    public boolean delete(int id) throws SQLException {
        return empleadoRepository.softDelete(id);
    }
}
