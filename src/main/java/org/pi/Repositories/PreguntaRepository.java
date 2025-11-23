package org.pi.Repositories;

import com.google.gson.Gson;
import org.pi.Config.DBconfig;
import org.pi.Models.Pregunta;
import org.pi.dto.PreguntaFormularioDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaRepository {

    private Pregunta mapResultSetToPregunta(ResultSet rs) throws SQLException {
        Pregunta pregunta = new Pregunta();
        pregunta.setIdPregunta(rs.getInt("id_pregunta"));
        pregunta.setPregunta(rs.getString("texto_pregunta"));
        pregunta.setTipoRespuesta(rs.getString("tipo_respuesta"));
        pregunta.setOpciones(rs.getString("opciones"));
        pregunta.setObligatoria(rs.getBoolean("obligatoria"));
        pregunta.setOrden(rs.getInt("orden"));
        pregunta.setActivo(rs.getBoolean("activo"));
        pregunta.setIdServicio(rs.getInt("id_servicio"));
        return pregunta;
    }

    public List<Pregunta> findByServicioId(int idServicio) throws SQLException {
        List<Pregunta> preguntas = new ArrayList<>();
        String sql = "SELECT * FROM pregunta WHERE id_servicio = ? AND activo = TRUE ORDER BY orden";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idServicio);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    preguntas.add(mapResultSetToPregunta(rs));
                }
            }
        }
        return preguntas;
    }

    public Pregunta findById(int id) throws SQLException {
        String sql = "SELECT * FROM pregunta WHERE id_pregunta = ?";
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

    public Pregunta save(PreguntaFormularioDTO dto) throws SQLException {
        String sql = "INSERT INTO pregunta(id_servicio, texto_pregunta, tipo_respuesta, opciones, obligatoria, orden, activo) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, dto.getIdServicio());
            stmt.setString(2, dto.getPregunta());
            stmt.setString(3, dto.getTipoRespuesta());
            stmt.setString(4, new Gson().toJson(dto.getOpciones()));
            stmt.setBoolean(5, dto.getObligatoria());
            stmt.setInt(6, dto.getOrden());
            stmt.setBoolean(7, dto.getActivo());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Pregunta p = new Pregunta();
                        p.setIdPregunta(rs.getInt(1));
                        return p;
                    }
                }
            }
            throw new SQLException("No se pudo guardar la pregunta.");
        }
    }

    public boolean update(int id, PreguntaFormularioDTO dto) throws SQLException {
        String sql = "UPDATE pregunta SET id_servicio = ?, texto_pregunta = ?, tipo_respuesta = ?, opciones = ?, obligatoria = ?, orden = ?, activo = ? WHERE id_pregunta = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dto.getIdServicio());
            stmt.setString(2, dto.getPregunta());
            stmt.setString(3, dto.getTipoRespuesta());
            stmt.setString(4, new Gson().toJson(dto.getOpciones()));
            stmt.setBoolean(5, dto.getObligatoria());
            stmt.setInt(6, dto.getOrden());
            stmt.setBoolean(7, dto.getActivo());
            stmt.setInt(8, id);
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
