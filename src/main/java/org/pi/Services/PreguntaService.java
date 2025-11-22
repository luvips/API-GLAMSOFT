package org.pi.Services;

import org.pi.Models.Pregunta;
import org.pi.Repositories.PreguntaRepository;
import org.pi.dto.PreguntaDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class PreguntaService {

    private final PreguntaRepository preguntaRepository;

    public PreguntaService(PreguntaRepository preguntaRepository) {
        this.preguntaRepository = preguntaRepository;
    }

    private PreguntaDTO toDTO(Pregunta pregunta) {
        PreguntaDTO dto = new PreguntaDTO();
        dto.setIdPregunta(pregunta.getIdPregunta());
        dto.setPregunta(pregunta.getPregunta());
        dto.setRespuesta(pregunta.getRespuesta());
        dto.setCategoria(pregunta.getCategoria());
        dto.setActivo(pregunta.isActivo());
        return dto;
    }

    public List<PreguntaDTO> findAll() throws SQLException {
        return preguntaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PreguntaDTO findById(int id) throws SQLException {
        Pregunta pregunta = preguntaRepository.findById(id);
        return pregunta != null ? toDTO(pregunta) : null;
    }

    public Pregunta create(Pregunta pregunta) throws SQLException {
        return preguntaRepository.save(pregunta);
    }

    public boolean update(int id, Pregunta pregunta) throws SQLException {
        pregunta.setIdPregunta(id);
        return preguntaRepository.update(pregunta);
    }

    public boolean delete(int id) throws SQLException {
        return preguntaRepository.softDelete(id);
    }
}
