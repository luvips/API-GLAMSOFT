package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Servicio;
import org.pi.Services.ServicioService;

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
            List<Servicio> servicios = servicioService.findAllServicios();
            successResponse(ctx, 200, "Servicios recuperados correctamente", servicios);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al obtener servicios: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Servicio servicio = servicioService.findById(id); // Corregido a findById

            if (servicio == null) {
                errorResponse(ctx, 404, "Servicio no encontrado");
                return;
            }
            successResponse(ctx, 200, "Servicio encontrado", servicio);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del servicio debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al buscar el servicio: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Servicio servicio = ctx.bodyAsClass(Servicio.class);

            // Validaciones
            if (servicio.getNombreServicio() == null || servicio.getNombreServicio().trim().isEmpty()) {
                errorResponse(ctx, 400, "El nombre del servicio es obligatorio");
                return;
            }
            if (servicio.getPrecio() <= 0) {
                errorResponse(ctx, 400, "El precio debe ser mayor a 0");
                return;
            }
            if (servicio.getDuracionMinutos() <= 0) {
                errorResponse(ctx, 400, "La duración en minutos debe ser mayor a 0");
                return;
            }

            int idGenerado = servicioService.saveServicio(servicio);
            Map<String, Integer> data = new HashMap<>();
            data.put("id", idGenerado);
            successResponse(ctx, 201, "Servicio creado exitosamente", data);

        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al crear el servicio: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Servicio servicio = ctx.bodyAsClass(Servicio.class);

            // Verificar existencia
            Servicio existente = servicioService.findById(id);
            if (existente == null) {
                errorResponse(ctx, 404, "Servicio no encontrado para actualizar");
                return;
            }

            // Validaciones
            if (servicio.getNombreServicio() == null || servicio.getNombreServicio().trim().isEmpty()) {
                errorResponse(ctx, 400, "El nombre del servicio es obligatorio");
                return;
            }
            if (servicio.getPrecio() <= 0) {
                errorResponse(ctx, 400, "El precio debe ser mayor a 0");
                return;
            }
            if (servicio.getDuracionMinutos() <= 0) {
                errorResponse(ctx, 400, "La duración en minutos debe ser mayor a 0");
                return;
            }

            boolean actualizado = servicioService.updateServicio(id, servicio);
            if (actualizado) {
                successResponse(ctx, 200, "Servicio actualizado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el servicio.");
            }

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del servicio debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al actualizar el servicio: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));

            // Verificar existencia
            Servicio existente = servicioService.findById(id);
            if (existente == null) {
                errorResponse(ctx, 404, "Servicio no encontrado para eliminar");
                return;
            }

            boolean eliminado = servicioService.deleteServicio(id);
            if (eliminado) {
                successResponse(ctx, 200, "Servicio eliminado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el servicio.");
            }

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del servicio debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al eliminar el servicio: " + e.getMessage());
        }
    }

    // --- Métodos de ayuda para respuestas ---

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
