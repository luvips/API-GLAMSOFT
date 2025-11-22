package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Comentario;
import org.pi.Services.ComentarioService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComentarioController {
    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    public void getAll(Context ctx) {
        try {
            // Opcional: Añadir query param para obtener los últimos N comentarios
            String limitParam = ctx.queryParam("limit");
            List<Comentario> comentarios;
            if (limitParam != null) {
                int limit = Integer.parseInt(limitParam);
                comentarios = comentarioService.findLatest(limit);
            } else {
                comentarios = comentarioService.findAll();
            }
            successResponse(ctx, 200, "Comentarios recuperados correctamente", comentarios);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El parámetro 'limit' debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Comentario comentario = comentarioService.findById(id);
            if (comentario == null) {
                errorResponse(ctx, 404, "Comentario no encontrado.");
                return;
            }
            successResponse(ctx, 200, "Comentario encontrado", comentario);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }
    
    public void getByCliente(Context ctx) {
        try {
            int idCliente = Integer.parseInt(ctx.pathParam("idCliente"));
            List<Comentario> comentarios = comentarioService.findByCliente(idCliente);
            successResponse(ctx, 200, "Comentarios del cliente recuperados", comentarios);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del cliente debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Comentario comentario = ctx.bodyAsClass(Comentario.class);
            if (comentario.getComentario() == null || comentario.getComentario().trim().isEmpty()) {
                errorResponse(ctx, 400, "El texto del comentario no puede estar vacío.");
                return;
            }
            if (comentario.getIdCita() <= 0 || comentario.getIdCliente() <= 0) {
                errorResponse(ctx, 400, "Se requieren IDs de cita y cliente válidos.");
                return;
            }
            int idGenerado = comentarioService.create(comentario);
            successResponse(ctx, 201, "Comentario creado exitosamente", Map.of("id", idGenerado));
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Comentario comentario = ctx.bodyAsClass(Comentario.class);

            if (comentarioService.findById(id) == null) {
                errorResponse(ctx, 404, "Comentario no encontrado para actualizar.");
                return;
            }
            if (comentario.getComentario() == null || comentario.getComentario().trim().isEmpty()) {
                errorResponse(ctx, 400, "El texto del comentario no puede estar vacío.");
                return;
            }
            if (comentarioService.update(id, comentario)) {
                successResponse(ctx, 200, "Comentario actualizado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el comentario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (comentarioService.findById(id) == null) {
                errorResponse(ctx, 404, "Comentario no encontrado para eliminar.");
                return;
            }
            if (comentarioService.delete(id)) {
                successResponse(ctx, 200, "Comentario eliminado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el comentario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    // --- Métodos de ayuda ---

    private void successResponse(Context ctx, int statusCode, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        ctx.status(statusCode).json(response);
    }

    private void errorResponse(Context ctx, int statusCode, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        ctx.status(statusCode).json(response);
    }
}
