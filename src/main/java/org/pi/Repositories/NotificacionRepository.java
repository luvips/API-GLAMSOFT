package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Notificacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionRepository {

    private Notificacion mapResultSetToNotificacion(ResultSet rs) throws SQLException {
        Notificacion notificacion = new Notificacion();
        notificacion.setIdNotificacion(rs.getInt("id_notificacion"));
        notificacion.setIdUsuario(rs.getInt("id_usuario"));
        notificacion.setTipo(rs.getString("tipo"));
        notificacion.setTitulo(rs.getString("titulo"));
        notificacion.setMensaje(rs.getString("mensaje"));
        notificacion.setIdCita(rs.getInt("id_cita"));
        notificacion.setLeida(rs.getBoolean("leida"));
        notificacion.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        return notificacion;
    }

    public void save(Notificacion notificacion) throws SQLException {
        String sql = "INSERT INTO notificacion (id_usuario, tipo, titulo, mensaje, id_cita) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificacion.getIdUsuario());
            stmt.setString(2, notificacion.getTipo());
            stmt.setString(3, notificacion.getTitulo());
            stmt.setString(4, notificacion.getMensaje());
            stmt.setObject(5, notificacion.getIdCita());
            stmt.executeUpdate();
        }
    }

    public List<Notificacion> findByUsuarioId(int idUsuario) throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM notificacion WHERE id_usuario = ? ORDER BY fecha_creacion DESC";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notificaciones.add(mapResultSetToNotificacion(rs));
                }
            }
        }
        return notificaciones;
    }

    public boolean marcarComoLeida(int idNotificacion) throws SQLException {
        String sql = "UPDATE notificacion SET leida = TRUE WHERE id_notificacion = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idNotificacion);
            return stmt.executeUpdate() > 0;
        }
    }

    public int countNoLeidas(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notificacion WHERE id_usuario = ? AND leida = FALSE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
