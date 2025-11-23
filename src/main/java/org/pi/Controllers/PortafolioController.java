package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Portafolio;
import org.pi.Services.PortafolioService;
import org.pi.dto.PortafolioDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortafolioController {
    private final PortafolioService portafolioService;

    public PortafolioController(PortafolioService portafolioService) {
        this.portafolioService = portafolioService;
    }

    public void getAll(Context ctx) {
        try {
            List<PortafolioDTO> portafolios = portafolioService.findAll();
            successResponse(ctx, 200, "Portafolio recuperado", portafolios);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            PortafolioDTO portafolio = portafolioService.findById(id);
            if (portafolio == null) {
                errorResponse(ctx, 404, "Entrada de portafolio no encontrada.");
                return;
            }
            successResponse(ctx, 200, "Entrada de portafolio encontrada", portafolio);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de portafolio inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Portafolio portafolio = ctx.bodyAsClass(Portafolio.class);
            
            if (portafolio.getTitulo() == null || portafolio.getUrl() == null) {
                errorResponse(ctx, 400, "Título y URL de la imagen son obligatorios.");
                return;
            }

            Portafolio portafolioCreado = portafolioService.create(portafolio);

            Map<String, Object> data = new HashMap<>();
            data.put("idImagen", portafolioCreado.getIdImagen());
            data.put("titulo", portafolioCreado.getTitulo());
            data.put("fecha", LocalDateTime.now().toString());

            successResponse(ctx, 201, "Portafolio creado exitosamente", data);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Portafolio portafolio = ctx.bodyAsClass(Portafolio.class);
            
            if (portafolioService.findById(id) == null) {
                errorResponse(ctx, 404, "Entrada de portafolio no encontrada para actualizar.");
                return;
            }

            if (portafolioService.update(id, portafolio)) {
                successResponse(ctx, 200, "Portafolio actualizado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar la entrada del portafolio.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de portafolio inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (portafolioService.findById(id) == null) {
                errorResponse(ctx, 404, "Entrada de portafolio no encontrada para eliminar.");
                return;
            }
            if (portafolioService.delete(id)) {
                successResponse(ctx, 200, "Portafolio eliminado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar la entrada del portafolio.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de portafolio inválido.");
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
