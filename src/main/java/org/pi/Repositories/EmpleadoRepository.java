package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoRepository {

    public List<Empleado> findAll() throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT e.id_empleado, u.email, e.nombre, e.telefono, e.imagen_perfil " +
                     "FROM empleado e JOIN usuario u ON e.id_usuario = u.id_usuario";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Empleado empleado = new Empleado();
                empleado.setIdEmpleado(rs.getInt("id_empleado"));
                empleado.setEmail(rs.getString("email"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setTelefono(rs.getString("telefono"));
                empleado.setImagenPerfil(rs.getString("imagen_perfil"));
                empleados.add(empleado);
            }
        }
        return empleados;
    }

    public Empleado findById(int id) throws SQLException {
        Empleado empleado = null;
        String sql = "SELECT e.id_empleado, u.id_usuario, u.email, e.nombre, e.telefono, e.imagen_perfil " +
                     "FROM empleado e JOIN usuario u ON e.id_usuario = u.id_usuario WHERE e.id_empleado = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    empleado = new Empleado();
                    empleado.setIdEmpleado(rs.getInt("id_empleado"));
                    empleado.setIdUsuario(rs.getInt("id_usuario"));
                    empleado.setEmail(rs.getString("email"));
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setTelefono(rs.getString("telefono"));
                    empleado.setImagenPerfil(rs.getString("imagen_perfil"));
                }
            }
        }
        return empleado;
    }

    public Empleado save(Empleado empleado) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (email, password, id_rol) VALUES (?, ?, ?)";
        String sqlEmpleado = "INSERT INTO empleado (nombre, telefono, imagen_perfil, id_usuario) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false); // Iniciar transacción

                // Insertar en usuario
                int idUsuario;
                try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    stmtUsuario.setString(1, empleado.getEmail());
                    stmtUsuario.setString(2, empleado.getPassword());
                    stmtUsuario.setInt(3, empleado.getIdRol());
                    stmtUsuario.executeUpdate();
                    try (ResultSet rs = stmtUsuario.getGeneratedKeys()) {
                        if (rs.next()) {
                            idUsuario = rs.getInt(1);
                            empleado.setIdUsuario(idUsuario);
                        } else {
                            throw new SQLException("No se pudo obtener el ID del usuario creado.");
                        }
                    }
                }

                // Insertar en empleado
                try (PreparedStatement stmtEmpleado = conn.prepareStatement(sqlEmpleado, Statement.RETURN_GENERATED_KEYS)) {
                    stmtEmpleado.setString(1, empleado.getNombre());
                    stmtEmpleado.setString(2, empleado.getTelefono());
                    stmtEmpleado.setString(3, empleado.getImagenPerfil());
                    stmtEmpleado.setInt(4, idUsuario);
                    stmtEmpleado.executeUpdate();
                    try (ResultSet rs = stmtEmpleado.getGeneratedKeys()) {
                        if (rs.next()) {
                            empleado.setIdEmpleado(rs.getInt(1));
                        } else {
                            throw new SQLException("No se pudo obtener el ID del empleado creado.");
                        }
                    }
                }

                conn.commit(); // Finalizar transacción
                return empleado;
            } catch (SQLException e) {
                conn.rollback(); // Revertir en caso de error
                throw e;
            }
        } // try-with-resources se encarga de cerrar la conexión correctamente
    }

    public boolean update(Empleado empleado) throws SQLException {
        String sql = "UPDATE empleado SET nombre = ?, telefono = ?, imagen_perfil = ? WHERE id_empleado = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getTelefono());
            stmt.setString(3, empleado.getImagenPerfil());
            stmt.setInt(4, empleado.getIdEmpleado());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int idEmpleado) throws SQLException {
        String sql = "DELETE FROM empleado WHERE id_empleado = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmpleado);
            return stmt.executeUpdate() > 0;
        }
    }
}
