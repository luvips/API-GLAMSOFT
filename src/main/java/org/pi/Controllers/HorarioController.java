package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Horario;
import org.pi.Services.HorarioService;
import org.pi.dto.HorarioDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    public void getAll(Context ctx) {
        try {
            List<HorarioDTO> horarios = horarioService.findAll();
            successResponse(ctx, 200, "Horarios recuperados", horarios);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            HorarioDTO horario = horarioService.findById(id);
            if (horario == null) {
                errorResponse(ctx, 404, "Horario no encontrado.");
                return;
            }
            successResponse(ctx, 200, "Horario encontrado", horario);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de horario inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            
            Horario horario = new Horario();
            horario.setDiaSemana((String) body.get("dia"));
            horario.setHoraInicio(java.time.LocalTime.parse((String) body.get("horaInicio")));
            horario.setHoraFin(java.time.LocalTime.parse((String) body.get("horaFin")));
            
            int idEstilista = (Integer) body.get("idEstilista");

            Horario horarioCreado = horarioService.create(horario, idEstilista);
            
            Map<String, Object> data = new HashMap<>();
            data.put("idHorario", horarioCreado.getIdHorario());
            data.put("dia", horarioCreado.getDiaSemana());
            data.put("horaInicio", horarioCreado.getHoraInicio().toString());
            data.put("horaFin", horarioCreado.getHoraFin().toString());

            successResponse(ctx, 201, "Horario creado exitosamente", data);
        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            
            if (horarioService.findById(id) == null) {
                errorResponse(ctx, 404, "Horario no encontrado para actualizar.");
                return;
            }
            
            Horario horario = new Horario();
            horario.setHoraInicio(java.time.LocalTime.parse(body.get("horaInicio")));
            horario.setHoraFin(java.time.LocalTime.parse(body.get("horaFin")));

            if (horarioService.update(id, horario)) {
                successResponse(ctx, 200, "Horario actualizado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el horario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de horario inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (horarioService.findById(id) == null) {
                errorResponse(ctx, 404, "Horario no encontrado para eliminar.");
                return;
            }
            if (horarioService.delete(id)) {
                successResponse(ctx, 200, "Horario eliminado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el horario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de horario inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    // --- Métodos de ayuda ---

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
