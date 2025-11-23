package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Services.NotificacionService;
import org.pi.dto.NotificacionDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificacionController {
    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    public void getNotificacionesByUsuario(Context ctx) {
        try {
            int idUsuario = Integer.parseInt(ctx.pathParam("idUsuario"));
            List<NotificacionDTO> notificaciones = notificacionService.getNotificacionesByUsuario(idUsuario);
            successResponse(ctx, 200, "Notificaciones recuperadas", notificaciones);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de usuario inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void marcarComoLeida(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (notificacionService.marcarComoLeida(id)) {
                successResponse(ctx, 200, "Notificación marcada como leída", null);
            } else {
                errorResponse(ctx, 404, "Notificación no encontrada.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de notificación inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void contarNoLeidas(Context ctx) {
        try {
            int idUsuario = Integer.parseInt(ctx.pathParam("idUsuario"));
            int count = notificacionService.contarNoLeidas(idUsuario);
            Map<String, Integer> response = new HashMap<>();
            response.put("noLeidas", count);
            successResponse(ctx, 200, "Contador de notificaciones no leídas", response);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de usuario inválido.");
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
