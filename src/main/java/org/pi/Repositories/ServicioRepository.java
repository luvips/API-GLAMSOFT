package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioRepository {

    public List<Servicio> findAll() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicio";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                servicios.add(mapResultSetToServicio(rs));
            }
        }
        return servicios;
    }

    public Servicio findById(int id) throws SQLException {
        Servicio servicio = null;
        String sql = "SELECT * FROM servicio WHERE id_servicio = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    servicio = mapResultSetToServicio(rs);
                }
            }
        }
        return servicio;
    }

    public List<Servicio> findByCategoria(int idCategoria) throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicio WHERE id_categoria = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCategoria);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    servicios.add(mapResultSetToServicio(rs));
                }
            }
        }
        return servicios;
    }

    public int save(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO servicio(imagen, nombre_servicio, duracion_minutos, precio, descripcion, id_categoria, id_formulario) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, servicio.getImagenURL());
            stmt.setString(2, servicio.getNombreServicio());
            stmt.setInt(3, servicio.getDuracionMinutos());
            stmt.setDouble(4, servicio.getPrecio());
            stmt.setString(5, servicio.getDescripcion());
            stmt.setInt(6, servicio.getIdCategoria());

            if (servicio.getIdFormulario() == null) { // Condición corregida
                stmt.setNull(7, Types.INTEGER);
            } else {
                stmt.setInt(7, servicio.getIdFormulario());
            }

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("La inserción de servicio falló, ninguna fila afectada.");
            }
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                } else {
                    throw new SQLException("No se encontró id generado para servicio.");
                }
            }
        }
    }

    public boolean update(Servicio servicio) throws SQLException {
        String sql = "UPDATE servicio SET imagen = ?, nombre_servicio = ?, duracion_minutos = ?, precio = ?, descripcion = ?, id_categoria = ?, id_formulario = ? WHERE id_servicio = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, servicio.getImagenURL());
            stmt.setString(2, servicio.getNombreServicio());
            stmt.setInt(3, servicio.getDuracionMinutos());
            stmt.setDouble(4, servicio.getPrecio());
            stmt.setString(5, servicio.getDescripcion());
            stmt.setInt(6, servicio.getIdCategoria());

            if (servicio.getIdFormulario() == null) { // Condición corregida
                stmt.setNull(7, Types.INTEGER);
            } else {
                stmt.setInt(7, servicio.getIdFormulario());
            }
            stmt.setInt(8, servicio.getIdServicio());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM servicio WHERE id_servicio = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Servicio mapResultSetToServicio(ResultSet rs) throws SQLException {
        int idServicio = rs.getInt("id_servicio");
        String imagen = rs.getString("imagen");
        String nombreServicio = rs.getString("nombre_servicio");
        int duracionMinutos = rs.getInt("duracion_minutos");
        double precio = rs.getDouble("precio");
        String descripcion = rs.getString("descripcion");
        int categoriaId = rs.getInt("id_categoria");
        // Usar getObject para manejar posibles nulos de la DB de forma segura
        Integer formularioId = (Integer) rs.getObject("id_formulario"); 
        return new Servicio(idServicio, imagen, nombreServicio, duracionMinutos, precio, descripcion, categoriaId, formularioId);
    }
}
