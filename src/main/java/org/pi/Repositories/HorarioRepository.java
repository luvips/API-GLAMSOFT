package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Horario;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioRepository {

    public List<Horario> findAll() throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        String sql = "SELECT * FROM horario";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                horarios.add(mapResultSetToHorario(rs));
            }
        }
        return horarios;
    }

    public Horario findById(int id) throws SQLException {
        Horario horario = null;
        String sql = "SELECT * FROM horario WHERE id_horario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    horario = mapResultSetToHorario(rs);
                }
            }
        }
        return horario;
    }

    public int save(Horario horario) throws SQLException {
        String sql = "INSERT INTO horario(hora_inicio, hora_fin, dia_semana) VALUES(?, ?, ?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setTime(1, Time.valueOf(horario.getHoraInicio()));
            stmt.setTime(2, Time.valueOf(horario.getHoraFin()));
            stmt.setString(3, horario.getDiaSemana());
            
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("La inserción de horario falló.");
            }
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                } else {
                    throw new SQLException("No se encontró id generado para horario.");
                }
            }
        }
    }

    public boolean update(Horario horario) throws SQLException {
        String sql = "UPDATE horario SET hora_inicio = ?, hora_fin = ?, dia_semana = ? WHERE id_horario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTime(1, Time.valueOf(horario.getHoraInicio()));
            stmt.setTime(2, Time.valueOf(horario.getHoraFin()));
            stmt.setString(3, horario.getDiaSemana());
            stmt.setInt(4, horario.getIdHorario());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM horario WHERE id_horario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Horario mapResultSetToHorario(ResultSet rs) throws SQLException {
        int idHorario = rs.getInt("id_horario");
        LocalTime horaInicio = rs.getTime("hora_inicio").toLocalTime();
        LocalTime horaFin = rs.getTime("hora_fin").toLocalTime();
        String diaSemana = rs.getString("dia_semana");
        return new Horario(idHorario, horaInicio, horaFin, diaSemana);
    }
}
