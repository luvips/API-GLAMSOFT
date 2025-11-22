package org.pi.Services;

import org.pi.Models.Servicio;
import org.pi.Repositories.ServicioRepository;
import org.pi.dto.ServicioDTO;

import java.sql.SQLException;
import java.util.List;

public class ServicioService {
    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public List<ServicioDTO> findAll(String categoria, Boolean activo) throws SQLException {
        return servicioRepository.findAll(categoria, activo);
    }

    public ServicioDTO findById(int id) throws SQLException {
        return servicioRepository.findById(id);
    }

    public Servicio create(Servicio servicio) throws SQLException {
        return servicioRepository.save(servicio);
    }

    public boolean update(int id, Servicio servicio) throws SQLException {
        servicio.setIdServicio(id);
        return servicioRepository.update(servicio);
    }

    public boolean delete(int id) throws SQLException {
        return servicioRepository.softDelete(id);
    }
}
