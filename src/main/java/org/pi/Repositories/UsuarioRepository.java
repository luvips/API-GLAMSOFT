package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Usuario;

import java.sql.*;

public class UsuarioRepository {

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("telefono"),
                rs.getString("password"),
                rs.getInt("id_rol"),
                rs.getBoolean("activo")
        );
    }

    public Usuario findUserById(int id) throws SQLException {
        // Buscamos por ID sin importar si está activo (para admins)
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    public Usuario findUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    public Usuario findUserByTelefono(String telefono) throws SQLException {
        // Para login/registro validamos que no exista, o recuperamos activo
        String sql = "SELECT * FROM usuario WHERE telefono = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefono);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    // ✅ MÉTODO CORREGIDO CON TRANSACCIÓN
    public Usuario saveUser(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario(nombre, email, telefono, password, id_rol) VALUES(?,?,?,?,?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet claves = null;

        try {
            conn = DBconfig.getDataSource().getConnection();
            // 1. Iniciar Transacción
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefono());
            stmt.setString(4, usuario.getPassword());
            stmt.setInt(5, usuario.getIdRol());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo insertar el registro de usuario.");
            }

            claves = stmt.getGeneratedKeys();
            if (claves.next()) {
                usuario.setIdUsuario(claves.getInt(1));
            } else {
                throw new SQLException("No se encontró el ID generado para el usuario.");
            }

            // 2. Confirmar Transacción (Todo salió bien)
            conn.commit();
            return usuario;

        } catch (SQLException e) {
            // 3. Deshacer cambios si algo falla (Rollback)
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Relanzar el error original
        } finally {
            // 4. Cerrar recursos manualmente
            if (claves != null) try { claves.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar estado por defecto
                    conn.close();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean updateUser(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, email = ?, telefono = ?, id_rol = ? WHERE id_usuario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefono());
            stmt.setInt(4, usuario.getIdRol());
            stmt.setInt(5, usuario.getIdUsuario());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean softDeleteUser(int id) throws SQLException {
        String sql = "UPDATE usuario SET activo = FALSE WHERE id_usuario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}