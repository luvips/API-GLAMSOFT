package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Servicio;
import org.pi.dto.ServicioDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioRepository {

    private ServicioDTO mapResultSetToServicioDTO(ResultSet rs) throws SQLException {
        ServicioDTO dto = new ServicioDTO();
        dto.setIdServicio(rs.getInt("id_servicio"));
        dto.setNombre(rs.getString("nombre_servicio"));
        dto.setDescripcion(rs.getString("descripcion"));
        dto.setPrecio(rs.getDouble("precio"));
        dto.setDuracion(rs.getInt("duracion_minutos"));
        dto.setActivo(rs.getBoolean("activo"));
        dto.setImagenURL(rs.getString("imagen")); // Mapeo añadido

        // Estos campos pueden no estar en todas las consultas
        if (hasColumn(rs, "nombre_categoria")) {
            dto.setCategoria(rs.getString("nombre_categoria"));
        }
        if (hasColumn(rs, "valoracion_promedio")) {
            dto.setValoracionPromedio(rs.getDouble("valoracion_promedio"));
        }
        if (hasColumn(rs, "total_valoraciones")) {
            dto.setTotalValoraciones(rs.getInt("total_valoraciones"));
        }
        return dto;
    }

    public List<ServicioDTO> findAll(String categoria, Boolean activo) throws SQLException {
        List<ServicioDTO> servicios = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT s.*, c.nombre_categoria " +
            "FROM servicio s " +
            "JOIN categoria c ON s.id_categoria = c.id_categoria " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (categoria != null && !categoria.isEmpty()) {
            sql.append("AND c.nombre_categoria = ? ");
            params.add(categoria);
        }
        if (activo != null) {
            sql.append("AND s.activo = ? ");
            params.add(activo);
        }

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    servicios.add(mapResultSetToServicioDTO(rs));
                }
            }
        }
        return servicios;
    }

    public ServicioDTO findById(int id) throws SQLException {
        String sql = "SELECT s.*, c.nombre_categoria, " +
                     "COALESCE(AVG(v.puntuacion), 0) AS valoracion_promedio, " +
                     "COUNT(v.id_valoracion) AS total_valoraciones " +
                     "FROM servicio s " +
                     "JOIN categoria c ON s.id_categoria = c.id_categoria " +
                     "LEFT JOIN valoracion v ON s.id_servicio = v.id_servicio " +
                     "WHERE s.id_servicio = ? " +
                     "GROUP BY s.id_servicio, c.nombre_categoria";

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToServicioDTO(rs);
                }
            }
        }
        return null;
    }

    public Servicio save(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO servicio(nombre_servicio, descripcion, precio, duracion_minutos, id_categoria, activo, imagen) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, servicio.getNombreServicio());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setDouble(3, servicio.getPrecio());
            stmt.setInt(4, servicio.getDuracionMinutos());
            stmt.setInt(5, servicio.getIdCategoria());
            stmt.setBoolean(6, servicio.isActivo());
            stmt.setString(7, servicio.getImagenURL());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        servicio.setIdServicio(rs.getInt(1));
                        return servicio;
                    }
                }
            }
            throw new SQLException("No se pudo guardar el servicio.");
        }
    }

    public boolean update(Servicio servicio) throws SQLException {
        String sql = "UPDATE servicio SET nombre_servicio = ?, descripcion = ?, precio = ?, duracion_minutos = ?, id_categoria = ?, activo = ?, imagen = ? WHERE id_servicio = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, servicio.getNombreServicio());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setDouble(3, servicio.getPrecio());
            stmt.setInt(4, servicio.getDuracionMinutos());
            stmt.setInt(5, servicio.getIdCategoria());
            stmt.setBoolean(6, servicio.isActivo());
            stmt.setString(7, servicio.getImagenURL());
            stmt.setInt(8, servicio.getIdServicio());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean softDelete(int id) throws SQLException {
        String sql = "UPDATE servicio SET activo = FALSE WHERE id_servicio = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equalsIgnoreCase(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }
}
