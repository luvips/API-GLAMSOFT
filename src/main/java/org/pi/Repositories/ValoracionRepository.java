package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Valoracion;
import org.pi.dto.ValoracionDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ValoracionRepository {

    private ValoracionDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        ValoracionDTO dto = new ValoracionDTO();
        dto.setIdValoracion(rs.getInt("id_valoracion"));
        dto.setCalificacion(rs.getBigDecimal("puntuacion"));
        dto.setComentario(rs.getString("comentario"));
        dto.setFecha(rs.getTimestamp("fecha_valoracion").toLocalDateTime());

        // Cliente
        ValoracionDTO.ClienteDTO cliente = new ValoracionDTO.ClienteDTO();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        cliente.setNombre(rs.getString("nombre_cliente"));
        dto.setCliente(cliente);

        // Servicio
        ValoracionDTO.ServicioDTO servicio = new ValoracionDTO.ServicioDTO();
        servicio.setIdServicio(rs.getInt("id_servicio"));
        servicio.setNombre(rs.getString("nombre_servicio"));
        dto.setServicio(servicio);

        return dto;
    }

    public List<ValoracionDTO> findAll() throws SQLException {
        List<ValoracionDTO> valoraciones = new ArrayList<>();
        String sql = "SELECT v.*, u.nombre AS nombre_cliente, s.nombre_servicio " +
                     "FROM valoracion v " +
                     "JOIN usuario u ON v.id_cliente = u.id_usuario " +
                     "JOIN servicio s ON v.id_servicio = s.id_servicio " +
                     "ORDER BY v.fecha_valoracion DESC";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                valoraciones.add(mapResultSetToDTO(rs));
            }
        }
        return valoraciones;
    }

    public ValoracionDTO findById(int id) throws SQLException {
        String sql = "SELECT v.*, u.nombre AS nombre_cliente, s.nombre_servicio " +
                     "FROM valoracion v " +
                     "JOIN usuario u ON v.id_cliente = u.id_usuario " +
                     "JOIN servicio s ON v.id_servicio = s.id_servicio " +
                     "WHERE v.id_valoracion = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        }
        return null;
    }

    public boolean isCitaCompletada(int idCita) throws SQLException {
        String sql = "SELECT 1 FROM cita WHERE id_cita = ? AND estado_cita = 'COMPLETADA'";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCita);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Valoracion save(Valoracion valoracion) throws SQLException {
        String sql = "INSERT INTO valoracion(puntuacion, comentario, id_cita, id_cliente, id_servicio) VALUES(?,?,?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBigDecimal(1, valoracion.getPuntuacion());
            stmt.setString(2, valoracion.getComentario());
            stmt.setInt(3, valoracion.getIdCita());
            stmt.setInt(4, valoracion.getIdCliente());
            stmt.setInt(5, valoracion.getIdServicio());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        valoracion.setIdValoracion(rs.getInt(1));
                        return valoracion;
                    }
                }
            }
            throw new SQLException("No se pudo guardar la valoración.");
        }
    }

    public boolean update(Valoracion valoracion) throws SQLException {
        String sql = "UPDATE valoracion SET puntuacion = ?, comentario = ? WHERE id_valoracion = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, valoracion.getPuntuacion());
            stmt.setString(2, valoracion.getComentario());
            stmt.setInt(3, valoracion.getIdValoracion());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM valoracion WHERE id_valoracion = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
