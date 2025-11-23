package org.pi.Services;

import org.pi.Models.Notificacion;
import org.pi.Repositories.NotificacionRepository;
import org.pi.dto.NotificacionDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class NotificacionService {
    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    private NotificacionDTO toDTO(Notificacion notificacion) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setIdNotificacion(notificacion.getIdNotificacion());
        dto.setIdUsuario(notificacion.getIdUsuario());
        dto.setTipo(notificacion.getTipo());
        dto.setTitulo(notificacion.getTitulo());
        dto.setMensaje(notificacion.getMensaje());
        dto.setIdCita(notificacion.getIdCita());
        dto.setLeida(notificacion.isLeida());
        dto.setFechaCreacion(notificacion.getFechaCreacion());
        return dto;
    }

    public List<NotificacionDTO> getNotificacionesByUsuario(int idUsuario) throws SQLException {
        return notificacionRepository.findByUsuarioId(idUsuario).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public boolean marcarComoLeida(int idNotificacion) throws SQLException {
        return notificacionRepository.marcarComoLeida(idNotificacion);
    }

    public int contarNoLeidas(int idUsuario) throws SQLException {
        return notificacionRepository.countNoLeidas(idUsuario);
    }
    
    public void crearNotificacion(Notificacion notificacion) throws SQLException {
        notificacionRepository.save(notificacion);
    }
}
