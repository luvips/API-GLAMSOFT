package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Horario;
import org.pi.dto.HorarioDTO;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioRepository {

    private HorarioDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        HorarioDTO dto = new HorarioDTO();
        dto.setIdHorario(rs.getInt("id_horario"));
        dto.setDia(rs.getString("dia_semana"));
        dto.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
        dto.setHoraFin(rs.getTime("hora_fin").toLocalTime());

        if (hasColumn(rs, "id_estilista")) {
            HorarioDTO.EstilistaDTO estilista = new HorarioDTO.EstilistaDTO();
            estilista.setIdEstilista(rs.getInt("id_estilista"));
            estilista.setNombre(rs.getString("nombre_estilista"));
            dto.setEstilista(estilista);
        }
        return dto;
    }

    public List<HorarioDTO> findAll() throws SQLException {
        List<HorarioDTO> horarios = new ArrayList<>();
        String sql = "SELECT h.*, e.id_empleado as id_estilista, e.nombre as nombre_estilista " +
                     "FROM horario h " +
                     "LEFT JOIN estilista_horario eh ON h.id_horario = eh.id_horario " +
                     "LEFT JOIN empleado e ON eh.id_estilista = e.id_empleado " +
                     "WHERE h.activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                horarios.add(mapResultSetToDTO(rs));
            }
        }
        return horarios;
    }

    public HorarioDTO findById(int id) throws SQLException {
        String sql = "SELECT h.*, e.id_empleado as id_estilista, e.nombre as nombre_estilista " +
                     "FROM horario h " +
                     "LEFT JOIN estilista_horario eh ON h.id_horario = eh.id_horario " +
                     "LEFT JOIN empleado e ON eh.id_estilista = e.id_empleado " +
                     "WHERE h.id_horario = ? AND h.activo = TRUE";
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

    public Horario save(Horario horario, int idEstilista) throws SQLException {
        String sqlHorario = "INSERT INTO horario(dia_semana, hora_inicio, hora_fin) VALUES(?,?,?)";
        String sqlRelacion = "INSERT INTO estilista_horario(id_estilista, id_horario) VALUES(?,?)";

        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false);

                // 1. Crear Horario
                try (PreparedStatement stmtHorario = conn.prepareStatement(sqlHorario, Statement.RETURN_GENERATED_KEYS)) {
                    stmtHorario.setString(1, horario.getDiaSemana());
                    stmtHorario.setTime(2, Time.valueOf(horario.getHoraInicio()));
                    stmtHorario.setTime(3, Time.valueOf(horario.getHoraFin()));
                    if (stmtHorario.executeUpdate() > 0) {
                        try (ResultSet rs = stmtHorario.getGeneratedKeys()) {
                            if (rs.next()) horario.setIdHorario(rs.getInt(1));
                        }
                    } else {
                        throw new SQLException("No se pudo guardar el horario.");
                    }
                }

                // 2. Crear Relación
                try (PreparedStatement stmtRelacion = conn.prepareStatement(sqlRelacion)) {
                    stmtRelacion.setInt(1, idEstilista);
                    stmtRelacion.setInt(2, horario.getIdHorario());
                    stmtRelacion.executeUpdate();
                }

                conn.commit();
                return horario;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean update(Horario horario) throws SQLException {
        String sql = "UPDATE horario SET hora_inicio = ?, hora_fin = ? WHERE id_horario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTime(1, Time.valueOf(horario.getHoraInicio()));
            stmt.setTime(2, Time.valueOf(horario.getHoraFin()));
            stmt.setInt(3, horario.getIdHorario());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean softDelete(int id) throws SQLException {
        String sql = "UPDATE horario SET activo = FALSE WHERE id_horario = ?";
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
