package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Pregunta;
import org.pi.Services.PreguntaService;
import org.pi.dto.PreguntaDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreguntaController {
    private final PreguntaService preguntaService;

    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    public void getAll(Context ctx) {
        try {
            List<PreguntaDTO> preguntas = preguntaService.findAll();
            successResponse(ctx, 200, "Preguntas recuperadas", preguntas);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            PreguntaDTO pregunta = preguntaService.findById(id);
            if (pregunta == null) {
                errorResponse(ctx, 404, "Pregunta no encontrada.");
                return;
            }
            successResponse(ctx, 200, "Pregunta encontrada", pregunta);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de pregunta inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Pregunta pregunta = ctx.bodyAsClass(Pregunta.class);
            if (pregunta.getPregunta() == null || pregunta.getPregunta().trim().isEmpty()) {
                errorResponse(ctx, 400, "El texto de la pregunta es obligatorio.");
                return;
            }

            Pregunta preguntaCreada = preguntaService.create(pregunta);
            
            Map<String, Object> data = new HashMap<>();
            data.put("idPregunta", preguntaCreada.getIdPregunta());
            data.put("pregunta", preguntaCreada.getPregunta());

            successResponse(ctx, 201, "Pregunta creada exitosamente", data);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Pregunta pregunta = ctx.bodyAsClass(Pregunta.class);
            
            if (preguntaService.findById(id) == null) {
                errorResponse(ctx, 404, "Pregunta no encontrada para actualizar.");
                return;
            }
            if (pregunta.getRespuesta() == null) {
                errorResponse(ctx, 400, "El campo 'respuesta' es obligatorio para actualizar.");
                return;
            }

            if (preguntaService.update(id, pregunta)) {
                successResponse(ctx, 200, "Pregunta actualizada exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar la pregunta.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de pregunta inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (preguntaService.findById(id) == null) {
                errorResponse(ctx, 404, "Pregunta no encontrada para eliminar.");
                return;
            }
            if (preguntaService.delete(id)) {
                successResponse(ctx, 200, "Pregunta eliminada exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar la pregunta.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de pregunta inválido.");
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
