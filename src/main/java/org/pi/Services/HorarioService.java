package org.pi.Services;

import org.pi.Models.Horario;
import org.pi.Repositories.HorarioRepository;
import org.pi.dto.HorarioDTO;

import java.sql.SQLException;
import java.util.List;

public class HorarioService {

    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public List<HorarioDTO> findAll() throws SQLException {
        return horarioRepository.findAll();
    }

    public HorarioDTO findById(int id) throws SQLException {
        return horarioRepository.findById(id);
    }

    public Horario create(Horario horario, int idEstilista) throws SQLException {
        if (idEstilista <= 0) {
            throw new IllegalArgumentException("Se requiere un ID de estilista válido.");
        }
        return horarioRepository.save(horario, idEstilista);
    }

    public boolean update(int id, Horario horario) throws SQLException {
        horario.setIdHorario(id);
        return horarioRepository.update(horario);
    }

    public boolean delete(int id) throws SQLException {
        return horarioRepository.softDelete(id);
    }
}
