package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Empleado;
import org.pi.Models.Usuario;
import org.pi.Services.UsuarioService;
import com.password4j.Password;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UsuarioController {
    private final UsuarioService usuarioService;
    private final TokenManager tokenManager;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    public UsuarioController(UsuarioService usuarioService, TokenManager tokenManager) {
        this.usuarioService = usuarioService;
        this.tokenManager = tokenManager;
    }

    // --- Autenticación ---

    public void register(Context ctx) {
        try {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            if (usuario.getEmail() == null || !EMAIL_PATTERN.matcher(usuario.getEmail()).matches()) {
                errorResponse(ctx, 400, "El formato del email no es válido.");
                return;
            }
            if (usuario.getPassword() == null || usuario.getPassword().length() < 8) {
                errorResponse(ctx, 400, "La contraseña debe tener al menos 8 caracteres.");
                return;
            }
            if (usuarioService.findUserByEmail(usuario.getEmail()) != null) {
                errorResponse(ctx, 409, "El email ya está registrado.");
                return;
            }
            String passHashed = Password.hash(usuario.getPassword()).withBcrypt().getResult();
            usuario.setPassword(passHashed);
            if (usuario.getIdRol() == 0) {
                usuario.setIdRol(2); // Rol Cliente por defecto
            }
            int id = usuarioService.saveUser(usuario);
            String token = tokenManager.issueToken(String.valueOf(id));
            Map<String, Object> data = new HashMap<>();
            data.put("userId", id);
            data.put("token", token);
            successResponse(ctx, 201, "Usuario registrado con éxito", data);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void login(Context ctx) {
        try {
            Usuario credentials = ctx.bodyAsClass(Usuario.class);
            if (credentials.getEmail() == null || credentials.getPassword() == null) {
                errorResponse(ctx, 400, "Email y contraseña son obligatorios.");
                return;
            }
            Usuario userFromDB = usuarioService.findUserByEmail(credentials.getEmail());
            if (userFromDB == null) {
                errorResponse(ctx, 404, "Usuario no encontrado.");
                return;
            }
            if (Password.check(credentials.getPassword(), userFromDB.getPassword()).withBcrypt()) {
                String token = tokenManager.issueToken(String.valueOf(userFromDB.getIdUsuario()));
                Map<String, Object> data = new HashMap<>();
                data.put("userId", userFromDB.getIdUsuario());
                data.put("token", token);
                successResponse(ctx, 200, "Login exitoso", data);
            } else {
                errorResponse(ctx, 401, "Contraseña incorrecta.");
            }
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    // --- CRUD de Usuarios ---

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = usuarioService.findUserById(id);
            if (usuario == null) {
                errorResponse(ctx, 404, "Usuario no encontrado.");
                return;
            }
            // No devolver la contraseña en la respuesta
            usuario.setPassword(null);
            successResponse(ctx, 200, "Usuario encontrado", usuario);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID de usuario debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void updateUser(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            if (usuarioService.findUserById(id) == null) {
                errorResponse(ctx, 404, "Usuario no encontrado para actualizar.");
                return;
            }
            usuario.setIdUsuario(id);
            // Si se incluye una nueva contraseña, hashearla.
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                 if (usuario.getPassword().length() < 8) {
                    errorResponse(ctx, 400, "La contraseña debe tener al menos 8 caracteres.");
                    return;
                }
                usuario.setPassword(Password.hash(usuario.getPassword()).withBcrypt().getResult());
            }
            
            if (usuarioService.updateUser(usuario)) {
                successResponse(ctx, 200, "Usuario actualizado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el usuario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID de usuario debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void deleteUser(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (usuarioService.findUserById(id) == null) {
                errorResponse(ctx, 404, "Usuario no encontrado para eliminar.");
                return;
            }
            if (usuarioService.deleteUser(id)) {
                successResponse(ctx, 200, "Usuario eliminado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el usuario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID de usuario debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }
    
    // --- Métodos complejos para Empleados ---

    public void registrarEmpleadoCompleto(Context ctx) {
        try {
            Empleado empleado = ctx.bodyAsClass(Empleado.class);
            // Lógica de validación aquí...
            String hashedPass = Password.hash(empleado.getPassword()).withBcrypt().getResult();
            empleado.setPassword(hashedPass);
            int idUsuario = usuarioService.saveEmpleadoCompleto(empleado);
            successResponse(ctx, 201, "Empleado registrado con éxito", Map.of("idUsuario", idUsuario));
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error al registrar empleado: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void updateEmpleadoCompleto(Context ctx) {
        try {
            Empleado empleado = ctx.bodyAsClass(Empleado.class);
            Usuario usuario = new Usuario(empleado.getIdUsuario(), empleado.getEmail(), empleado.getPassword(), empleado.getIdRol());
            // Lógica de validación aquí...
            usuarioService.updateEmpleadoCompleto(usuario, empleado);
            successResponse(ctx, 200, "Empleado actualizado correctamente", null);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error en base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    // --- Métodos de ayuda ---

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
