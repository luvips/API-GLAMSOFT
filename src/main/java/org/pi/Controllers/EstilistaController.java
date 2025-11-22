package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Cita;
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

    // --- MÉTODOS DE LECTURA ---

    public void getAll(Context ctx) {
        try {
            List<EstilistaDTO> estilistas = estilistaService.findAllEstilistas();
            successResponse(ctx, 200, "Estilistas recuperados correctamente", estilistas);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
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
            errorResponse(ctx, 400, "El ID debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    // --- MÉTODOS CRUD ---

    public void create(Context ctx) {
        try {
            Estilista estilista = ctx.bodyAsClass(Estilista.class);
            // Validaciones
            if (estilista.getNombre() == null || estilista.getNombre().trim().isEmpty() ||
                estilista.getEmail() == null || estilista.getEmail().trim().isEmpty() ||
                estilista.getPassword() == null || estilista.getPassword().trim().isEmpty()) {
                errorResponse(ctx, 400, "Nombre, email y contraseña son obligatorios.");
                return;
            }
            // Hashear contraseña
            estilista.setPassword(Password.hash(estilista.getPassword()).withBcrypt().getResult());
            
            Estilista estilistaCreado = estilistaService.create(estilista);
            successResponse(ctx, 201, "Estilista creado exitosamente", estilistaCreado);
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
            if (estilistaService.findEstilistaById(id) == null) {
                errorResponse(ctx, 404, "Estilista no encontrado para actualizar.");
                return;
            }
            if (estilistaService.update(id, estilista)) {
                successResponse(ctx, 200, "Estilista actualizado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el estilista.");
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
            if (estilistaService.findEstilistaById(id) == null) {
                errorResponse(ctx, 404, "Estilista no encontrado para eliminar.");
                return;
            }
            if (estilistaService.delete(id)) {
                successResponse(ctx, 200, "Estilista eliminado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el estilista.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }
    
    // --- OTROS MÉTODOS ---
    
    public void findEstilistaServicio(Context ctx) {
        // Lógica existente...
    }

    // --- MÉTODOS DE AYUDA ---

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
