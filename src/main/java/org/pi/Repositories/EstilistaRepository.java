package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Estilista;
import org.pi.dto.EstilistaDTO;
import org.pi.dto.HorarioDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstilistaRepository {

    public List<EstilistaDTO> findAll() throws SQLException {
        List<EstilistaDTO> estilistas = new ArrayList<>();
        String sql = "SELECT " +
                "e.id_empleado, e.nombre, e.puesto AS especialidad, e.telefono, u.email, e.activo, " +
                "COALESCE(AVG(v.puntuacion), 0) AS valoracion_promedio, " +
                "COUNT(DISTINCT v.id_valoracion) AS total_valoraciones " +
                "FROM empleado e " +
                "JOIN usuario u ON e.id_usuario = u.id_usuario " +
                "LEFT JOIN cita c ON e.id_empleado = c.id_estilista " +
                "LEFT JOIN valoracion v ON c.id_cita = v.id_cita " +
                "WHERE u.id_rol = 2 AND e.activo = TRUE " + // Rol Estilista = 2
                "GROUP BY e.id_empleado, e.nombre, e.puesto, e.telefono, u.email, e.activo";

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                EstilistaDTO dto = new EstilistaDTO();
                dto.setIdEstilista(rs.getInt("id_empleado"));
                dto.setNombre(rs.getString("nombre"));
                dto.setEspecialidad(rs.getString("especialidad"));
                dto.setTelefono(rs.getString("telefono"));
                dto.setEmail(rs.getString("email"));
                dto.setActivo(rs.getBoolean("activo"));
                dto.setValoracionPromedio(rs.getDouble("valoracion_promedio"));
                dto.setTotalValoraciones(rs.getInt("total_valoraciones"));
                estilistas.add(dto);
            }
        }
        return estilistas;
    }

    public EstilistaDTO findById(int id) throws SQLException {
        EstilistaDTO dto = null;
        String sql = "SELECT " +
                "e.id_empleado, e.nombre, e.puesto AS especialidad, e.telefono, u.email, e.activo, " +
                "COALESCE(AVG(v.puntuacion), 0) AS valoracion_promedio " +
                "FROM empleado e " +
                "JOIN usuario u ON e.id_usuario = u.id_usuario " +
                "LEFT JOIN cita c ON e.id_empleado = c.id_estilista " +
                "LEFT JOIN valoracion v ON c.id_cita = v.id_cita " +
                "WHERE e.id_empleado = ? AND u.id_rol = 2 " +
                "GROUP BY e.id_empleado";

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dto = new EstilistaDTO();
                    dto.setIdEstilista(rs.getInt("id_empleado"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setEspecialidad(rs.getString("especialidad"));
                    dto.setTelefono(rs.getString("telefono"));
                    dto.setEmail(rs.getString("email"));
                    dto.setActivo(rs.getBoolean("activo"));
                    dto.setValoracionPromedio(rs.getDouble("valoracion_promedio"));
                    dto.setHorarios(findHorariosForEstilista(id, conn));
                }
            }
        }
        return dto;
    }

    private List<HorarioDTO> findHorariosForEstilista(int idEstilista, Connection conn) throws SQLException {
        List<HorarioDTO> horarios = new ArrayList<>();
        String sql = "SELECT h.dia_semana, h.hora_inicio, h.hora_fin " +
                     "FROM estilista_horario eh " +
                     "JOIN horario h ON eh.id_horario = h.id_horario " +
                     "WHERE eh.id_estilista = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstilista);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    horarios.add(new HorarioDTO(
                        rs.getString("dia_semana"),
                        rs.getTime("hora_inicio").toLocalTime(),
                        rs.getTime("hora_fin").toLocalTime()
                    ));
                }
            }
        }
        return horarios;
    }

    public Estilista save(Estilista estilista) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (nombre, email, telefono, password, id_rol) VALUES (?, ?, ?, ?, ?)";
        String sqlEmpleado = "INSERT INTO empleado (id_usuario, nombre, telefono, imagen_perfil, puesto) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false);
                
                try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    stmtUsuario.setString(1, estilista.getNombre());
                    stmtUsuario.setString(2, estilista.getEmail());
                    stmtUsuario.setString(3, estilista.getTelefono());
                    stmtUsuario.setString(4, estilista.getPassword());
                    stmtUsuario.setInt(5, 2); // Rol Estilista
                    stmtUsuario.executeUpdate();
                    try (ResultSet rs = stmtUsuario.getGeneratedKeys()) {
                        if (rs.next()) estilista.setIdUsuario(rs.getInt(1));
                        else throw new SQLException("No se generó id_usuario");
                    }
                }

                try (PreparedStatement stmtEmpleado = conn.prepareStatement(sqlEmpleado, Statement.RETURN_GENERATED_KEYS)) {
                    stmtEmpleado.setInt(1, estilista.getIdUsuario());
                    stmtEmpleado.setString(2, estilista.getNombre());
                    stmtEmpleado.setString(3, estilista.getTelefono());
                    stmtEmpleado.setString(4, estilista.getImagenPerfil());
                    stmtEmpleado.setString(5, estilista.getPuesto());
                    stmtEmpleado.executeUpdate();
                    try (ResultSet rs = stmtEmpleado.getGeneratedKeys()) {
                        if (rs.next()) estilista.setIdEmpleado(rs.getInt(1));
                    }
                }
                
                conn.commit();
                return estilista;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean update(Estilista estilista) throws SQLException {
        String sql = "UPDATE empleado SET nombre = ?, telefono = ?, imagen_perfil = ?, puesto = ? WHERE id_empleado = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estilista.getNombre());
            stmt.setString(2, estilista.getTelefono());
            stmt.setString(3, estilista.getImagenPerfil());
            stmt.setString(4, estilista.getPuesto());
            stmt.setInt(5, estilista.getIdEmpleado());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean hasCitas(int idEstilista) throws SQLException {
        String sql = "SELECT 1 FROM cita WHERE id_estilista = ? AND estado_cita IN ('PENDIENTE', 'CONFIRMADA') LIMIT 1";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstilista);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM empleado WHERE id_empleado = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<EstilistaDTO> findEstilistasByServicio(int idServicio) throws SQLException {
        List<EstilistaDTO> estilistas = new ArrayList<>();
        String sql = "SELECT e.id_empleado, e.nombre, e.puesto AS especialidad, u.email, e.activo " +
                     "FROM empleado e " +
                     "JOIN usuario u ON e.id_usuario = u.id_usuario " +
                     "JOIN estilista_servicio es ON e.id_empleado = es.id_estilista " +
                     "WHERE es.id_servicio = ? AND e.activo = TRUE AND u.id_rol = 2";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idServicio);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EstilistaDTO dto = new EstilistaDTO();
                    dto.setIdEstilista(rs.getInt("id_empleado"));
                    dto.setNombre(rs.getString("nombre"));
                    dto.setEspecialidad(rs.getString("especialidad"));
                    dto.setEmail(rs.getString("email"));
                    dto.setActivo(rs.getBoolean("activo"));
                    estilistas.add(dto);
                }
            }
        }
        return estilistas;
    }
}
