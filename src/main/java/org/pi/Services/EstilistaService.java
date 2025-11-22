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

    public List<EstilistaDTO> findAllEstilistas() throws SQLException {
        return estilistaRepository.findAllEstilistas();
    }

    public EstilistaDTO findEstilistaById(int id) throws SQLException {
        // Devuelve null si no se encuentra, el controlador gestiona el 404.
        return estilistaRepository.findEstilistaById(id);
    }

    public List<Estilista> findEstilistaServicio(int idServicio, Cita fecha) throws SQLException {
        return estilistaRepository.findEstilistasServicios(idServicio, fecha);
    }

    public List<Horario> findHorarios(int idEstilista) throws SQLException {
        // Simplemente devuelve la lista, vacía si no hay resultados.
        return estilistaRepository.findHorarios(idEstilista);
    }

    public List<Servicio> findServicios(int idEstilista) throws SQLException {
        // Simplemente devuelve la lista, vacía si no hay resultados.
        return estilistaRepository.findServicios(idEstilista);
    }

    public void saveHorario(Estilista estilista) throws SQLException, IllegalArgumentException {
        if (estilista.getIdEmpleado() <= 0 || estilista.getIdHorario() <= 0) {
            throw new IllegalArgumentException("Los IDs de estilista y horario son obligatorios.");
        }
        // La lógica para evitar duplicados es una buena práctica de negocio.
        List<Horario> horariosExistentes = estilistaRepository.findHorarios(estilista.getIdEmpleado());
        boolean yaAsignado = horariosExistentes.stream()
                .anyMatch(h -> h.getIdHorario() == estilista.getIdHorario());

        if (yaAsignado) {
            throw new IllegalArgumentException("El horario ya está asignado a este estilista");
        }
        estilistaRepository.saveHorarios(estilista);
    }

    public void saveServicio(Estilista estilista) throws SQLException, IllegalArgumentException {
        if (estilista.getIdEmpleado() <= 0 || estilista.getIdServicio() <= 0) {
            throw new IllegalArgumentException("Los IDs de estilista y servicio son obligatorios.");
        }
        // La lógica para evitar duplicados es una buena práctica de negocio.
        List<Servicio> serviciosExistentes = estilistaRepository.findServicios(estilista.getIdEmpleado());
        boolean yaAsignado = serviciosExistentes.stream()
                .anyMatch(s -> s.getIdServicio() == estilista.getIdServicio());

        if (yaAsignado) {
            throw new IllegalArgumentException("El servicio ya está asignado a este estilista");
        }
        estilistaRepository.saveServicios(estilista);
    }
}
