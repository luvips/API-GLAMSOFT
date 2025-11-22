package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolRepository {

    private Rol mapResultSetToRol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setNombreRol(rs.getString("nombre_rol"));
        rol.setDescripcion(rs.getString("descripcion"));
        rol.setActivo(rs.getBoolean("activo"));
        return rol;
    }

    public List<Rol> findAll() throws SQLException {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT * FROM rol WHERE activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                roles.add(mapResultSetToRol(rs));
            }
        }
        return roles;
    }

    public Rol findById(int id) throws SQLException {
        String sql = "SELECT * FROM rol WHERE id_rol = ? AND activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRol(rs);
                }
            }
        }
        return null;
    }

    public Rol save(Rol rol) throws SQLException {
        String sql = "INSERT INTO rol(nombre_rol, descripcion, activo) VALUES(?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, rol.getNombreRol());
            stmt.setString(2, rol.getDescripcion());
            stmt.setBoolean(3, rol.isActivo());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        rol.setIdRol(rs.getInt(1));
                        return rol;
                    }
                }
            }
            throw new SQLException("No se pudo guardar el rol.");
        }
    }

    public boolean update(Rol rol) throws SQLException {
        String sql = "UPDATE rol SET nombre_rol = ?, descripcion = ? WHERE id_rol = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rol.getNombreRol());
            stmt.setString(2, rol.getDescripcion());
            stmt.setInt(3, rol.getIdRol());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean softDelete(int id) throws SQLException {
        String sql = "UPDATE rol SET activo = FALSE WHERE id_rol = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
