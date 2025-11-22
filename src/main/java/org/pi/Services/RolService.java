package org.pi.Services;

import org.pi.Models.Rol;
import org.pi.Repositories.RolRepository;

import java.sql.SQLException;
import java.util.List;

public class RolService {
    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> findAll() throws SQLException {
        return rolRepository.findAll();
    }

    public Rol findById(int id) throws SQLException {
        return rolRepository.findById(id);
    }

    public Rol create(Rol rol) throws SQLException {
        return rolRepository.save(rol);
    }

    public boolean update(int id, Rol rol) throws SQLException {
        rol.setIdRol(id);
        return rolRepository.update(rol);
    }

    public boolean delete(int id) throws SQLException {
        return rolRepository.softDelete(id);
    }
}
