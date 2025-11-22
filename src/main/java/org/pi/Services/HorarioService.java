package org.pi.Services;

import org.pi.Models.Horario;
import org.pi.Repositories.HorarioRepository;

import java.sql.SQLException;
import java.util.List;

public class HorarioService {

    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public List<Horario> findAll() throws SQLException {
        return horarioRepository.findAll();
    }

    public Horario findById(int id) throws SQLException {
        return horarioRepository.findById(id);
    }

    public int save(Horario horario) throws SQLException {
        // Las validaciones de datos de entrada se manejan en el controlador.
        return horarioRepository.save(horario);
    }

    public boolean update(Horario horario) throws SQLException {
        // El ID ya viene en el objeto desde el controlador
        return horarioRepository.update(horario);
    }

    public boolean delete(int id) throws SQLException {
        return horarioRepository.delete(id);
    }
}
