package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Empleado;
import org.pi.Services.EmpleadoService;
import com.password4j.Password;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EmpleadoController {
    private final EmpleadoService empleadoService;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    public void getAll(Context ctx) {
        try {
            List<Empleado> empleados = empleadoService.getAll();
            successResponse(ctx, 200, "Empleados recuperados", empleados);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Empleado empleado = empleadoService.getById(id);
            if (empleado != null) {
                successResponse(ctx, 200, "Empleado encontrado", empleado);
            } else {
                errorResponse(ctx, 404, "Empleado no encontrado");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID inválido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error: " + e.getMessage());
        }
    }

    public void getByRol(Context ctx) {
        try {
            int idRol = Integer.parseInt(ctx.pathParam("idRol"));
            List<Empleado> empleados = empleadoService.getByRol(idRol);
            successResponse(ctx, 200, "Empleados recuperados por rol", empleados);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de rol inválido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error: " + e.getMessage());
        }
    }

    // ✅ MÉTODO CREATE CORREGIDO Y ROBUSTO
    public void create(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            // 1. Extraemos IDs con el helper seguro (acepta String o Number)
            Integer idUsuario = parseIntSafe(body.get("idUsuario"));
            Integer idRol = parseIntSafe(body.get("idRol"));

            // Creamos el objeto
            Empleado empleado = new Empleado();
            empleado.setNombre((String) body.get("nombre"));
            empleado.setEmail((String) body.get("email"));
            empleado.setTelefono((String) body.get("telefono"));
            empleado.setPuesto((String) body.get("puesto"));
            empleado.setImagenPerfil((String) body.get("imagenPerfil"));
            empleado.setActivo(true);

            // Asignar rol (default 2 si es nulo)
            empleado.setIdRol(idRol != null ? idRol : 2);

            // --- ESCENARIO A: VINCULAR USUARIO EXISTENTE ---
            if (idUsuario != null && idUsuario > 0) {
                empleado.setIdUsuario(idUsuario);

                if (empleado.getPuesto() == null || empleado.getTelefono() == null) {
                    errorResponse(ctx, 400, "Puesto y teléfono son obligatorios.");
                    return;
                }

                try {
                    Empleado creado = empleadoService.create(empleado);
                    successResponse(ctx, 201, "Estilista vinculado correctamente", creado);
                } catch (Exception e) {
                    if (e.getMessage().toLowerCase().contains("duplicate")) {
                        errorResponse(ctx, 409, "El usuario ya está registrado como empleado.");
                    } else {
                        throw e;
                    }
                }
                return;
            }

            // --- ESCENARIO B: USUARIO NUEVO ---
            String password = (String) body.get("password");

            if (empleado.getNombre() == null || empleado.getEmail() == null || password == null) {
                errorResponse(ctx, 400, "Nombre, email y contraseña obligatorios para nuevos registros.");
                return;
            }

            if (!EMAIL_PATTERN.matcher(empleado.getEmail()).matches()) {
                errorResponse(ctx, 400, "Email inválido.");
                return;
            }

            empleado.setPassword(Password.hash(password).withBcrypt().getResult());

            Empleado creado = empleadoService.create(empleado);
            successResponse(ctx, 201, "Empleado creado exitosamente", creado);

        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error BD: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            errorResponse(ctx, 400, "Error en solicitud: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            // Usamos Map para flexibilidad
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            Empleado empleado = empleadoService.getById(id);
            if (empleado == null) {
                errorResponse(ctx, 404, "Empleado no encontrado.");
                return;
            }

            // Actualizamos campos si vienen en el JSON
            if (body.containsKey("puesto")) empleado.setPuesto((String) body.get("puesto"));
            if (body.containsKey("imagenPerfil")) empleado.setImagenPerfil((String) body.get("imagenPerfil"));

            // Si también quieres permitir actualizar datos de usuario base (nombre/tel) desde aquí:
            if (body.containsKey("nombre")) empleado.setNombre((String) body.get("nombre"));
            if (body.containsKey("telefono")) empleado.setTelefono((String) body.get("telefono"));
            if (body.containsKey("email")) empleado.setEmail((String) body.get("email"));

            if (empleadoService.update(empleado)) {
                successResponse(ctx, 200, "Estilista actualizado correctamente", empleado);
            } else {
                errorResponse(ctx, 500, "No se pudieron guardar los cambios.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorResponse(ctx, 500, "Error al actualizar: " + e.getMessage());
        }
    }

    // ✅ HELPER PARA PARSEAR NUMEROS SEGUROS (STRING O NUMBER)
    private Integer parseIntSafe(Object value) {
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private void successResponse(Context ctx, int statusCode, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        if (data != null) response.put("data", data);
        ctx.status(statusCode).json(response);
    }

    private void errorResponse(Context ctx, int statusCode, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        ctx.status(statusCode).json(response);
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

}
