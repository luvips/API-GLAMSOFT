package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Estilista;
import org.pi.Services.EstilistaService;
import org.pi.dto.EstilistaDTO;
import com.password4j.Password;

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
            List<EstilistaDTO> estilistas = estilistaService.findAll();
            successResponse(ctx, 200, "Estilistas recuperados", estilistas);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            EstilistaDTO estilista = estilistaService.findById(id);
            if (estilista == null) {
                errorResponse(ctx, 404, "Estilista no encontrado.");
                return;
            }
            successResponse(ctx, 200, "Estilista encontrado", estilista);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de estilista inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Estilista estilista = ctx.bodyAsClass(Estilista.class);
            
            if (estilista.getNombre() == null || estilista.getEmail() == null || estilista.getPassword() == null) {
                errorResponse(ctx, 400, "Nombre, email y contraseña son obligatorios.");
                return;
            }
            
            estilista.setPassword(Password.hash(estilista.getPassword()).withBcrypt().getResult());
            
            Estilista estilistaCreado = estilistaService.create(estilista);
            
            Map<String, Object> data = new HashMap<>();
            data.put("idEstilista", estilistaCreado.getIdEmpleado());
            data.put("nombre", estilistaCreado.getNombre());
            data.put("especialidad", estilistaCreado.getPuesto());

            successResponse(ctx, 201, "Estilista creado exitosamente", data);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Estilista estilista = ctx.bodyAsClass(Estilista.class);
            
            if (estilistaService.findById(id) == null) {
                errorResponse(ctx, 404, "Estilista no encontrado para actualizar.");
                return;
            }

            if (estilistaService.update(id, estilista)) {
                successResponse(ctx, 200, "Estilista actualizado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el estilista.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de estilista inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (estilistaService.findById(id) == null) {
                errorResponse(ctx, 404, "Estilista no encontrado para eliminar.");
                return;
            }
            if (estilistaService.delete(id)) {
                successResponse(ctx, 200, "Estilista eliminado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el estilista.");
            }
        } catch (IllegalStateException e) {
            errorResponse(ctx, 409, e.getMessage()); // 409 Conflict
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de estilista inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void findEstilistaServicio(Context ctx) {
        try {
            int idServicio = Integer.parseInt(ctx.pathParam("idServicio"));
            List<EstilistaDTO> estilistas = estilistaService.findEstilistasByServicio(idServicio);
            successResponse(ctx, 200, "Estilistas encontrados para el servicio", estilistas);
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
