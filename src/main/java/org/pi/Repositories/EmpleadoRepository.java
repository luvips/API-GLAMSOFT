package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoRepository {

    private Empleado mapResultSetToEmpleado(ResultSet rs) throws SQLException {
        Empleado emp = new Empleado();
        emp.setIdEmpleado(rs.getInt("id_empleado"));
        emp.setIdUsuario(rs.getInt("id_usuario"));
        emp.setPuesto(rs.getString("puesto"));
        emp.setImagenPerfil(rs.getString("imagen_perfil"));

        // Mapeamos datos redundantes
        try { emp.setNombre(rs.getString("nombre")); } catch (SQLException e) {}
        try { emp.setTelefono(rs.getString("telefono")); } catch (SQLException e) {}

        // Intentamos obtener email del JOIN con usuario si existe
        try { emp.setEmail(rs.getString("email")); } catch (SQLException e) {}
        try { emp.setIdRol(rs.getInt("id_rol")); } catch (SQLException e) {}

        return emp;
    }

    public List<Empleado> findAll() throws SQLException {
        String sql = "SELECT e.*, u.email, u.id_rol " +
                "FROM empleado e " +
                "LEFT JOIN usuario u ON e.id_usuario = u.id_usuario";
        List<Empleado> empleados = new ArrayList<>();
        try (Connection conn = DBconfig.getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                empleados.add(mapResultSetToEmpleado(rs));
            }
        }
        return empleados;
    }

    public Empleado findById(int id) throws SQLException {
        String sql = "SELECT e.*, u.email, u.id_rol FROM empleado e " +
                "LEFT JOIN usuario u ON e.id_usuario = u.id_usuario " +
                "WHERE e.id_empleado = ?";
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

    public List<Empleado> findByRol(int idRol) throws SQLException {
        String sql = "SELECT e.*, u.email, u.id_rol FROM empleado e " +
                "JOIN usuario u ON e.id_usuario = u.id_usuario " +
                "WHERE u.id_rol = ?";
        List<Empleado> empleados = new ArrayList<>();
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRol);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    empleados.add(mapResultSetToEmpleado(rs));
                }
            }
        }
        return empleados;
    }

    // ✅ MÉTODO CORREGIDO: Insertamos id_usuario, puesto, imagen, nombre y telefono. SIN EMAIL.
    public Empleado save(Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleado (id_usuario, puesto, imagen_perfil, nombre, telefono) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, empleado.getIdUsuario());
            stmt.setString(2, empleado.getPuesto());
            stmt.setString(3, empleado.getImagenPerfil());
            // Datos redundantes requeridos por la tabla empleado (según bd.sql)
            stmt.setString(4, empleado.getNombre());
            stmt.setString(5, empleado.getTelefono());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear el registro de empleado.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    empleado.setIdEmpleado(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("No se obtuvo ID para el empleado.");
                }
            }
        }
        return empleado;
    }

    public boolean update(Empleado empleado) throws SQLException {
        // Actualizamos solo los datos propios de la tabla empleado
        String sql = "UPDATE empleado SET puesto = ?, imagen_perfil = ?, nombre = ?, telefono = ? WHERE id_usuario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, empleado.getPuesto());
            stmt.setString(2, empleado.getImagenPerfil());
            stmt.setString(3, empleado.getNombre());
            stmt.setString(4, empleado.getTelefono());
            stmt.setInt(5, empleado.getIdUsuario());
            return stmt.executeUpdate() > 0;
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
}