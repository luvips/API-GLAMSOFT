package org.pi.Controllers;

import io.javalin.http.Context;
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

    public void register(Context ctx) {
        try {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);

            if (usuario.getNombre() == null || usuario.getTelefono() == null || usuario.getEmail() == null || usuario.getPassword() == null) {
                errorResponse(ctx, 400, "Nombre, teléfono, email y contraseña son obligatorios.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(usuario.getEmail()).matches()) {
                errorResponse(ctx, 400, "El formato del email no es válido.");
                return;
            }
            if (usuario.getPassword().length() < 8) {
                errorResponse(ctx, 400, "La contraseña debe tener al menos 8 caracteres.");
                return;
            }
            if (usuarioService.findUserByEmail(usuario.getEmail()) != null || usuarioService.findUserByTelefono(usuario.getTelefono()) != null) {
                errorResponse(ctx, 409, "El email o teléfono ya están registrados.");
                return;
            }

            usuario.setPassword(Password.hash(usuario.getPassword()).withBcrypt().getResult());
            if (usuario.getIdRol() == 0) {
                usuario.setIdRol(3); // Rol Cliente por defecto
            }

            Usuario usuarioCreado = usuarioService.saveUser(usuario);

            Map<String, Object> data = new HashMap<>();
            data.put("idUsuario", usuarioCreado.getIdUsuario());
            data.put("nombre", usuarioCreado.getNombre());
            data.put("telefono", usuarioCreado.getTelefono());
            data.put("email", usuarioCreado.getEmail());
            data.put("rol", "Cliente"); // Asumiendo rol fijo en registro

            successResponse(ctx, 201, "Usuario registrado exitosamente", data);

        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void login(Context ctx) {
        try {
            Map<String, String> credentials = ctx.bodyAsClass(Map.class);
            String telefono = credentials.get("telefono");
            String password = credentials.get("password");

            if (telefono == null || password == null) {
                errorResponse(ctx, 400, "Teléfono y contraseña son obligatorios.");
                return;
            }

            Usuario userFromDB = usuarioService.findUserByTelefono(telefono);

            if (userFromDB == null) {
                errorResponse(ctx, 404, "Usuario no encontrado.");
                return;
            }

            if (Password.check(password, userFromDB.getPassword()).withBcrypt()) {
                String token = tokenManager.issueToken(String.valueOf(userFromDB.getIdUsuario()));
                
                Map<String, Object> usuarioData = new HashMap<>();
                usuarioData.put("idUsuario", userFromDB.getIdUsuario());
                usuarioData.put("nombre", userFromDB.getNombre());
                usuarioData.put("telefono", userFromDB.getTelefono());
                usuarioData.put("email", userFromDB.getEmail());
                usuarioData.put("rol", "Cliente"); // Debería obtenerse del rol real

                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("usuario", usuarioData);

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

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = usuarioService.findUserById(id);
            if (usuario == null) {
                errorResponse(ctx, 404, "Usuario no encontrado.");
                return;
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("idUsuario", usuario.getIdUsuario());
            data.put("nombre", usuario.getNombre());
            data.put("telefono", usuario.getTelefono());
            data.put("email", usuario.getEmail());
            data.put("idRol", usuario.getIdRol());
            data.put("rol", "Cliente"); // Debería obtenerse del rol real
            data.put("activo", usuario.isActivo());

            successResponse(ctx, 200, "Usuario recuperado", data);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID de usuario debe ser un número válido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void updateUser(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario dataToUpdate = ctx.bodyAsClass(Usuario.class);
            
            Usuario usuarioExistente = usuarioService.findUserById(id);
            if (usuarioExistente == null) {
                errorResponse(ctx, 404, "Usuario no encontrado para actualizar.");
                return;
            }

            if (dataToUpdate.getNombre() != null) usuarioExistente.setNombre(dataToUpdate.getNombre());
            if (dataToUpdate.getEmail() != null) usuarioExistente.setEmail(dataToUpdate.getEmail());
            if (dataToUpdate.getTelefono() != null) usuarioExistente.setTelefono(dataToUpdate.getTelefono());

            if (usuarioService.updateUser(usuarioExistente)) {
                Map<String, Object> data = new HashMap<>();
                data.put("idUsuario", usuarioExistente.getIdUsuario());
                data.put("nombre", usuarioExistente.getNombre());
                data.put("email", usuarioExistente.getEmail());
                data.put("telefono", usuarioExistente.getTelefono());
                successResponse(ctx, 200, "Usuario actualizado exitosamente", data);
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
                successResponse(ctx, 200, "Usuario eliminado exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el usuario.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID de usuario debe ser un número válido.");
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
