package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Categoria;
import org.pi.Models.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository {

    private Categoria mapResultSetToCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria"));
        categoria.setNombreCategoria(rs.getString("nombre_categoria"));
        categoria.setDescripcion(rs.getString("descripcion"));
        categoria.setActivo(rs.getBoolean("activo"));
        return categoria;
    }

    public List<Categoria> findAll() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria WHERE activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categorias.add(mapResultSetToCategoria(rs));
            }
        }
        return categorias;
    }

    public Categoria findById(int id) throws SQLException {
        String sql = "SELECT * FROM categoria WHERE id_categoria = ? AND activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategoria(rs);
                }
            }
        }
        return null;
    }

    public List<Servicio> findServiciosByCategoriaId(int idCategoria) throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT id_servicio, nombre_servicio, precio FROM servicio WHERE id_categoria = ? AND activo = TRUE";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCategoria);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Servicio servicio = new Servicio();
                    servicio.setIdServicio(rs.getInt("id_servicio"));
                    servicio.setNombreServicio(rs.getString("nombre_servicio"));
                    servicio.setPrecio(rs.getDouble("precio"));
                    servicios.add(servicio);
                }
            }
        }
        return servicios;
    }

    public Categoria save(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categoria(nombre_categoria, descripcion, activo) VALUES(?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNombreCategoria());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setBoolean(3, categoria.isActivo());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        categoria.setIdCategoria(rs.getInt(1));
                        return categoria;
                    }
                }
            }
            throw new SQLException("No se pudo guardar la categoría.");
        }
    }

    public boolean update(Categoria categoria) throws SQLException {
        String sql = "UPDATE categoria SET nombre_categoria = ?, descripcion = ? WHERE id_categoria = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNombreCategoria());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setInt(3, categoria.getIdCategoria());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean softDelete(int id) throws SQLException {
        String sql = "UPDATE categoria SET activo = FALSE WHERE id_categoria = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
