package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Comentario;
import org.pi.dto.ComentarioDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComentarioRepository {

    private ComentarioDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        ComentarioDTO dto = new ComentarioDTO();
        dto.setIdComentario(rs.getInt("id_comentario"));
        dto.setContenido(rs.getString("comentario"));
        dto.setFecha(rs.getTimestamp("fecha_comentario").toLocalDateTime());

        if (hasColumn(rs, "nombre_cliente")) {
            ComentarioDTO.ClienteDTO cliente = new ComentarioDTO.ClienteDTO();
            cliente.setIdCliente(rs.getInt("id_cliente"));
            cliente.setNombre(rs.getString("nombre_cliente"));
            dto.setCliente(cliente);
        }

        if (hasColumn(rs, "id_cita_fk")) {
            ComentarioDTO.CitaDTO cita = new ComentarioDTO.CitaDTO();
            cita.setIdCita(rs.getInt("id_cita_fk"));
            if(hasColumn(rs, "nombre_servicio")) {
                cita.setServicio(rs.getString("nombre_servicio"));
            }
            dto.setCita(cita);
        }
        
        return dto;
    }

    public List<ComentarioDTO> findAll() throws SQLException {
        List<ComentarioDTO> comentarios = new ArrayList<>();
        String sql = "SELECT c.id_comentario, c.comentario, c.fecha_comentario, c.id_cliente, u.nombre AS nombre_cliente " +
                     "FROM comentario c " +
                     "JOIN usuario u ON c.id_cliente = u.id_usuario " +
                     "WHERE c.activo = TRUE ORDER BY c.fecha_comentario DESC";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                comentarios.add(mapResultSetToDTO(rs));
            }
        }
        return comentarios;
    }

    public ComentarioDTO findById(int id) throws SQLException {
        String sql = "SELECT c.id_comentario, c.comentario, c.fecha_comentario, c.id_cliente, u.nombre AS nombre_cliente " +
                     "FROM comentario c " +
                     "JOIN usuario u ON c.id_cliente = u.id_usuario " +
                     "WHERE c.id_comentario = ? AND c.activo = TRUE";
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

    public List<ComentarioDTO> findByCliente(int idCliente) throws SQLException {
        List<ComentarioDTO> comentarios = new ArrayList<>();
        String sql = "SELECT c.id_comentario, c.comentario, c.fecha_comentario, c.id_cita as id_cita_fk, " +
                     "(SELECT GROUP_CONCAT(s.nombre_servicio SEPARATOR ', ') FROM cita_servicio cs JOIN servicio s ON cs.id_servicio = s.id_servicio WHERE cs.id_cita = c.id_cita) as nombre_servicio " +
                     "FROM comentario c " +
                     "WHERE c.id_cliente = ? AND c.activo = TRUE ORDER BY c.fecha_comentario DESC";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comentarios.add(mapResultSetToDTO(rs));
                }
            }
        }
        return comentarios;
    }

    public Comentario save(Comentario comentario) throws SQLException {
        String sql = "INSERT INTO comentario(comentario, id_cita, id_cliente) VALUES(?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, comentario.getComentario());
            stmt.setInt(2, comentario.getIdCita());
            stmt.setInt(3, comentario.getIdCliente());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        comentario.setIdComentario(rs.getInt(1));
                        return comentario;
                    }
                }
            }
            throw new SQLException("No se pudo guardar el comentario.");
        }
    }

    public boolean update(Comentario comentario) throws SQLException {
        String sql = "UPDATE comentario SET comentario = ? WHERE id_comentario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, comentario.getComentario());
            stmt.setInt(2, comentario.getIdComentario());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean softDelete(int id) throws SQLException {
        String sql = "UPDATE comentario SET activo = FALSE WHERE id_comentario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equalsIgnoreCase(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }
}
