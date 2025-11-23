package org.pi.Repositories;

import com.google.gson.Gson;
import org.pi.Config.DBconfig;
import org.pi.Models.Cita;
import org.pi.dto.CitaDTO;
import org.pi.dto.RespuestaFormularioDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CitaRepository {

    private List<CitaDTO> findCitasDetailed(String whereClause, Object... params) throws SQLException {
        List<CitaDTO> citas = new ArrayList<>();
        String sql = "SELECT " +
                "c.id_cita, c.fecha_hora_cita, c.estado_cita, c.notas, " +
                "u.id_usuario AS id_cliente, u.nombre AS nombre_cliente, u.telefono AS telefono_cliente, " +
                "e.id_empleado AS id_estilista, e.nombre AS nombre_estilista, e.puesto AS especialidad_estilista, " +
                "s.id_servicio, s.nombre_servicio, s.precio AS precio_servicio, s.duracion_minutos " +
                "FROM cita c " +
                "JOIN usuario u ON c.id_cliente = u.id_usuario " +
                "JOIN empleado e ON c.id_estilista = e.id_empleado " +
                "LEFT JOIN cita_servicio cs ON c.id_cita = cs.id_cita " +
                "LEFT JOIN servicio s ON cs.id_servicio = s.id_servicio " +
                whereClause + " ORDER BY c.fecha_hora_cita DESC";

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();
            
            CitaDTO currentCita = null;
            int lastCitaId = -1;

            while (rs.next()) {
                int citaId = rs.getInt("id_cita");
                if (citaId != lastCitaId) {
                    if (currentCita != null) {
                        citas.add(currentCita);
                    }
                    currentCita = new CitaDTO();
                    currentCita.setIdCita(rs.getInt("id_cita"));
                    currentCita.setFechaHoraCita(rs.getTimestamp("fecha_hora_cita").toLocalDateTime());
                    currentCita.setEstadoCita(rs.getString("estado_cita"));
                    currentCita.setNotas(rs.getString("notas"));
                    
                    currentCita.setIdCliente(rs.getInt("id_cliente"));
                    currentCita.setNombreCliente(rs.getString("nombre_cliente"));
                    currentCita.setTelefonoCliente(rs.getString("telefono_cliente"));

                    currentCita.setIdEstilista(rs.getInt("id_estilista"));
                    currentCita.setNombreEstilista(rs.getString("nombre_estilista"));
                    currentCita.setEspecialidadEstilista(rs.getString("especialidad_estilista"));

                    lastCitaId = citaId;
                }

                if (rs.getInt("id_servicio") != 0 && currentCita != null) {
                    currentCita.addServicio(
                        rs.getInt("id_servicio"),
                        rs.getString("nombre_servicio"),
                        rs.getDouble("precio_servicio"),
                        rs.getInt("duracion_minutos")
                    );
                }
            }
            if (currentCita != null) {
                citas.add(currentCita);
            }
        }
        return citas;
    }

    public List<CitaDTO> findAll(String estado, String fecha) throws SQLException {
        StringBuilder whereClause = new StringBuilder("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (estado != null && !estado.isEmpty()) {
            whereClause.append("AND c.estado_cita = ? ");
            params.add(estado);
        }
        if (fecha != null && !fecha.isEmpty()) {
            whereClause.append("AND DATE(c.fecha_hora_cita) = ? ");
            params.add(fecha);
        }
        return findCitasDetailed(whereClause.toString(), params.toArray());
    }

    public CitaDTO findById(int id) throws SQLException {
        List<CitaDTO> result = findCitasDetailed("WHERE c.id_cita = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<CitaDTO> findByCliente(int idCliente) throws SQLException {
        return findCitasDetailed("WHERE c.id_cliente = ?", idCliente);
    }

    public List<CitaDTO> findByEstilista(int idEstilista) throws SQLException {
        return findCitasDetailed("WHERE c.id_estilista = ?", idEstilista);
    }
    
    public List<CitaDTO> findByEstado(String estado) throws SQLException {
        return findCitasDetailed("WHERE c.estado_cita = ?", estado);
    }

    public List<CitaDTO> findByWeek(int semana, int year) throws SQLException {
        return findCitasDetailed("WHERE WEEK(c.fecha_hora_cita, 1) = ? AND YEAR(c.fecha_hora_cita) = ?", semana, year);
    }

    public List<CitaDTO> findByMonth(int mes, int year) throws SQLException {
        return findCitasDetailed("WHERE MONTH(c.fecha_hora_cita) = ? AND YEAR(c.fecha_hora_cita) = ?", mes, year);
    }

    public List<CitaDTO> findByYear(int year) throws SQLException {
        return findCitasDetailed("WHERE YEAR(c.fecha_hora_cita) = ?", year);
    }

    public Cita save(Cita cita, List<Integer> servicios, List<RespuestaFormularioDTO> respuestas) throws SQLException {
        String sqlCita = "INSERT INTO cita(fecha_hora_cita, estado_cita, notas, id_cliente, id_estilista, respuestas_formulario) VALUES(?,?,?,?,?,?)";
        String sqlRelacion = "INSERT INTO cita_servicio(id_cita, id_servicio, precio_aplicado) VALUES(?, ?, (SELECT precio FROM servicio WHERE id_servicio = ?))";
        String sqlUpdatePrecio = "UPDATE cita SET precio_total = (SELECT SUM(precio_aplicado) FROM cita_servicio WHERE id_cita = ?) WHERE id_cita = ?";

        try (Connection conn = DBconfig.getDataSource().getConnection()) {
            try {
                conn.setAutoCommit(false);

                try (PreparedStatement stmtCita = conn.prepareStatement(sqlCita, Statement.RETURN_GENERATED_KEYS)) {
                    stmtCita.setTimestamp(1, Timestamp.valueOf(cita.getFechaHoraCita()));
                    stmtCita.setString(2, cita.getEstadoCita());
                    stmtCita.setString(3, cita.getNotas());
                    stmtCita.setInt(4, cita.getIdCliente());
                    stmtCita.setInt(5, cita.getIdEstilista());
                    stmtCita.setString(6, new Gson().toJson(respuestas));
                    stmtCita.executeUpdate();
                    try (ResultSet rs = stmtCita.getGeneratedKeys()) {
                        if (rs.next()) {
                            cita.setIdCita(rs.getInt(1));
                        } else {
                            throw new SQLException("No se pudo obtener el ID de la cita creada.");
                        }
                    }
                }

                try (PreparedStatement stmtRelacion = conn.prepareStatement(sqlRelacion)) {
                    for (int idServicio : servicios) {
                        stmtRelacion.setInt(1, cita.getIdCita());
                        stmtRelacion.setInt(2, idServicio);
                        stmtRelacion.setInt(3, idServicio);
                        stmtRelacion.addBatch();
                    }
                    stmtRelacion.executeBatch();
                }

                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdatePrecio)) {
                    stmtUpdate.setInt(1, cita.getIdCita());
                    stmtUpdate.setInt(2, cita.getIdCita());
                    stmtUpdate.executeUpdate();
                }

                conn.commit();
                return cita;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean update(Cita cita) throws SQLException {
        String sql = "UPDATE cita SET fecha_hora_cita = ?, id_estilista = ?, notas = ? WHERE id_cita = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(cita.getFechaHoraCita()));
            stmt.setInt(2, cita.getIdEstilista());
            stmt.setString(3, cita.getNotas());
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
    
    public boolean aprobar(int idCita, int adminId) throws SQLException {
        String sql = "UPDATE cita SET estado_cita = 'APROBADA', fecha_aprobacion = CURRENT_TIMESTAMP, aprobada_por = ? WHERE id_cita = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            stmt.setInt(2, idCita);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean rechazar(int idCita, String razon) throws SQLException {
        String sql = "UPDATE cita SET estado_cita = 'RECHAZADA', razon_rechazo = ? WHERE id_cita = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, razon);
            stmt.setInt(2, idCita);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean cancelar(int idCita, String razon) throws SQLException {
        String sql = "UPDATE cita SET estado_cita = 'CANCELADA', razon_rechazo = ? WHERE id_cita = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, razon);
            stmt.setInt(2, idCita);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean completar(int idCita) throws SQLException {
        String sql = "UPDATE cita SET estado_cita = 'COMPLETADA' WHERE id_cita = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCita);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int idCita) throws SQLException {
        String sql = "DELETE FROM cita WHERE id_cita = ?";
        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCita);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean isEstilistaDisponible(int idEstilista, LocalDateTime fechaHoraCita) throws SQLException {
        String diaSemana = fechaHoraCita.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        String sql = "SELECT 1 FROM estilista_horario eh " +
                     "JOIN horario h ON eh.id_horario = h.id_horario " +
                     "WHERE eh.id_estilista = ? " +
                     "AND h.dia_semana = ? " +
                     "AND ? BETWEEN h.hora_inicio AND h.hora_fin";

        try (Connection conn = DBconfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idEstilista);
            stmt.setString(2, diaSemana);
            stmt.setTime(3, Time.valueOf(fechaHoraCita.toLocalTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
