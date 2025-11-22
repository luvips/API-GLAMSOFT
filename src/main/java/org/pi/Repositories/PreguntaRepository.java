package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Pregunta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaRepository {

    private Pregunta mapResultSetToPregunta(ResultSet rs) throws SQLException {
        Pregunta pregunta = new Pregunta();
        pregunta.setIdPregunta(rs.getInt("id_pregunta"));
        pregunta.setPregunta(rs.getString("texto_pregunta"));
        pregunta.setRespuesta(rs.getString("texto_respuesta"));
        pregunta.setCategoria(rs.getString("tipo")); // Mapeando 'tipo' a 'categoria'
        pregunta.setActivo(rs.getBoolean("activo"));
        pregunta.setIdFormulario(rs.getInt("id_formulario"));
        return pregunta;
    }

    public List<Pregunta> findAll() throws SQLException {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "SELECT * FROM pregunta WHERE activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                preguntas.add(mapResultSetToPregunta(rs));
            }
        }
        return preguntas;
    }

    public Pregunta findById(int id) throws SQLException {
        String sql = "SELECT * FROM pregunta WHERE id_pregunta = ? AND activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPregunta(rs);
                }
            }
        }
        return null;
    }

    public Pregunta save(Pregunta pregunta) throws SQLException {
        String sql = "INSERT INTO pregunta(texto_pregunta, texto_respuesta, tipo, activo, id_formulario) VALUES(?,?,?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pregunta.getPregunta());
            stmt.setString(2, pregunta.getRespuesta());
            stmt.setString(3, pregunta.getCategoria());
            stmt.setBoolean(4, pregunta.isActivo());
            stmt.setInt(5, pregunta.getIdFormulario());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pregunta.setIdPregunta(rs.getInt(1));
                        return pregunta;
                    }
                }
            }
            throw new SQLException("No se pudo guardar la pregunta.");
        }
    }

    public boolean update(Pregunta pregunta) throws SQLException {
        String sql = "UPDATE pregunta SET texto_pregunta = ?, texto_respuesta = ?, tipo = ?, activo = ?, id_formulario = ? WHERE id_pregunta = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pregunta.getPregunta());
            stmt.setString(2, pregunta.getRespuesta());
            stmt.setString(3, pregunta.getCategoria());
            stmt.setBoolean(4, pregunta.isActivo());
            stmt.setInt(5, pregunta.getIdFormulario());
            stmt.setInt(6, pregunta.getIdPregunta());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean softDelete(int id) throws SQLException {
        String sql = "UPDATE pregunta SET activo = FALSE WHERE id_pregunta = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
