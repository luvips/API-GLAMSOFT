package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Formulario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormularioRepository {

    private Formulario mapResultSetToFormulario(ResultSet rs) throws SQLException {
        Formulario formulario = new Formulario();
        formulario.setIdFormulario(rs.getInt("id_formulario"));
        formulario.setNombreFormulario(rs.getString("nombre_formulario"));
        formulario.setDescripcion(rs.getString("descripcion"));
        formulario.setActivo(rs.getBoolean("activo"));
        return formulario;
    }

    public List<Formulario> findAll() throws SQLException {
        List<Formulario> formularios = new ArrayList<>();
        String sql = "SELECT * FROM formulario WHERE activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                formularios.add(mapResultSetToFormulario(rs));
            }
        }
        return formularios;
    }

    public Formulario findById(int id) throws SQLException {
        String sql = "SELECT * FROM formulario WHERE id_formulario = ? AND activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFormulario(rs);
                }
            }
        }
        return null;
    }

    public Formulario save(Formulario formulario) throws SQLException {
        String sql = "INSERT INTO formulario(nombre_formulario, descripcion) VALUES(?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, formulario.getNombreFormulario());
            stmt.setString(2, formulario.getDescripcion());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        formulario.setIdFormulario(rs.getInt(1));
                        return formulario;
                    }
                }
            }
            throw new SQLException("No se pudo guardar el formulario.");
        }
    }

    public boolean update(Formulario formulario) throws SQLException {
        String sql = "UPDATE formulario SET nombre_formulario = ?, descripcion = ? WHERE id_formulario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, formulario.getNombreFormulario());
            stmt.setString(2, formulario.getDescripcion());
            stmt.setInt(3, formulario.getIdFormulario());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean softDelete(int id) throws SQLException {
        String sql = "UPDATE formulario SET activo = FALSE WHERE id_formulario = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
