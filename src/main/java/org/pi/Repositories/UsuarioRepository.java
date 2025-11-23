package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<Integer> findAdminAndEstilistaIds(int idEstilista) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_usuario FROM usuario WHERE id_rol = 1 " + // Admins
                     "UNION " +
                     "SELECT id_usuario FROM empleado WHERE id_empleado = ?"; // Estilista
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstilista);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_usuario"));
                }
            }
        }
        return ids;
    }

    public Usuario findUserById(int id) throws SQLException {
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

    public Usuario saveUser(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario(nombre, email, telefono, password, id_rol) VALUES(?,?,?,?,?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet claves = null;

        try {
            conn = DBconfig.getDataSource().getConnection();
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

            conn.commit();
            return usuario;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (claves != null) try { claves.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean updateUser(Usuario usuario) throws SQLException {
        // ✅ CORRECCIÓN: Se agregó "password = ?" a la consulta SQL
        String sql = "UPDATE usuario SET nombre = ?, email = ?, telefono = ?, password = ?, id_rol = ? WHERE id_usuario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefono());
            stmt.setString(4, usuario.getPassword()); // ✅ Se envía la contraseña
            stmt.setInt(5, usuario.getIdRol());
            stmt.setInt(6, usuario.getIdUsuario());
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
