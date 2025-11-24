package org.pi.Services;

import org.pi.Models.Cita;
import org.pi.Models.Notificacion;
import org.pi.Repositories.CitaRepository;
import org.pi.Repositories.UsuarioRepository;
import org.pi.dto.CitaDTO;
import org.pi.dto.RespuestaFormularioDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class CitaService {

    private final CitaRepository citaRepository;
    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;
    private final List<String> ESTADOS_VALIDOS = Arrays.asList("PENDIENTE", "APROBADA", "RECHAZADA", "COMPLETADA", "CANCELADA", "NO_ASISTIO");

    public CitaService(CitaRepository citaRepository, NotificacionService notificacionService, UsuarioRepository usuarioRepository) {
        this.citaRepository = citaRepository;
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
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

    public List<CitaDTO> getCitasPendientes() throws SQLException {
        return citaRepository.findByEstado("PENDIENTE");
    }

    public List<CitaDTO> findByWeek(int semana, int anio) throws SQLException {
        if (semana < 1 || semana > 53 || anio < 2000) {
            throw new IllegalArgumentException("Semana o año inválido.");
        }
        return citaRepository.findByWeek(semana, anio);
    }

    public List<CitaDTO> findByMonth(int mes, int anio) throws SQLException {
        if (mes < 1 || mes > 12 || anio < 2000) {
            throw new IllegalArgumentException("Mes o año inválido.");
        }
        return citaRepository.findByMonth(mes, anio);
    }

    public List<CitaDTO> findByYear(int anio) throws SQLException {
        if (anio < 2000) {
            throw new IllegalArgumentException("Año inválido.");
        }
        return citaRepository.findByYear(anio);
    }

    public Cita create(Cita cita, List<Integer> servicios, List<RespuestaFormularioDTO> respuestas) throws SQLException, IllegalArgumentException {
        if (cita.getFechaHoraCita().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede agendar una cita en el pasado.");
        }
        if (servicios == null || servicios.isEmpty()) {
            throw new IllegalArgumentException("La cita debe tener al menos un servicio.");
        }

        boolean disponible = citaRepository.isEstilistaDisponible(cita.getIdEstilista(), cita.getFechaHoraCita());
        if (!disponible) {
            throw new IllegalArgumentException("El estilista no está disponible en la fecha y hora seleccionadas.");
        }

        cita.setEstadoCita("PENDIENTE");
        Cita citaCreada = citaRepository.save(cita, servicios, respuestas);

        List<Integer> idsParaNotificar = usuarioRepository.findAdminAndEstilistaIds(cita.getIdEstilista());
        for (Integer idUsuario : idsParaNotificar) {
            Notificacion notif = new Notificacion();
            notif.setIdUsuario(idUsuario);
            notif.setTipo("CITA_PENDIENTE");
            notif.setTitulo("Nueva cita pendiente de aprobación");
            notif.setMensaje("El cliente ha solicitado una cita para el " + cita.getFechaHoraCita().toLocalDate());
            notif.setIdCita(citaCreada.getIdCita());
            notificacionService.crearNotificacion(notif);
        }

        return citaCreada;
    }

    public void update(int id, Cita cita) throws SQLException, IllegalArgumentException {
        CitaDTO existente = citaRepository.findById(id);
        if (existente == null) {
            throw new IllegalArgumentException("Cita con ID " + id + " no encontrada.");
        }
        if (cita.getFechaHoraCita().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la cita no puede ser en el pasado.");
        }

        boolean disponible = citaRepository.isEstilistaDisponible(cita.getIdEstilista(), cita.getFechaHoraCita());
        if (!disponible) {
            throw new IllegalArgumentException("El estilista no está disponible en la nueva fecha y hora seleccionadas.");
        }

        cita.setIdCita(id);
        citaRepository.update(cita);
    }

    public boolean aprobarCita(int idCita, int adminId) throws SQLException {
        CitaDTO cita = citaRepository.findById(idCita);
        if (cita == null) {
            return false;
        }

        boolean success = citaRepository.aprobar(idCita, adminId);
        if (success) {
            Notificacion notif = new Notificacion();
            notif.setIdUsuario(cita.getIdCliente());
            notif.setTipo("CITA_APROBADA");
            notif.setTitulo("¡Cita aprobada!");
            notif.setMensaje("Tu cita para el " + cita.getFechaHoraCita().toLocalDate() + " ha sido aprobada.");
            notif.setIdCita(idCita);
            notificacionService.crearNotificacion(notif);
        }
        return success;
    }

    public boolean rechazarCita(int idCita, String razon) throws SQLException {
        CitaDTO cita = citaRepository.findById(idCita);
        if (cita == null) {
            return false;
        }

        boolean success = citaRepository.rechazar(idCita, razon);
        if (success) {
            Notificacion notif = new Notificacion();
            notif.setIdUsuario(cita.getIdCliente());
            notif.setTipo("CITA_RECHAZADA");
            notif.setTitulo("Cita no aprobada");
            notif.setMensaje("Tu cita no pudo ser aprobada. Razón: " + razon);
            notif.setIdCita(idCita);
            notificacionService.crearNotificacion(notif);
        }
        return success;
    }

    public boolean completarCita(int idCita) throws SQLException {
        CitaDTO cita = citaRepository.findById(idCita);
        if (cita == null) {
            return false;
        }
        boolean success = citaRepository.completar(idCita);
        if (success) {
            Notificacion notif = new Notificacion();
            notif.setIdUsuario(cita.getIdCliente());
            notif.setTipo("CITA_COMPLETADA");
            notif.setTitulo("¡Gracias por tu visita!");
            notif.setMensaje("Tu cita ha sido completada. ¡Esperamos verte pronto!");
            notif.setIdCita(idCita);
            notificacionService.crearNotificacion(notif);
        }
        return success;
    }

    public boolean cancelarCita(int idCita, String razon) throws SQLException {
        CitaDTO cita = citaRepository.findById(idCita);
        if (cita == null) {
            return false;
        }
        boolean success = citaRepository.cancelar(idCita, razon);
        if (success) {
            Notificacion notif = new Notificacion();
            notif.setIdUsuario(cita.getIdCliente());
            notif.setTipo("CITA_CANCELADA");
            notif.setTitulo("Cita cancelada");
            notif.setMensaje("Tu cita ha sido cancelada. Razón: " + razon);
            notif.setIdCita(idCita);
            notificacionService.crearNotificacion(notif);
        }
        return success;
    }

    public boolean delete(int id) throws SQLException {
        return citaRepository.delete(id);
    }
}
