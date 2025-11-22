package org.pi.Services;

import org.pi.Models.Portafolio;
import org.pi.Repositories.PortafolioRepository;
import org.pi.dto.PortafolioDTO;

import java.sql.SQLException;
import java.util.List;

public class PortafolioService {

    private final PortafolioRepository portafolioRepository;

    public PortafolioService(PortafolioRepository portafolioRepository) {
        this.portafolioRepository = portafolioRepository;
    }

    public List<PortafolioDTO> findAll() throws SQLException {
        return portafolioRepository.findAll();
    }

    public PortafolioDTO findById(int id) throws SQLException {
        return portafolioRepository.findById(id);
    }

    public Portafolio create(Portafolio portafolio) throws SQLException {
        return portafolioRepository.save(portafolio);
    }

    public boolean update(int id, Portafolio portafolio) throws SQLException {
        portafolio.setIdImagen(id);
        return portafolioRepository.update(portafolio);
    }

    public boolean delete(int id) throws SQLException {
        return portafolioRepository.delete(id);
    }
}
