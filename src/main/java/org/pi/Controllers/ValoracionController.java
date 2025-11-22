package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Valoracion;
import org.pi.Services.ValoracionService;
import org.pi.dto.ValoracionDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValoracionController {
    private final ValoracionService valoracionService;

    public ValoracionController(ValoracionService valoracionService) {
        this.valoracionService = valoracionService;
    }

    public void getAll(Context ctx) {
        try {
            List<ValoracionDTO> valoraciones = valoracionService.findAll();
            successResponse(ctx, 200, "Valoraciones recuperadas", valoraciones);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ValoracionDTO valoracion = valoracionService.findById(id);
            if (valoracion == null) {
                errorResponse(ctx, 404, "Valoración no encontrada.");
                return;
            }
            successResponse(ctx, 200, "Valoración encontrada", valoracion);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de valoración inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Valoracion valoracion = ctx.bodyAsClass(Valoracion.class);
            
            Valoracion valoracionCreada = valoracionService.create(valoracion);
            
            Map<String, Object> data = new HashMap<>();
            data.put("idValoracion", valoracionCreada.getIdValoracion());
            data.put("calificacion", valoracionCreada.getPuntuacion());
            data.put("fecha", LocalDateTime.now().toString());

            successResponse(ctx, 201, "Valoración creada exitosamente", data);
        } catch (IllegalStateException | IllegalArgumentException e) {
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
            Valoracion valoracion = ctx.bodyAsClass(Valoracion.class);
            
            if (valoracionService.findById(id) == null) {
                errorResponse(ctx, 404, "Valoración no encontrada para actualizar.");
                return;
            }

            if (valoracionService.update(id, valoracion)) {
                successResponse(ctx, 200, "Valoración actualizada exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar la valoración.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de valoración inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (valoracionService.findById(id) == null) {
                errorResponse(ctx, 404, "Valoración no encontrada para eliminar.");
                return;
            }
            if (valoracionService.delete(id)) {
                successResponse(ctx, 200, "Valoración eliminada exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar la valoración.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de valoración inválido.");
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
