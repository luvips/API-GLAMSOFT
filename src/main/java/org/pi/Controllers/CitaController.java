package org.pi.Controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;
import org.pi.Models.Cita;
import org.pi.Services.CitaService;
import org.pi.dto.CitaDTO;
import org.pi.dto.RechazoCitaDTO;
import org.pi.dto.RespuestaFormularioDTO;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    public void create(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            
            Cita nuevaCita = new Cita();
            nuevaCita.setIdCliente((Integer) body.get("idCliente"));
            nuevaCita.setIdEstilista((Integer) body.get("idEstilista"));
            nuevaCita.setNotas((String) body.get("notas"));
            
            LocalDate fecha = LocalDate.parse((String) body.get("fecha"));
            LocalTime hora = LocalTime.parse((String) body.get("hora"));
            nuevaCita.setFechaHoraCita(LocalDateTime.of(fecha, hora));

            List<Integer> servicios = (List<Integer>) body.get("servicios");
            
            Type listType = new TypeToken<List<RespuestaFormularioDTO>>() {}.getType();
            List<RespuestaFormularioDTO> respuestas = new Gson().fromJson(new Gson().toJson(body.get("respuestasFormulario")), listType);

            Cita citaCreada = citaService.create(nuevaCita, servicios, respuestas);
            
            successResponse(ctx, 201, "Cita creada, pendiente de aprobación", null);

        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Formato de JSON inválido: " + e.getMessage());
        }
    }

    public void aprobarCita(Context ctx) {
        try {
            int idCita = Integer.parseInt(ctx.pathParam("id"));
            // Aquí deberías obtener el ID del admin/estilista autenticado
            int adminId = 1; // Placeholder
            if (citaService.aprobarCita(idCita, adminId)) {
                successResponse(ctx, 200, "Cita aprobada exitosamente", null);
            } else {
                errorResponse(ctx, 404, "Cita no encontrada.");
            }
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error al aprobar la cita: " + e.getMessage());
        }
    }

    public void rechazarCita(Context ctx) {
        try {
            int idCita = Integer.parseInt(ctx.pathParam("id"));
            RechazoCitaDTO dto = ctx.bodyAsClass(RechazoCitaDTO.class);
            if (citaService.rechazarCita(idCita, dto.getRazonRechazo())) {
                successResponse(ctx, 200, "Cita rechazada exitosamente", null);
            } else {
                errorResponse(ctx, 404, "Cita no encontrada.");
            }
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error al rechazar la cita: " + e.getMessage());
        }
    }

    public void completarCita(Context ctx) {
        try {
            int idCita = Integer.parseInt(ctx.pathParam("id"));
            if (citaService.completarCita(idCita)) {
                successResponse(ctx, 200, "Cita marcada como completada", null);
            } else {
                errorResponse(ctx, 404, "Cita no encontrada.");
            }
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error al completar la cita: " + e.getMessage());
        }
    }

    public void cancelarCita(Context ctx) {
        try {
            int idCita = Integer.parseInt(ctx.pathParam("id"));
            RechazoCitaDTO dto = ctx.bodyAsClass(RechazoCitaDTO.class);
            if (citaService.cancelarCita(idCita, dto.getRazonRechazo())) {
                successResponse(ctx, 200, "Cita cancelada exitosamente", null);
            } else {
                errorResponse(ctx, 404, "Cita no encontrada.");
            }
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error al cancelar la cita: " + e.getMessage());
        }
    }

    public void getCitasPendientes(Context ctx) {
        try {
            List<CitaDTO> citas = citaService.getCitasPendientes();
            successResponse(ctx, 200, "Citas pendientes recuperadas", citas);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    // --- Métodos existentes (getAll, getById, etc.) ---
    public void getAll(Context ctx) {
        try {
            String estado = ctx.queryParam("estado");
            String fecha = ctx.queryParam("fecha");
            List<CitaDTO> citas = citaService.findAll(estado, fecha);
            successResponse(ctx, 200, "Citas recuperadas", citas);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            CitaDTO cita = citaService.findById(id);
            if (cita == null) {
                errorResponse(ctx, 404, "Cita no encontrada.");
                return;
            }
            successResponse(ctx, 200, "Cita encontrada", cita);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de cita inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }
    
    private void successResponse(Context ctx, int statusCode, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        ctx.status(statusCode).json(response);
    }

    private void errorResponse(Context ctx, int statusCode, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        ctx.status(statusCode).json(response);
    }
}
