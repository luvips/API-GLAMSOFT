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

    // ✅ Obtener todos los empleados
    public List<Empleado> getAll() throws SQLException {
        return empleadoRepository.findAll();
    }

    // ✅ Obtener por ID
    public Empleado getById(int id) throws SQLException {
        return empleadoRepository.findById(id);
    }

    // ✅ Obtener por Rol (Ej: solo estilistas)
    public List<Empleado> getByRol(int idRol) throws SQLException {
        return empleadoRepository.findByRol(idRol);
    }

    // ✅ Crear nuevo empleado
    public Empleado create(Empleado empleado) throws SQLException {
        return empleadoRepository.save(empleado);
    }

    // ✅ Actualizar empleado
    public boolean update(Empleado empleado) throws SQLException {
        return empleadoRepository.update(empleado);
    }

    // ✅ Eliminar empleado
    public boolean delete(int id) throws SQLException {
        return empleadoRepository.delete(id);
    }
    public Empleado findById(int id) throws SQLException {
        return empleadoRepository.findById(id);
    }

}