package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.*;
import org.pi.dto.EstilistaDTO;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EstilistaRepository {

    // --- MÉTODOS DE LECTURA ---

    public List<EstilistaDTO> findAllEstilistas() throws SQLException {
        List<EstilistaDTO> estilistas = new ArrayList<>();
        String sql = "SELECT e.id_empleado, e.nombre, e.telefono, u.email AS email_usuario, " +
                     "GROUP_CONCAT(DISTINCT s.nombre_servicio SEPARATOR ', ') AS nombres_servicios, " +
                     "GROUP_CONCAT(DISTINCT CONCAT(h.dia_semana, ' ', TIME_FORMAT(h.hora_inicio, '%H:%i'), '-', TIME_FORMAT(h.hora_fin, '%H:%i')) SEPARATOR '; ') AS horarios_completos " +
                     "FROM empleado e " +
                     "JOIN usuario u ON e.id_usuario = u.id_usuario " +
                     "LEFT JOIN estilista_servicio es ON e.id_empleado = es.id_estilista " +
                     "LEFT JOIN servicio s ON es.id_servicio = s.id_servicio " +
                     "LEFT JOIN estilista_horario eh ON e.id_empleado = eh.id_estilista " +
                     "LEFT JOIN horario h ON eh.id_horario = h.id_horario " +
                     "WHERE u.id_rol = 3 " + // Asumiendo que el rol de estilista es 3
                     "GROUP BY e.id_empleado, e.nombre, e.telefono, u.email " +
                     "ORDER BY e.nombre";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                EstilistaDTO dto = new EstilistaDTO();
                dto.setIdEmpleado(rs.getInt("id_empleado"));
                dto.setNombre(rs.getString("nombre"));
                dto.setTelefono(rs.getString("telefono"));
                dto.setEmailUsuario(rs.getString("email_usuario"));
                dto.setServicios(rs.getString("nombres_servicios"));
                dto.setHorarios(rs.getString("horarios_completos"));
                estilistas.add(dto);
            }
        }
        return estilistas;
    }

    public EstilistaDTO findEstilistaById(int id) throws SQLException {
        // Implementación existente...
        return null; // Placeholder
    }
    
    // --- MÉTODOS DE ESCRITURA (CRUD) ---

    public Estilista save(Estilista estilista) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (email, password, id_rol) VALUES (?, ?, ?)";
        String sqlEmpleado = "INSERT INTO empleado (nombre, telefono, imagen_perfil, id_usuario) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false);
                
                int idUsuario;
                try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    stmtUsuario.setString(1, estilista.getEmail());
                    stmtUsuario.setString(2, estilista.getPassword());
                    stmtUsuario.setInt(3, 3); // Rol de Estilista
                    stmtUsuario.executeUpdate();
                    try (ResultSet rs = stmtUsuario.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("No se generó id_usuario");
                        idUsuario = rs.getInt(1);
                        estilista.setIdUsuario(idUsuario);
                    }
                }

                try (PreparedStatement stmtEmpleado = conn.prepareStatement(sqlEmpleado, Statement.RETURN_GENERATED_KEYS)) {
                    stmtEmpleado.setString(1, estilista.getNombre());
                    stmtEmpleado.setString(2, estilista.getTelefono());
                    stmtEmpleado.setString(3, estilista.getImagenPerfil());
                    stmtEmpleado.setInt(4, idUsuario);
                    stmtEmpleado.executeUpdate();
                    try (ResultSet rs = stmtEmpleado.getGeneratedKeys()) {
                        if (rs.next()) {
                            estilista.setIdEmpleado(rs.getInt(1));
                        }
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
        String sql = "UPDATE empleado SET nombre = ?, telefono = ?, imagen_perfil = ? WHERE id_empleado = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estilista.getNombre());
            stmt.setString(2, estilista.getTelefono());
            stmt.setString(3, estilista.getImagenPerfil());
            stmt.setInt(4, estilista.getIdEmpleado());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        // Nota: Esto solo elimina el empleado, no el usuario asociado.
        // Una eliminación completa requeriría una lógica más compleja.
        String sql = "DELETE FROM empleado WHERE id_empleado = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    // --- MÉTODOS DE RELACIONES (ya existentes) ---
    // ... (findHorarios, findServicios, saveHorarios, saveServicios, etc.)
}
