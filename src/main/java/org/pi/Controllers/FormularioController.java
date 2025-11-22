package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Formulario;
import org.pi.Services.FormularioService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormularioController {
    private final FormularioService formularioService;

    public FormularioController(FormularioService formularioService) {
        this.formularioService = formularioService;
    }

    public void getAll(Context ctx) {
        try {
            List<Formulario> formularios = formularioService.findAll();
            successResponse(ctx, 200, "Formularios recuperados", formularios);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Formulario formulario = formularioService.findById(id);
            if (formulario == null) {
                errorResponse(ctx, 404, "Formulario no encontrado.");
                return;
            }
            successResponse(ctx, 200, "Formulario encontrado", formulario);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de formulario inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Formulario formulario = ctx.bodyAsClass(Formulario.class);
            if (formulario.getNombreFormulario() == null || formulario.getNombreFormulario().trim().isEmpty()) {
                errorResponse(ctx, 400, "El nombre del formulario es obligatorio.");
                return;
            }
            
            Formulario formularioCreado = formularioService.create(formulario);
            successResponse(ctx, 201, "Formulario creado exitosamente", formularioCreado);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Formulario formulario = ctx.bodyAsClass(Formulario.class);
            
            if (formularioService.findById(id) == null) {
                errorResponse(ctx, 404, "Formulario no encontrado para actualizar.");
                return;
            }

            if (formularioService.update(id, formulario)) {
                successResponse(ctx, 200, "Formulario actualizado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el formulario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de formulario inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (formularioService.findById(id) == null) {
                errorResponse(ctx, 404, "Formulario no encontrado para eliminar.");
                return;
            }
            if (formularioService.delete(id)) {
                successResponse(ctx, 200, "Formulario eliminado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el formulario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de formulario inválido.");
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
