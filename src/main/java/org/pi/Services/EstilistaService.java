package org.pi.Services;

import org.pi.Models.Cita;
import org.pi.Models.Estilista;
import org.pi.Models.Horario;
import org.pi.Models.Servicio;
import org.pi.Repositories.EstilistaRepository;
import org.pi.dto.EstilistaDTO;

import java.sql.SQLException;
import java.util.List;

public class EstilistaService {
    private final EstilistaRepository estilistaRepository;

    public EstilistaService(EstilistaRepository estilistaRepository) {
        this.estilistaRepository = estilistaRepository;
    }

    // --- MÉTODOS DE LECTURA ---

    public List<EstilistaDTO> findAllEstilistas() throws SQLException {
        return estilistaRepository.findAllEstilistas();
    }

    public EstilistaDTO findEstilistaById(int id) throws SQLException {
        return estilistaRepository.findEstilistaById(id);
    }

    public List<Estilista> findEstilistaServicio(int idServicio, Cita fecha) throws SQLException {
        // Lógica existente...
        return null; // Placeholder
    }

    // --- MÉTODOS CRUD ---

    public Estilista create(Estilista estilista) throws SQLException {
        // Las validaciones de datos se hacen en el controlador
        return estilistaRepository.save(estilista);
    }

    public boolean update(int id, Estilista estilista) throws SQLException {
        estilista.setIdEmpleado(id);
        return estilistaRepository.update(estilista);
    }

    public boolean delete(int id) throws SQLException {
        return estilistaRepository.delete(id);
    }

    // --- MÉTODOS DE RELACIONES ---
    
    public List<Horario> findHorarios(int idEstilista) throws SQLException {
        // Lógica existente...
        return null; // Placeholder
    }

    public List<Servicio> findServicios(int idEstilista) throws SQLException {
        // Lógica existente...
        return null; // Placeholder
    }

    public void saveHorario(Estilista estilista) throws SQLException, IllegalArgumentException {
        // Lógica existente...
    }

    public void saveServicio(Estilista estilista) throws SQLException, IllegalArgumentException {
        // Lógica existente...
    }
}
