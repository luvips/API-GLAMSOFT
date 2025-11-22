package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Rol;
import org.pi.Services.RolService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolController {
    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    public void getAll(Context ctx) {
        try {
            List<Rol> roles = rolService.findAll();
            successResponse(ctx, 200, "Roles recuperados", roles);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Rol rol = rolService.findById(id);
            if (rol == null) {
                errorResponse(ctx, 404, "Rol no encontrado.");
                return;
            }
            successResponse(ctx, 200, "Rol encontrado", rol);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de rol inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Rol rol = ctx.bodyAsClass(Rol.class);
            if (rol.getNombreRol() == null || rol.getNombreRol().trim().isEmpty()) {
                errorResponse(ctx, 400, "El nombre del rol es obligatorio.");
                return;
            }
            
            Rol rolCreado = rolService.create(rol);
            
            Map<String, Object> data = new HashMap<>();
            data.put("idRol", rolCreado.getIdRol());
            data.put("nombre", rolCreado.getNombreRol());

            successResponse(ctx, 201, "Rol creado exitosamente", data);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Rol rol = ctx.bodyAsClass(Rol.class);
            
            if (rolService.findById(id) == null) {
                errorResponse(ctx, 404, "Rol no encontrado para actualizar.");
                return;
            }

            if (rolService.update(id, rol)) {
                successResponse(ctx, 200, "Rol actualizado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el rol.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de rol inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (rolService.findById(id) == null) {
                errorResponse(ctx, 404, "Rol no encontrado para eliminar.");
                return;
            }
            if (rolService.delete(id)) {
                successResponse(ctx, 200, "Rol eliminado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el rol.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de rol inválido.");
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
