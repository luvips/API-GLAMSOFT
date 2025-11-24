package org.pi.Services;

import org.pi.Models.Estilista;
import org.pi.Repositories.EstilistaRepository;
import org.pi.dto.EstilistaDTO;

import java.sql.SQLException;
import java.util.List;

public class EstilistaService {
    private final EstilistaRepository estilistaRepository;

    public EstilistaService(EstilistaRepository estilistaRepository) {
        this.estilistaRepository = estilistaRepository;
    }

    public List<EstilistaDTO> findAll() throws SQLException {
        return estilistaRepository.findAll();
    }

    public EstilistaDTO findById(int id) throws SQLException {
        return estilistaRepository.findById(id);
    }

    public Estilista create(Estilista estilista) throws SQLException {
        return estilistaRepository.save(estilista);
    }

    public boolean update(int id, Estilista estilista) throws SQLException {
        estilista.setIdEmpleado(id);
        return estilistaRepository.update(estilista);
    }

    public boolean delete(int id) throws SQLException, IllegalStateException {
        // Regla de negocio: No eliminar si tiene citas pendientes o confirmadas.
        if (estilistaRepository.hasCitas(id)) {
            throw new IllegalStateException("No se puede eliminar el estilista porque tiene citas activas.");
        }
        return estilistaRepository.delete(id);
    }

    public List<EstilistaDTO> getEstilistasByServicio(int idServicio) throws SQLException {
        return estilistaRepository.findEstilistasByServicio(idServicio);
    }
    // ✅ NUEVO
    public boolean createServicio(int idEstilista, int idServicio) throws SQLException {
        return estilistaRepository.asignarServicio(idEstilista, idServicio);
    }

    // ✅ NUEVO
    public boolean createHorario(int idEstilista, String dia, String inicio, String fin) throws SQLException {
        return estilistaRepository.asignarHorario(idEstilista, dia, inicio, fin);
    }
}
