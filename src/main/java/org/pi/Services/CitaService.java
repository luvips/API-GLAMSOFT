package org.pi.Services;

import org.pi.Models.Cita;
import org.pi.Repositories.CitaRepository;
import org.pi.dto.CitaDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class CitaService {

    private final CitaRepository citaRepository;
    private final List<String> ESTADOS_VALIDOS = Arrays.asList("PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA");

    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public List<CitaDTO> findAll() throws SQLException {
        return citaRepository.findAllCitas();
    }

    public CitaDTO findById(int id) throws SQLException {
        return citaRepository.findCitaById(id);
    }

    public List<CitaDTO> findByCliente(int idCliente) throws SQLException {
        return citaRepository.findCitaCliente(idCliente);
    }

    public List<CitaDTO> findByEstilista(int idEstilista) throws SQLException {
        return citaRepository.findByEstilista(idEstilista);
    }

    public List<CitaDTO> findByEstado(String estado) throws SQLException, IllegalArgumentException {
        if (estado == null || !ESTADOS_VALIDOS.contains(estado.toUpperCase())) {
            throw new IllegalArgumentException("Estado de cita no válido. Los estados permitidos son: " + String.join(", ", ESTADOS_VALIDOS));
        }
        return citaRepository.findByEstado(estado.toUpperCase());
    }

    public List<CitaDTO> findCitasMes(int mes, int anio) throws SQLException, IllegalArgumentException {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12.");
        }
        if (anio < 2020 || anio > 2100) {
            throw new IllegalArgumentException("El año no es válido.");
        }
        return citaRepository.findCitasMes(mes, anio);
    }

    public int create(Cita cita) throws SQLException, IllegalArgumentException {
        if (cita.getFechaCita() == null || cita.getFechaCita().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado.");
        }
        if (cita.getIdCliente() <= 0 || cita.getIdEstilista() <= 0 || cita.getIdHorario() <= 0) {
            throw new IllegalArgumentException("Los IDs de cliente, estilista y horario son obligatorios y deben ser mayores a 0.");
        }
        if (cita.getServicios() == null || cita.getServicios().isEmpty()) {
            throw new IllegalArgumentException("La cita debe tener al menos un servicio asociado.");
        }

        cita.setEstadoCita("PENDIENTE");
        cita.setFechaSolicitudCita(LocalDateTime.now());

        return citaRepository.save(cita);
    }

    public boolean update(int id, Cita cita) throws SQLException, IllegalArgumentException {
        CitaDTO citaExistente = findById(id);
        if (citaExistente == null) {
            return false; // Indica que no se encontró para que el controlador devuelva 404
        }

        if (cita.getFechaCita() == null || cita.getFechaCita().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado.");
        }
        if (cita.getIdEstilista() <= 0 || cita.getIdHorario() <= 0) {
            throw new IllegalArgumentException("Los IDs de estilista y horario son obligatorios.");
        }

        cita.setIdCita(id);
        return citaRepository.update(cita);
    }

    public boolean updateEstado(int id, String estado) throws SQLException, IllegalArgumentException {
        CitaDTO citaExistente = findById(id);
        if (citaExistente == null) {
            return false; // Indica que no se encontró
        }

        String upperCaseEstado = estado.toUpperCase();
        if (!ESTADOS_VALIDOS.contains(upperCaseEstado)) {
            throw new IllegalArgumentException("Estado de cita no válido. Los estados permitidos son: " + String.join(", ", ESTADOS_VALIDOS));
        }

        return citaRepository.updateEstado(id, upperCaseEstado);
    }

    public boolean delete(int id) throws SQLException {
        CitaDTO citaExistente = findById(id);
        if (citaExistente == null) {
            return false; // Indica que no se encontró
        }
        return citaRepository.delete(id);
    }
}
