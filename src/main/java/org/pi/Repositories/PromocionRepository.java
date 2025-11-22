package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Promocion;
import org.pi.Models.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromocionRepository {

    private Promocion mapResultSetToPromocion(ResultSet rs) throws SQLException {
        Promocion promo = new Promocion();
        promo.setIdPromocion(rs.getInt("id_promocion"));
        promo.setNombrePromocion(rs.getString("nombre_promocion"));
        promo.setTipoDescuento(rs.getString("tipo_descuento"));
        promo.setDescuento(rs.getDouble("valor_descuento")); // Corregido
        promo.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        promo.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
        return promo;
    }

    public List<Promocion> findAll() throws SQLException {
        List<Promocion> promociones = new ArrayList<>();
        String sql = "SELECT * FROM promocion";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                promociones.add(mapResultSetToPromocion(rs));
            }
        }
        return promociones;
    }

    public Promocion findById(int id) throws SQLException {
        String sql = "SELECT * FROM promocion WHERE id_promocion = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPromocion(rs);
                }
            }
        }
        return null;
    }

    public Promocion save(Promocion promo) throws SQLException {
        String sql = "INSERT INTO promocion (nombre_promocion, tipo_descuento, valor_descuento, fecha_inicio, fecha_fin) VALUES (?,?,?,?,?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, promo.getNombrePromocion());
            stmt.setString(2, promo.getTipoDescuento());
            stmt.setDouble(3, promo.getDescuento());
            stmt.setDate(4, Date.valueOf(promo.getFechaInicio()));
            stmt.setDate(5, Date.valueOf(promo.getFechaFin()));
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        promo.setIdPromocion(rs.getInt(1));
                        return promo;
                    }
                }
            }
            throw new SQLException("No se pudo guardar la promoción.");
        }
    }

    public boolean update(Promocion promo) throws SQLException {
        String sql = "UPDATE promocion SET nombre_promocion = ?, valor_descuento = ?, fecha_fin = ? WHERE id_promocion = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, promo.getNombrePromocion());
            stmt.setDouble(2, promo.getDescuento());
            stmt.setDate(3, Date.valueOf(promo.getFechaFin()));
            stmt.setInt(4, promo.getIdPromocion());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM promocion WHERE id_promocion = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean addServicioToPromocion(int idPromocion, int idServicio) throws SQLException {
        String sql = "INSERT INTO servicio_promocion (id_promocion, id_servicio) VALUES (?, ?)";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPromocion);
            stmt.setInt(2, idServicio);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Servicio> findServiciosByPromocionId(int idPromocion) throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT s.* FROM servicio s JOIN servicio_promocion sp ON s.id_servicio = sp.id_servicio WHERE sp.id_promocion = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPromocion);
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
}
