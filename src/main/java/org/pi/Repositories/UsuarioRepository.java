package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Empleado;
import org.pi.Models.Usuario;

import java.sql.*;

public class UsuarioRepository {

    public Usuario findUserById(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        Usuario user = null;
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new Usuario();
                    user.setIdUsuario(rs.getInt("id_usuario"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setIdRol(rs.getInt("id_rol"));
                }
            }
        }
        return user;
    }

    public Usuario findUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        Usuario user = null;
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new Usuario();
                    user.setIdUsuario(rs.getInt("id_usuario"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setIdRol(rs.getInt("id_rol"));
                }
            }
        }
        return user;
    }

    public int saveUser(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario(email, password, id_rol) VALUES(?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getPassword());
            stmt.setInt(3, usuario.getIdRol());
            
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo insertar el registro de usuario.");
            }
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                } else {
                    throw new SQLException("No se encontró el ID generado para el usuario.");
                }
            }
        }
    }

    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateUser(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET email = ?, password = ? WHERE id_usuario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getPassword());
            stmt.setInt(3, usuario.getIdUsuario());
            return stmt.executeUpdate() > 0;
        }
    }
    
    // --- Métodos complejos que mantienen su propia transacción ---

    public int saveEmpleadoCompleto(Empleado empleado) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (email, password, id_rol) VALUES (?, ?, ?)";
        String sqlEmpleado = "INSERT INTO empleado (nombre, telefono, imagen_perfil, id_usuario) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false);
                int idUsuario;
                try (PreparedStatement stmt = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, empleado.getEmail());
                    stmt.setString(2, empleado.getPassword());
                    stmt.setInt(3, empleado.getIdRol());
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("No se generó id_usuario");
                        idUsuario = rs.getInt(1);
                    }
                }
                try (PreparedStatement stmt2 = conn.prepareStatement(sqlEmpleado)) {
                    stmt2.setString(1, empleado.getNombre());
                    stmt2.setString(2, empleado.getTelefono());
                    stmt2.setString(3, empleado.getImagenPerfil());
                    stmt2.setInt(4, idUsuario);
                    stmt2.executeUpdate();
                }
                conn.commit();
                return idUsuario;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void updateEmpleado(Usuario usuario, Empleado empleado) throws SQLException {
        String sqlUsuario = "UPDATE usuario SET email = ?, password = ? WHERE id_usuario = ?";
        String sqlEmpleado = "UPDATE empleado SET nombre = ?, telefono = ? WHERE id_empleado = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement stmt = conn.prepareStatement(sqlUsuario)) {
                    stmt.setString(1, usuario.getEmail());
                    stmt.setString(2, usuario.getPassword());
                    stmt.setInt(3, usuario.getIdUsuario());
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt2 = conn.prepareStatement(sqlEmpleado)) {
                    stmt2.setString(1, empleado.getNombre());
                    stmt2.setString(2, empleado.getTelefono());
                    stmt2.setInt(3, empleado.getIdEmpleado());
                    stmt2.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
