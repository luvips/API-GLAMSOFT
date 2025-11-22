package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Servicio;
import org.pi.Services.ServicioService;
import org.pi.dto.ServicioDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicioController {
    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    public void getAll(Context ctx) {
        try {
            String categoria = ctx.queryParam("categoria");
            Boolean activo = ctx.queryParamAsClass("activo", Boolean.class).getOrDefault(null);
            
            List<ServicioDTO> servicios = servicioService.findAll(categoria, activo);
            successResponse(ctx, 200, "Servicios recuperados", servicios);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ServicioDTO servicio = servicioService.findById(id);
            if (servicio == null) {
                errorResponse(ctx, 404, "Servicio no encontrado.");
                return;
            }
            successResponse(ctx, 200, "Servicio encontrado", servicio);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de servicio inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Servicio servicio = ctx.bodyAsClass(Servicio.class);

            if (servicio.getNombreServicio() == null || servicio.getPrecio() <= 0 || servicio.getDuracionMinutos() <= 0) {
                errorResponse(ctx, 400, "Nombre, precio y duración son obligatorios.");
                return;
            }

            Servicio servicioCreado = servicioService.create(servicio);
            
            Map<String, Object> data = new HashMap<>();
            data.put("idServicio", servicioCreado.getIdServicio());
            data.put("nombre", servicioCreado.getNombreServicio());
            data.put("precio", servicioCreado.getPrecio());
            data.put("duracion", servicioCreado.getDuracionMinutos());

            successResponse(ctx, 201, "Servicio creado exitosamente", data);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Servicio servicio = ctx.bodyAsClass(Servicio.class);
            
            if (servicioService.findById(id) == null) {
                errorResponse(ctx, 404, "Servicio no encontrado para actualizar.");
                return;
            }

            if (servicioService.update(id, servicio)) {
                successResponse(ctx, 200, "Servicio actualizado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el servicio.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de servicio inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (servicioService.findById(id) == null) {
                errorResponse(ctx, 404, "Servicio no encontrado para eliminar.");
                return;
            }
            if (servicioService.delete(id)) {
                successResponse(ctx, 200, "Servicio eliminado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el servicio.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de servicio inválido.");
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
