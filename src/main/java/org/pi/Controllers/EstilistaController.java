package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Cita;
import org.pi.Models.Estilista;
import org.pi.Services.EstilistaService;
import org.pi.dto.EstilistaDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstilistaController {
    private final EstilistaService estilistaService;

    public EstilistaController(EstilistaService estilistaService) {
        this.estilistaService = estilistaService;
    }

    public void getAll(Context ctx) {
        try {
            List<EstilistaDTO> estilistas = estilistaService.findAllEstilistas();
            successResponse(ctx, 200, "Estilistas recuperados correctamente", estilistas);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al obtener estilistas: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            EstilistaDTO estilista = estilistaService.findEstilistaById(id);

            if (estilista == null) {
                errorResponse(ctx, 404, "Estilista no encontrado");
                return;
            }
            successResponse(ctx, 200, "Estilista encontrado", estilista);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del estilista debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al buscar el estilista: " + e.getMessage());
        }
    }

    public void findEstilistaServicio(Context ctx) {
        try {
            int idServicio = Integer.parseInt(ctx.pathParam("idServicio"));
            Cita fecha = ctx.bodyAsClass(Cita.class);
            List<Estilista> estilistas = estilistaService.findEstilistaServicio(idServicio, fecha);
            successResponse(ctx, 200, "Estilistas disponibles para el servicio recuperados", estilistas);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del servicio debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
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
