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
    private final List<String> ESTADOS_VALIDOS = Arrays.asList("PENDIENTE", "CONFIRMADA", "COMPLETADA", "CANCELADA", "NO_ASISTIO");

    // CORRECCIÓN: Añadido constructor para inyección de dependencias
    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public List<CitaDTO> findAll(String estado, String fecha) throws SQLException {
        return citaRepository.findAll(estado, fecha);
    }

    public CitaDTO findById(int id) throws SQLException {
        return citaRepository.findById(id);
    }

    public List<CitaDTO> findByCliente(int idCliente) throws SQLException {
        return citaRepository.findByCliente(idCliente);
    }

    public List<CitaDTO> findByEstilista(int idEstilista) throws SQLException {
        return citaRepository.findByEstilista(idEstilista);
    }

    public List<CitaDTO> findByMonth(int mes, int anio) throws SQLException {
        if (mes < 1 || mes > 12 || anio < 2000) {
            throw new IllegalArgumentException("Mes o año inválido.");
        }
        return citaRepository.findByMonth(mes, anio);
    }

    public Cita create(Cita cita, List<Integer> servicios) throws SQLException {
        if (cita.getFechaHoraCita().isBefore(LocalDateTime.now())) { // Corrected to getFechaHoraCita
            throw new IllegalArgumentException("No se puede agendar una cita en el pasado.");
        }
        if (servicios == null || servicios.isEmpty()) {
            throw new IllegalArgumentException("La cita debe tener al menos un servicio.");
        }
        cita.setEstadoCita("PENDIENTE");
        return citaRepository.save(cita, servicios);
    }

    public boolean update(int id, Cita cita) throws SQLException {
        CitaDTO existente = citaRepository.findById(id);
        if (existente == null) {
            return false; // No encontrado
        }
        if (cita.getFechaHoraCita().isBefore(LocalDateTime.now())) { // Corrected to getFechaHoraCita
            throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado.");
        }
        cita.setIdCita(id);
        return citaRepository.update(cita);
    }

    public boolean updateEstado(int id, String estado) throws SQLException {
        if (!ESTADOS_VALIDOS.contains(estado.toUpperCase())) {
            throw new IllegalArgumentException("Estado de cita no válido.");
        }
        CitaDTO existente = citaRepository.findById(id);
        if (existente == null) {
            return false; // No encontrado
        }
        return citaRepository.updateEstado(id, estado.toUpperCase());
    }

    public boolean delete(int id) throws SQLException {
        CitaDTO existente = citaRepository.findById(id);
        if (existente == null) {
            return false; // No encontrado
        }
        return citaRepository.delete(id);
    }
}
