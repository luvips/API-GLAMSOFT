package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Empleado;
import org.pi.Services.EmpleadoService;
import com.password4j.Password;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmpleadoController {
    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    private Map<String, Object> empleadoToDTO(Empleado empleado) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("idEmpleado", empleado.getIdEmpleado());
        dto.put("nombre", empleado.getNombre());
        dto.put("puesto", empleado.getPuesto());
        dto.put("telefono", empleado.getTelefono());
        dto.put("email", empleado.getEmail());
        // dto.put("fechaContratacion", ...); // Este campo no está en el modelo
        dto.put("activo", empleado.isActivo());
        return dto;
    }

    public void getAll(Context ctx) {
        try {
            List<Empleado> empleados = empleadoService.findAll();
            List<Map<String, Object>> dtoList = empleados.stream()
                .map(this::empleadoToDTO)
                .collect(Collectors.toList());
            successResponse(ctx, 200, "Empleados recuperados", dtoList);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Empleado empleado = empleadoService.findById(id);
            if (empleado == null) {
                errorResponse(ctx, 404, "Empleado no encontrado.");
                return;
            }
            successResponse(ctx, 200, "Empleado encontrado", empleadoToDTO(empleado));
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de empleado inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Empleado empleado = ctx.bodyAsClass(Empleado.class);

            if (empleado.getNombre() == null || empleado.getEmail() == null || empleado.getPassword() == null) {
                errorResponse(ctx, 400, "Nombre, email y contraseña son obligatorios.");
                return;
            }
            
            empleado.setPassword(Password.hash(empleado.getPassword()).withBcrypt().getResult());
            if (empleado.getIdRol() == 0) {
                empleado.setIdRol(2); // Rol Estilista por defecto si no se especifica
            }

            Empleado empleadoCreado = empleadoService.create(empleado);
            
            Map<String, Object> data = new HashMap<>();
            data.put("idEmpleado", empleadoCreado.getIdEmpleado());
            data.put("nombre", empleadoCreado.getNombre());
            data.put("puesto", empleadoCreado.getPuesto());

            successResponse(ctx, 201, "Empleado creado exitosamente", data);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Empleado dataToUpdate = ctx.bodyAsClass(Empleado.class);
            
            if (empleadoService.findById(id) == null) {
                errorResponse(ctx, 404, "Empleado no encontrado para actualizar.");
                return;
            }

            dataToUpdate.setIdEmpleado(id);
            if (empleadoService.update(id, dataToUpdate)) {
                successResponse(ctx, 200, "Empleado actualizado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el empleado.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de empleado inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (empleadoService.findById(id) == null) {
                errorResponse(ctx, 404, "Empleado no encontrado para eliminar.");
                return;
            }
            if (empleadoService.delete(id)) {
                successResponse(ctx, 200, "Empleado eliminado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el empleado.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de empleado inválido.");
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
