package org.pi.Services;

import org.pi.Models.Servicio;
import org.pi.Repositories.ServicioRepository;

import java.sql.SQLException;
import java.util.List;

public class ServicioService {
    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public List<Servicio> findAllServicios() throws SQLException {
        return servicioRepository.findAll();
    }

    public Servicio findById(int id) throws SQLException {
        // El servicio ahora devuelve null si no se encuentra,
        // el controlador se encargará de la respuesta 404.
        return servicioRepository.findById(id);
    }

    public List<Servicio> findByCategoria(int id) throws SQLException {
        return servicioRepository.findByCategoria(id);
    }

    public int saveServicio(Servicio servicio) throws SQLException {
        // Las validaciones de datos de entrada se manejan en el controlador.
        // Aquí podrían ir validaciones de lógica de negocio (ej. no duplicados).
        return servicioRepository.save(servicio);
    }

    public boolean updateServicio(int id, Servicio servicio) throws SQLException {
        // Asegurar que el ID del objeto coincida con el ID de la URL
        servicio.setIdServicio(id);
        return servicioRepository.update(servicio);
    }

    public boolean deleteServicio(int id) throws SQLException {
        return servicioRepository.delete(id);
    }
}
