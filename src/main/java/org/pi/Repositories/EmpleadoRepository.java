package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Empleado;
import org.pi.Models.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoRepository {

    private Empleado mapResultSetToEmpleado(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();
        // Datos de Usuario
        empleado.setIdUsuario(rs.getInt("id_usuario"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setTelefono(rs.getString("telefono"));
        empleado.setEmail(rs.getString("email"));
        empleado.setIdRol(rs.getInt("id_rol"));
        empleado.setActivo(rs.getBoolean("activo"));
        
        // Datos de Empleado
        empleado.setIdEmpleado(rs.getInt("id_empleado"));
        empleado.setPuesto(rs.getString("puesto"));
        empleado.setImagenPerfil(rs.getString("imagen_perfil"));
        
        return empleado;
    }

    public List<Empleado> findAll() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT u.*, e.id_empleado, e.puesto, e.imagen_perfil, e.fecha_creacion " +
                     "FROM empleado e JOIN usuario u ON e.id_usuario = u.id_usuario " +
                     "WHERE e.activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                empleados.add(mapResultSetToEmpleado(rs));
            }
        }
        return empleados;
    }

    public Empleado findById(int id) throws SQLException {
        String sql = "SELECT u.*, e.id_empleado, e.puesto, e.imagen_perfil, e.fecha_creacion " +
                     "FROM empleado e JOIN usuario u ON e.id_usuario = u.id_usuario " +
                     "WHERE e.id_empleado = ? AND e.activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmpleado(rs);
                }
            }
        }
        return null;
    }

    public Empleado save(Empleado empleado) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (nombre, email, telefono, password, id_rol) VALUES (?, ?, ?, ?, ?)";
        String sqlEmpleado = "INSERT INTO empleado (id_usuario, nombre, telefono, imagen_perfil, puesto) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false);
                
                // 1. Crear Usuario
                try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    stmtUsuario.setString(1, empleado.getNombre());
                    stmtUsuario.setString(2, empleado.getEmail());
                    stmtUsuario.setString(3, empleado.getTelefono());
                    stmtUsuario.setString(4, empleado.getPassword());
                    stmtUsuario.setInt(5, empleado.getIdRol());
                    stmtUsuario.executeUpdate();
                    try (ResultSet rs = stmtUsuario.getGeneratedKeys()) {
                        if (rs.next()) empleado.setIdUsuario(rs.getInt(1));
                        else throw new SQLException("No se generó id_usuario");
                    }
                }

                // 2. Crear Empleado
                try (PreparedStatement stmtEmpleado = conn.prepareStatement(sqlEmpleado, Statement.RETURN_GENERATED_KEYS)) {
                    stmtEmpleado.setInt(1, empleado.getIdUsuario());
                    stmtEmpleado.setString(2, empleado.getNombre());
                    stmtEmpleado.setString(3, empleado.getTelefono());
                    stmtEmpleado.setString(4, empleado.getImagenPerfil());
                    stmtEmpleado.setString(5, empleado.getPuesto());
                    stmtEmpleado.executeUpdate();
                    try (ResultSet rs = stmtEmpleado.getGeneratedKeys()) {
                        if (rs.next()) empleado.setIdEmpleado(rs.getInt(1));
                    }
                }
                
                conn.commit();
                return empleado;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean update(Empleado empleado) throws SQLException {
        String sql = "UPDATE empleado SET puesto = ? WHERE id_empleado = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, empleado.getPuesto());
            stmt.setInt(2, empleado.getIdEmpleado());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean softDelete(int id) throws SQLException {
        // Desactiva tanto al empleado como a su usuario asociado
        String sqlEmpleado = "UPDATE empleado SET activo = FALSE WHERE id_empleado = ?";
        String sqlUsuario = "UPDATE usuario SET activo = FALSE WHERE id_usuario = (SELECT id_usuario FROM empleado WHERE id_empleado = ?)";
        
        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false);
                
                int affectedRows;
                try (PreparedStatement stmtEmpleado = conn.prepareStatement(sqlEmpleado)) {
                    stmtEmpleado.setInt(1, id);
                    affectedRows = stmtEmpleado.executeUpdate();
                }
                try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario)) {
                    stmtUsuario.setInt(1, id);
                    stmtUsuario.executeUpdate();
                }
                
                conn.commit();
                return affectedRows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
