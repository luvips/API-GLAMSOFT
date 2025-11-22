package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Promocion;
import org.pi.Services.PromocionService;
import org.pi.dto.PromocionDTO;
import org.pi.dto.ServicioPromocionDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromocionController {
    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    public void getAll(Context ctx) {
        try {
            List<PromocionDTO> promociones = promocionService.findAll();
            successResponse(ctx, 200, "Promociones recuperadas", promociones);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            PromocionDTO promocion = promocionService.findById(id);
            if (promocion == null) {
                errorResponse(ctx, 404, "Promoción no encontrada.");
                return;
            }
            successResponse(ctx, 200, "Promoción encontrada", promocion);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de promoción inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Promocion promo = ctx.bodyAsClass(Promocion.class);
            Promocion promoCreada = promocionService.create(promo);
            
            Map<String, Object> data = new HashMap<>();
            data.put("idPromocion", promoCreada.getIdPromocion());
            data.put("nombre", promoCreada.getNombrePromocion());
            data.put("porcentajeDescuento", promoCreada.getDescuento()); // Corrected method call

            successResponse(ctx, 201, "Promoción creada exitosamente", data);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Promocion promo = ctx.bodyAsClass(Promocion.class);
            if (promocionService.findById(id) == null) {
                errorResponse(ctx, 404, "Promoción no encontrada para actualizar.");
                return;
            }
            if (promocionService.update(id, promo)) {
                successResponse(ctx, 200, "Promoción actualizada exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar la promoción.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de promoción inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (promocionService.findById(id) == null) {
                errorResponse(ctx, 404, "Promoción no encontrada para eliminar.");
                return;
            }
            if (promocionService.delete(id)) {
                successResponse(ctx, 200, "Promoción eliminada exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar la promoción.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de promoción inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void addServicioToPromocion(Context ctx) {
        try {
            int idPromocion = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Integer> body = ctx.bodyAsClass(Map.class);
            int idServicio = body.get("idServicio");

            if (promocionService.addServicioToPromocion(idPromocion, idServicio)) {
                successResponse(ctx, 200, "Servicio agregado a la promoción exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo agregar el servicio a la promoción.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de promoción inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void getServiciosFromPromocion(Context ctx) {
        try {
            int idPromocion = Integer.parseInt(ctx.pathParam("id"));
            PromocionDTO promo = promocionService.findById(idPromocion);
            if (promo == null) {
                errorResponse(ctx, 404, "Promoción no encontrada.");
                return;
            }
            List<ServicioPromocionDTO> servicios = promo.getServicios();
            successResponse(ctx, 200, "Servicios de la promoción recuperados", servicios);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de promoción inválido.");
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
