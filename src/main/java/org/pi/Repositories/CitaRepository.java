package org.pi.Repositories;

import org.pi.Config.DBconfig;
import org.pi.Models.Cita;
import org.pi.dto.CitaDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaRepository {

    public List<CitaDTO> findCitaCliente(int idCliente) throws SQLException {
        List<CitaDTO> citas = new ArrayList<>();
        String sql = "SELECT c.id_cita, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud, " +
                "GROUP_CONCAT(s.nombre_servicio SEPARATOR ', ') AS nombres_servicios, " +
                "SUM(s.precio) AS total_cita " +
                "FROM cita c " +
                "JOIN usuario u ON c.id_cliente = u.id_usuario " +
                "JOIN cita_servicio cs ON c.id_cita = cs.id_cita " +
                "JOIN servicio s ON cs.id_servicio = s.id_servicio " +
                "WHERE c.id_cliente = ? " +
                "GROUP BY c.id_cita, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud " +
                "ORDER BY c.fecha_hora_cita DESC";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CitaDTO dto = new CitaDTO();
                dto.setIdCita(rs.getInt("id_cita"));
                dto.setEstadoCita(rs.getString("estado_cita"));
                dto.setFechaCita(rs.getTimestamp("fecha_hora_cita").toLocalDateTime());
                dto.setFechaSolicitudCita(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
                dto.setNombresServicios(rs.getString("nombres_servicios"));
                dto.setPrecioTotal(rs.getDouble("total_cita"));
                citas.add(dto);
            }
        }
        return citas;
    }

    public List<CitaDTO> findCitasMes(int mes, int year) throws SQLException {
        List<CitaDTO> citas = new ArrayList<>();
        String sql = "SELECT c.id_cita, c.fecha_hora_cita, u.email AS cliente_email, " +
                "GROUP_CONCAT(s.nombre_servicio SEPARATOR ', ') AS nombres_servicios " +
                "FROM cita c " +
                "JOIN usuario u ON c.id_cliente = u.id_usuario " +
                "JOIN cita_servicio cs ON c.id_cita = cs.id_cita " +
                "JOIN servicio s ON cs.id_servicio = s.id_servicio " +
                "WHERE MONTH(c.fecha_hora_cita) = ? AND YEAR(c.fecha_hora_cita) = ? " +
                "GROUP BY c.id_cita, c.fecha_hora_cita, u.email " +
                "ORDER BY c.fecha_hora_cita DESC";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mes);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CitaDTO dto = new CitaDTO();
                dto.setIdCita(rs.getInt("id_cita"));
                dto.setFechaCita(rs.getTimestamp("fecha_hora_cita").toLocalDateTime());
                dto.setClienteEmail(rs.getString("cliente_email"));
                dto.setNombresServicios(rs.getString("nombres_servicios"));
                citas.add(dto);
            }
        }
        return citas;
    }

    public List<CitaDTO> findAllCitas() throws SQLException {
        List<CitaDTO> citas = new ArrayList<>();
        String sql = "SELECT c.id_cita, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud, " +
                "u.email AS cliente_email, e.nombre AS estilista_nombre, " +
                "GROUP_CONCAT(s.nombre_servicio SEPARATOR ', ') AS nombres_servicios " +
                "FROM cita c " +
                "JOIN usuario u ON c.id_cliente = u.id_usuario " +
                "JOIN empleado e ON c.id_estilista = e.id_empleado " +
                "JOIN cita_servicio cs ON c.id_cita = cs.id_cita " +
                "JOIN servicio s ON cs.id_servicio = s.id_servicio " +
                "GROUP BY c.id_cita, u.email, e.nombre, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud " +
                "ORDER BY c.fecha_hora_cita DESC";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CitaDTO dto = new CitaDTO();
                dto.setIdCita(rs.getInt("id_cita"));
                dto.setEstadoCita(rs.getString("estado_cita"));
                dto.setFechaCita(rs.getTimestamp("fecha_hora_cita").toLocalDateTime());
                dto.setFechaSolicitudCita(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
                dto.setClienteEmail(rs.getString("cliente_email"));
                dto.setEstilistaNombre(rs.getString("estilista_nombre"));
                dto.setNombresServicios(rs.getString("nombres_servicios"));
                citas.add(dto);
            }
        }
        return citas;
    }

    public CitaDTO findCitaById(int id) throws SQLException {
        CitaDTO cita = null;
        String sql = "SELECT c.id_cita, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud, " +
                "u.email AS cliente_email, e.nombre AS estilista_nombre, " +
                "GROUP_CONCAT(s.nombre_servicio SEPARATOR ', ') AS nombres_servicios " +
                "FROM cita c " +
                "JOIN usuario u ON c.id_cliente = u.id_usuario " +
                "JOIN empleado e ON c.id_estilista = e.id_empleado " +
                "JOIN cita_servicio cs ON c.id_cita = cs.id_cita " +
                "JOIN servicio s ON cs.id_servicio = s.id_servicio " +
                "WHERE c.id_cita = ? " +
                "GROUP BY c.id_cita, u.email, e.nombre, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cita = new CitaDTO();
                    cita.setIdCita(rs.getInt("id_cita"));
                    cita.setEstadoCita(rs.getString("estado_cita"));
                    cita.setFechaCita(rs.getTimestamp("fecha_hora_cita").toLocalDateTime());
                    cita.setFechaSolicitudCita(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
                    cita.setClienteEmail(rs.getString("cliente_email"));
                    cita.setEstilistaNombre(rs.getString("estilista_nombre"));
                    cita.setNombresServicios(rs.getString("nombres_servicios"));
                }
            }
        }
        return cita;
    }
    
    public List<CitaDTO> findByEstado(String estado) throws SQLException {
        List<CitaDTO> citas = new ArrayList<>();
        String sql = "SELECT c.id_cita, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud, " +
                "u.email AS cliente_email, e.nombre AS estilista_nombre, " +
                "GROUP_CONCAT(s.nombre_servicio SEPARATOR ', ') AS nombres_servicios " +
                "FROM cita c " +
                "JOIN usuario u ON c.id_cliente = u.id_usuario " +
                "JOIN empleado e ON c.id_estilista = e.id_empleado " +
                "JOIN cita_servicio cs ON c.id_cita = cs.id_cita " +
                "JOIN servicio s ON cs.id_servicio = s.id_servicio " +
                "WHERE c.estado_cita = ? " +
                "GROUP BY c.id_cita, u.email, e.nombre, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud " +
                "ORDER BY c.fecha_hora_cita DESC";

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CitaDTO dto = new CitaDTO();
                dto.setIdCita(rs.getInt("id_cita"));
                dto.setEstadoCita(rs.getString("estado_cita"));
                dto.setFechaCita(rs.getTimestamp("fecha_hora_cita").toLocalDateTime());
                dto.setFechaSolicitudCita(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
                dto.setClienteEmail(rs.getString("cliente_email"));
                dto.setEstilistaNombre(rs.getString("estilista_nombre"));
                dto.setNombresServicios(rs.getString("nombres_servicios"));
                citas.add(dto);
            }
        }
        return citas;
    }

    public List<CitaDTO> findByEstilista(int idEstilista) throws SQLException {
        List<CitaDTO> citas = new ArrayList<>();
        String sql = "SELECT c.id_cita, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud, " +
                "u.email AS cliente_email, e.nombre AS estilista_nombre, " +
                "GROUP_CONCAT(s.nombre_servicio SEPARATOR ', ') AS nombres_servicios " +
                "FROM cita c " +
                "JOIN usuario u ON c.id_cliente = u.id_usuario " +
                "JOIN empleado e ON c.id_estilista = e.id_empleado " +
                "JOIN cita_servicio cs ON c.id_cita = cs.id_cita " +
                "JOIN servicio s ON cs.id_servicio = s.id_servicio " +
                "WHERE c.id_estilista = ? " +
                "GROUP BY c.id_cita, u.email, e.nombre, c.estado_cita, c.fecha_hora_cita, c.fecha_solicitud " +
                "ORDER BY c.fecha_hora_cita DESC";

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstilista);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CitaDTO dto = new CitaDTO();
                dto.setIdCita(rs.getInt("id_cita"));
                dto.setEstadoCita(rs.getString("estado_cita"));
                dto.setFechaCita(rs.getTimestamp("fecha_hora_cita").toLocalDateTime());
                dto.setFechaSolicitudCita(rs.getTimestamp("fecha_solicitud").toLocalDateTime());
                dto.setClienteEmail(rs.getString("cliente_email"));
                dto.setEstilistaNombre(rs.getString("estilista_nombre"));
                dto.setNombresServicios(rs.getString("nombres_servicios"));
                citas.add(dto);
            }
        }
        return citas;
    }

    public int save(Cita cita) throws SQLException {
        String sqlCita = "INSERT INTO cita(estado_cita, fecha_hora_cita, fecha_solicitud, id_cliente, id_estilista, id_horario) VALUES(?,?,?,?,?,?)";
        String sqlRelacion = "INSERT INTO cita_servicio(id_cita, id_servicio) VALUES(?,?)";
        
        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false); // Iniciar transacción

                int idCita;
                try (PreparedStatement stmtCita = conn.prepareStatement(sqlCita, Statement.RETURN_GENERATED_KEYS)) {
                    stmtCita.setString(1, cita.getEstadoCita());
                    stmtCita.setTimestamp(2, Timestamp.valueOf(cita.getFechaCita()));
                    stmtCita.setTimestamp(3, Timestamp.valueOf(cita.getFechaSolicitudCita()));
                    stmtCita.setInt(4, cita.getIdCliente());
                    stmtCita.setInt(5, cita.getIdEstilista());
                    stmtCita.setInt(6, cita.getIdHorario());
                    stmtCita.executeUpdate();

                    try (ResultSet rs = stmtCita.getGeneratedKeys()) {
                        if (rs.next()) {
                            idCita = rs.getInt(1);
                        } else {
                            throw new SQLException("No se pudo obtener el ID de la cita creada.");
                        }
                    }
                }

                try (PreparedStatement stmtRelacion = conn.prepareStatement(sqlRelacion)) {
                    for (int idServicio : cita.getServicios()) {
                        stmtRelacion.setInt(1, idCita);
                        stmtRelacion.setInt(2, idServicio);
                        stmtRelacion.addBatch();
                    }
                    stmtRelacion.executeBatch();
                }

                conn.commit(); // Finalizar transacción
                return idCita;
            } catch (SQLException e) {
                conn.rollback(); // Revertir en caso de error
                throw e;
            }
        } // try-with-resources se encarga de cerrar la conexión correctamente
    }

    public boolean update(Cita cita) throws SQLException {
        String sql = "UPDATE cita SET fecha_hora_cita = ?, id_estilista = ?, id_horario = ? WHERE id_cita = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(cita.getFechaCita()));
            stmt.setInt(2, cita.getIdEstilista());
            stmt.setInt(3, cita.getIdHorario());
            stmt.setInt(4, cita.getIdCita());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateEstado(int idCita, String estado) throws SQLException {
        String sql = "UPDATE cita SET estado_cita = ? WHERE id_cita = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, idCita);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int idCita) throws SQLException {
        String sqlRelacion = "DELETE FROM cita_servicio WHERE id_cita = ?";
        String sqlCita = "DELETE FROM cita WHERE id_cita = ?";
        
        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false); // Iniciar transacción

                try (PreparedStatement stmtRelacion = conn.prepareStatement(sqlRelacion)) {
                    stmtRelacion.setInt(1, idCita);
                    stmtRelacion.executeUpdate();
                }

                int affectedRows;
                try (PreparedStatement stmtCita = conn.prepareStatement(sqlCita)) {
                    stmtCita.setInt(1, idCita);
                    affectedRows = stmtCita.executeUpdate();
                }

                conn.commit(); // Finalizar transacción
                return affectedRows > 0;
            } catch (SQLException e) {
                conn.rollback(); // Revertir en caso de error
                throw e;
            }
        } // try-with-resources se encarga de cerrar la conexión correctamente
    }
}
