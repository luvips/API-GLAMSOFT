package org.pi.Services;

import org.pi.Models.Valoracion;
import org.pi.Repositories.ValoracionRepository;
import org.pi.dto.ValoracionDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public record ValoracionService(ValoracionRepository valoracionRepository) {

    public List<ValoracionDTO> findAll() throws SQLException {
        return valoracionRepository.findAll();
    }

    public ValoracionDTO findById(int id) throws SQLException {
        return valoracionRepository.findById(id);
    }

    public Valoracion create(Valoracion valoracion) throws SQLException, IllegalStateException {
        // Regla de negocio: La cita debe estar completada
        if (!valoracionRepository.isCitaCompletada(valoracion.getIdCita())) {
            throw new IllegalStateException("Solo se pueden valorar citas completadas.");
        }

        // Validación de la puntuación
        if (valoracion.getPuntuacion().compareTo(BigDecimal.ONE) < 0 || valoracion.getPuntuacion().compareTo(new BigDecimal("5")) > 0) {
            throw new IllegalArgumentException("La calificación debe estar entre 1.0 y 5.0");
        }

        return valoracionRepository.save(valoracion);
    }

    public boolean update(int id, Valoracion valoracion) throws SQLException {
        valoracion.setIdValoracion(id);
        return valoracionRepository.update(valoracion);
    }

    public boolean delete(int id) throws SQLException {
        return valoracionRepository.delete(id);
    }
}
