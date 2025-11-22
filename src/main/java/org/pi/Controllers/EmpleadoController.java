package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Empleado;
import org.pi.Services.EmpleadoService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpleadoController {
    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    public void getAll(Context ctx) {
        try {
            List<Empleado> empleados = empleadoService.findAll();
            successResponse(ctx, 200, "Empleados recuperados correctamente", empleados);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al obtener empleados: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Empleado empleado = empleadoService.findById(id);

            if (empleado == null) {
                errorResponse(ctx, 404, "Empleado no encontrado");
                return;
            }
            successResponse(ctx, 200, "Empleado encontrado", empleado);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del empleado debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al buscar el empleado: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Empleado empleado = ctx.bodyAsClass(Empleado.class);

            // Validaciones básicas de presencia de datos
            if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
                errorResponse(ctx, 400, "El nombre del empleado es obligatorio");
                return;
            }
            if (empleado.getTelefono() == null || empleado.getTelefono().trim().isEmpty()) {
                errorResponse(ctx, 400, "El teléfono del empleado es obligatorio");
                return;
            }

            Empleado empleadoCreado = empleadoService.create(empleado);
            successResponse(ctx, 201, "Empleado creado exitosamente", empleadoCreado);

        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al crear el empleado: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Empleado empleado = ctx.bodyAsClass(Empleado.class);

            // Verificar existencia
            Empleado existente = empleadoService.findById(id);
            if (existente == null) {
                errorResponse(ctx, 404, "Empleado no encontrado para actualizar");
                return;
            }

            // Validaciones
            if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
                errorResponse(ctx, 400, "El nombre del empleado es obligatorio");
                return;
            }
            if (empleado.getTelefono() == null || empleado.getTelefono().trim().isEmpty()) {
                errorResponse(ctx, 400, "El teléfono del empleado es obligatorio");
                return;
            }
            
            empleado.setIdEmpleado(id); // Usar el método correcto
            boolean actualizado = empleadoService.update(empleado);

            if (actualizado) {
                successResponse(ctx, 200, "Empleado actualizado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el empleado.");
            }

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del empleado debe ser un número válido");
        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al actualizar el empleado: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));

            // Verificar existencia
            Empleado existente = empleadoService.findById(id);
            if (existente == null) {
                errorResponse(ctx, 404, "Empleado no encontrado para eliminar");
                return;
            }

            boolean eliminado = empleadoService.delete(id);
            if (eliminado) {
                successResponse(ctx, 200, "Empleado eliminado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el empleado.");
            }

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del empleado debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al eliminar el empleado: " + e.getMessage());
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
