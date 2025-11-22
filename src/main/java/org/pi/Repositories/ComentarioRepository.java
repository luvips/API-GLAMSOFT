package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Comentario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComentarioRepository {

    public List<Comentario> findAll() throws SQLException {
        List<Comentario> comentarios = new ArrayList<>();
        // CORRECCIÓN: Añadida coma faltante y corregido "ORDEN BY"
        String sql = "SELECT c.id_comentario, c.comentario, c.fecha_comentario, u.email AS email_cliente " +
                     "FROM comentario c JOIN usuario u ON c.id_cliente = u.id_usuario " +
                     "ORDER BY c.fecha_comentario DESC";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                comentarios.add(mapResultSetToComentario(rs));
            }
        }
        return comentarios;
    }

    public List<Comentario> findComentariosByCliente(int idCliente) throws SQLException {
        List<Comentario> comentarios = new ArrayList<>();
        // CORRECCIÓN: Añadida coma faltante
        String sql = "SELECT c.id_comentario, c.comentario, c.fecha_comentario, u.email AS email_cliente " +
                     "FROM comentario c JOIN usuario u ON c.id_cliente = u.id_usuario " +
                     "WHERE c.id_cliente = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comentarios.add(mapResultSetToComentario(rs));
                }
            }
        }
        return comentarios;
    }

    public List<Comentario> findLatest(int limit) throws SQLException {
        List<Comentario> comentarios = new ArrayList<>();
        String sql = "SELECT c.id_comentario, c.comentario, c.fecha_comentario, u.email AS email_cliente " +
                     "FROM comentario c JOIN usuario u ON c.id_cliente = u.id_usuario " +
                     "ORDER BY c.fecha_comentario DESC LIMIT ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    comentarios.add(mapResultSetToComentario(rs));
                }
            }
        }
        return comentarios;
    }

    public Comentario findById(int id) throws SQLException {
        Comentario comen = null;
        String sql = "SELECT * FROM comentario WHERE id_comentario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    comen = mapResultSetToComentario(rs);
                }
            }
        }
        return comen;
    }

    public int save(Comentario comentario) throws SQLException {
        String sql = "INSERT INTO comentario(comentario, fecha_comentario, id_cita, id_cliente) VALUES(?,?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, comentario.getComentario());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, comentario.getIdCita());
            stmt.setInt(4, comentario.getIdCliente());
            
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("La inserción del comentario falló.");
            }
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                } else {
                    throw new SQLException("No se encontró el ID generado para el comentario.");
                }
            }
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

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM comentario WHERE id_comentario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Comentario mapResultSetToComentario(ResultSet rs) throws SQLException {
        Comentario comen = new Comentario();
        comen.setIdComentario(rs.getInt("id_comentario"));
        comen.setComentario(rs.getString("comentario"));
        comen.setFechaComentario(rs.getTimestamp("fecha_comentario").toLocalDateTime());
        if (hasColumn(rs, "email_cliente")) {
            comen.setEmailCliente(rs.getString("email_cliente"));
        }
        return comen;
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equals(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }
}
