package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Comentario;
import org.pi.Services.ComentarioService;
import org.pi.dto.ComentarioDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
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
            List<ComentarioDTO> comentarios = comentarioService.findAll();
            successResponse(ctx, 200, "Comentarios recuperados", comentarios);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ComentarioDTO comentario = comentarioService.findById(id);
            if (comentario == null) {
                errorResponse(ctx, 404, "Comentario no encontrado.");
                return;
            }
            successResponse(ctx, 200, "Comentario encontrado", comentario);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de comentario inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getByCliente(Context ctx) {
        try {
            int idCliente = Integer.parseInt(ctx.pathParam("idCliente"));
            List<ComentarioDTO> comentarios = comentarioService.findByCliente(idCliente);
            successResponse(ctx, 200, "Comentarios del cliente recuperados", comentarios);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de cliente inválido.");
        } catch (SQLException e) {
            e.printStackTrace();
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            // Validaciones de seguridad para evitar NullPointerException
            if (body.get("idCliente") == null || body.get("contenido") == null) {
                errorResponse(ctx, 400, "Faltan datos obligatorios (idCliente o contenido).");
                return;
            }

            Comentario comentario = new Comentario();
            comentario.setIdCliente((Integer) body.get("idCliente"));
            comentario.setComentario((String) body.get("contenido"));

            // ✅ CORRECCIÓN: Manejo seguro de idCita
            if (body.get("idCita") != null) {
                comentario.setIdCita((Integer) body.get("idCita"));
            } else {
                // Si tu lógica de negocio PERMITE comentarios sin cita, descomenta esto y ajusta la BD:
                // comentario.setIdCita(0);

                // Si es OBLIGATORIO (como está en tu BD actual), devolvemos error 400:
                errorResponse(ctx, 400, "Es necesario asociar el comentario a una Cita (idCita).");
                return;
            }

            if (comentario.getComentario().trim().isEmpty()) {
                errorResponse(ctx, 400, "El contenido del comentario no puede estar vacío.");
                return;
            }

            Comentario comentarioCreado = comentarioService.create(comentario);

            Map<String, Object> data = new HashMap<>();
            data.put("idComentario", comentarioCreado.getIdComentario());
            data.put("fecha", LocalDateTime.now().toString());

            successResponse(ctx, 201, "Comentario creado exitosamente", data);
        } catch (SQLException e) {
            e.printStackTrace();
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    // ... (Métodos update, delete y helpers se mantienen igual)
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String contenido = body.get("contenido");

            if (comentarioService.findById(id) == null) {
                errorResponse(ctx, 404, "Comentario no encontrado para actualizar.");
                return;
            }
            if (contenido == null || contenido.trim().isEmpty()) {
                errorResponse(ctx, 400, "El contenido no puede estar vacío.");
                return;
            }

            Comentario comentario = new Comentario();
            comentario.setComentario(contenido);

            if (comentarioService.update(id, comentario)) {
                successResponse(ctx, 200, "Comentario actualizado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el comentario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de comentario inválido.");
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
                successResponse(ctx, 200, "Comentario eliminado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el comentario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de comentario inválido.");
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