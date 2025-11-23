package org.pi.Services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.pi.Models.Pregunta;
import org.pi.Repositories.PreguntaRepository;
import org.pi.dto.PreguntaFormularioDTO;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final Gson gson = new Gson();

    public PreguntaService(PreguntaRepository preguntaRepository) {
        this.preguntaRepository = preguntaRepository;
    }

    private PreguntaFormularioDTO toDTO(Pregunta pregunta) {
        PreguntaFormularioDTO dto = new PreguntaFormularioDTO();
        dto.setIdPregunta(pregunta.getIdPregunta());
        dto.setIdServicio(pregunta.getIdServicio());
        dto.setPregunta(pregunta.getPregunta());
        dto.setTipoRespuesta(pregunta.getTipoRespuesta());
        
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> opciones = gson.fromJson(pregunta.getOpciones(), listType);
        dto.setOpciones(opciones);
        
        dto.setObligatoria(pregunta.isObligatoria());
        dto.setOrden(pregunta.getOrden());
        dto.setActivo(pregunta.isActivo());
        return dto;
    }

    public List<PreguntaFormularioDTO> getPreguntasByServicio(int idServicio) throws SQLException {
        return preguntaRepository.findByServicioId(idServicio).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Pregunta create(PreguntaFormularioDTO dto) throws SQLException {
        return preguntaRepository.save(dto);
    }

    public boolean update(int id, PreguntaFormularioDTO dto) throws SQLException {
        return preguntaRepository.update(id, dto);
    }

    public boolean delete(int id) throws SQLException {
        return preguntaRepository.softDelete(id);
    }
}
