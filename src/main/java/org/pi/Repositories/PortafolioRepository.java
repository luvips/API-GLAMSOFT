package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Portafolio;
import org.pi.dto.PortafolioDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PortafolioRepository {

    private PortafolioDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        PortafolioDTO dto = new PortafolioDTO();
        // CAMBIO: setIdPortafolio -> setIdImagen
        dto.setIdImagen(rs.getInt("id_imagen"));
        dto.setTitulo(rs.getString("titulo"));
        dto.setDescripcion(rs.getString("descripcion"));
        dto.setUrlImagen(rs.getString("url"));
        dto.setFecha(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        dto.setDestacado(rs.getBoolean("destacado"));

        if (hasColumn(rs, "nombre_categoria")) {
            dto.setCategoria(rs.getString("nombre_categoria"));
        }
        if (hasColumn(rs, "visitas")) {
            dto.setVisitas(rs.getInt("visitas"));
        }
        return dto;
    }

    public List<PortafolioDTO> findAll() throws SQLException {
        List<PortafolioDTO> portafolios = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre_categoria FROM portafolio p " +
                     "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
                     "ORDER BY p.fecha_creacion DESC";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                portafolios.add(mapResultSetToDTO(rs));
            }
        }
        return portafolios;
    }

    public PortafolioDTO findById(int id) throws SQLException {
        String sql = "SELECT p.*, c.nombre_categoria FROM portafolio p " +
                     "LEFT JOIN categoria c ON p.id_categoria = c.id_categoria " +
                     "WHERE p.id_imagen = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        }
        return null;
    }

    public Portafolio save(Portafolio portafolio) throws SQLException {
        String sql = "INSERT INTO portafolio(titulo, descripcion, url, id_categoria, destacado) VALUES(?,?,?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, portafolio.getTitulo());
            stmt.setString(2, portafolio.getDescripcion());
            stmt.setString(3, portafolio.getUrl());
            stmt.setObject(4, portafolio.getIdCategoria()); // setObject para manejar nulos
            stmt.setBoolean(5, portafolio.isDestacado());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        portafolio.setIdImagen(rs.getInt(1));
                        return portafolio;
                    }
                }
            }
            throw new SQLException("No se pudo guardar la entrada del portafolio.");
        }
    }

    public boolean update(Portafolio portafolio) throws SQLException {
        // CORRECCIÓN: Agregar descripcion, url, e id_categoria al SQL
        String sql = "UPDATE portafolio SET titulo = ?, descripcion = ?, url = ?, id_categoria = ?, destacado = ? WHERE id_imagen = ?";

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, portafolio.getTitulo());
            stmt.setString(2, portafolio.getDescripcion());
            stmt.setString(3, portafolio.getUrl());
            // Usar setObject para manejar posibles nulos en categoría, aunque tu front envía 1 por defecto
            stmt.setObject(4, portafolio.getIdCategoria());
            stmt.setBoolean(5, portafolio.isDestacado());
            stmt.setInt(6, portafolio.getIdImagen());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM portafolio WHERE id_imagen = ?";
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
