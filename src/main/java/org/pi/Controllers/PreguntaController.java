package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Services.PreguntaService;
import org.pi.dto.PreguntaFormularioDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreguntaController {
    private final PreguntaService preguntaService;

    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    public void getPreguntasByServicio(Context ctx) {
        try {
            int idServicio = Integer.parseInt(ctx.pathParam("idServicio"));
            List<PreguntaFormularioDTO> preguntas = preguntaService.getPreguntasByServicio(idServicio);
            successResponse(ctx, 200, "Preguntas del servicio recuperadas", preguntas);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de servicio inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            PreguntaFormularioDTO dto = ctx.bodyAsClass(PreguntaFormularioDTO.class);
            preguntaService.create(dto);
            successResponse(ctx, 201, "Pregunta creada exitosamente", null);
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            PreguntaFormularioDTO dto = ctx.bodyAsClass(PreguntaFormularioDTO.class);
            if (preguntaService.update(id, dto)) {
                successResponse(ctx, 200, "Pregunta actualizada exitosamente", null);
            } else {
                errorResponse(ctx, 404, "Pregunta no encontrada.");
            }
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (preguntaService.delete(id)) {
                successResponse(ctx, 200, "Pregunta eliminada exitosamente", null);
            } else {
                errorResponse(ctx, 404, "Pregunta no encontrada.");
            }
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error al eliminar la pregunta: " + e.getMessage());
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
