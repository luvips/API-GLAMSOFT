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
            System.out.println("Iniciando registro..."); // Log para depuración
            Usuario usuario = ctx.bodyAsClass(Usuario.class);

            // 1. Validaciones básicas
            if (usuario.getNombre() == null || usuario.getTelefono() == null || usuario.getEmail() == null || usuario.getPassword() == null) {
                errorResponse(ctx, 400, "Todos los campos son obligatorios.");
                return;
            }

            if (!EMAIL_PATTERN.matcher(usuario.getEmail()).matches()) {
                errorResponse(ctx, 400, "Email inválido.");
                return;
            }

            // 2. Verificar duplicados
            if (usuarioService.findUserByEmail(usuario.getEmail()) != null) {
                errorResponse(ctx, 409, "El email ya está registrado.");
                return;
            }
            if (usuarioService.findUserByTelefono(usuario.getTelefono()) != null) {
                errorResponse(ctx, 409, "El teléfono ya está registrado.");
                return;
            }

            // 3. Preparar usuario
            usuario.setPassword(Password.hash(usuario.getPassword()).withBcrypt().getResult());

            // Lógica de Rol: Si no envían rol, es 3 (Cliente). Si envían 1, es Admin.
            if (usuario.getIdRol() == 0) {
                usuario.setIdRol(3);
            }

            // 4. Guardar en BD
            Usuario usuarioCreado = usuarioService.saveUser(usuario);

            System.out.println("Usuario guardado ID: " + usuarioCreado.getIdUsuario()); // Log

            // 5. Construir respuesta
            Map<String, Object> data = new HashMap<>();
            data.put("idUsuario", usuarioCreado.getIdUsuario());
            data.put("nombre", usuarioCreado.getNombre());
            data.put("email", usuarioCreado.getEmail());
            // Enviamos el rol real para que el frontend sepa qué hacer
            data.put("idRol", usuarioCreado.getIdRol());
            data.put("rol", usuarioCreado.getIdRol() == 1 ? "Admin" : "Cliente");

            successResponse(ctx, 201, "Usuario registrado exitosamente", data);

        } catch (SQLException e) {
            e.printStackTrace(); // Ver error en consola del servidor
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Ver error en consola del servidor
            errorResponse(ctx, 400, "Error en la solicitud: " + e.getMessage());
        }
    }

    public void login(Context ctx) {
        try {
            Map<String, String> credentials = ctx.bodyAsClass(Map.class);
            String telefono = credentials.get("telefono");
            String email = credentials.get("email");
            String password = credentials.get("password");

            Usuario userFromDB = null;

            if (telefono != null && !telefono.isEmpty()) {
                userFromDB = usuarioService.findUserByTelefono(telefono);
            } else if (email != null && !email.isEmpty()) {
                userFromDB = usuarioService.findUserByEmail(email);
            } else {
                errorResponse(ctx, 400, "Debes ingresar teléfono o email.");
                return;
            }

            if (userFromDB == null) {
                errorResponse(ctx, 404, "Usuario no encontrado.");
                return;
            }

            if (Password.check(password, userFromDB.getPassword()).withBcrypt()) {
                String token = tokenManager.issueToken(String.valueOf(userFromDB.getIdUsuario()));

                Map<String, Object> usuarioData = new HashMap<>();
                usuarioData.put("idUsuario", userFromDB.getIdUsuario());
                usuarioData.put("nombre", userFromDB.getNombre());
                usuarioData.put("email", userFromDB.getEmail());
                // Enviamos el rol real de la BD
                usuarioData.put("idRol", userFromDB.getIdRol());
                usuarioData.put("rol", userFromDB.getIdRol() == 1 ? "Admin" : "Cliente");

                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("usuario", usuarioData);

                successResponse(ctx, 200, "Login exitoso", data);
            } else {
                errorResponse(ctx, 401, "Contraseña incorrecta.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorResponse(ctx, 500, "Error interno: " + e.getMessage());
        }
    }

    // ... (Mantén los métodos getById, updateUser, deleteUser, etc. si los necesitas, o pégalos del archivo anterior)

    // --- Métodos de ayuda ---
    // IMPORTANTE: Asegúrate de que estos métodos estén al final de la clase

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = usuarioService.findUserById(id);
            if (usuario == null) {
                errorResponse(ctx, 404, "Usuario no encontrado.");
                return;
            }

            // Crear un mapa para la respuesta para controlar qué datos enviamos
            Map<String, Object> data = new HashMap<>();
            data.put("idUsuario", usuario.getIdUsuario());
            data.put("nombre", usuario.getNombre());
            data.put("email", usuario.getEmail());
            data.put("telefono", usuario.getTelefono());
            data.put("idRol", usuario.getIdRol());
            data.put("activo", usuario.isActivo());

            successResponse(ctx, 200, "Usuario encontrado", data);
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error: " + e.getMessage());
        }
    }

    public void updateUser(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario dataToUpdate = ctx.bodyAsClass(Usuario.class);
            Usuario usuarioExistente = usuarioService.findUserById(id);

            if (usuarioExistente == null) {
                errorResponse(ctx, 404, "Usuario no encontrado.");
                return;
            }

            if (dataToUpdate.getNombre() != null) usuarioExistente.setNombre(dataToUpdate.getNombre());
            if (dataToUpdate.getEmail() != null) usuarioExistente.setEmail(dataToUpdate.getEmail());
            if (dataToUpdate.getTelefono() != null) usuarioExistente.setTelefono(dataToUpdate.getTelefono());
            if (dataToUpdate.getIdRol() != 0) usuarioExistente.setIdRol(dataToUpdate.getIdRol());

            if (usuarioService.updateUser(usuarioExistente)) {
                successResponse(ctx, 200, "Usuario actualizado", usuarioExistente);
            } else {
                errorResponse(ctx, 500, "Error al actualizar.");
            }
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error: " + e.getMessage());
        }
    }

    public void deleteUser(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (usuarioService.deleteUser(id)) {
                successResponse(ctx, 200, "Usuario eliminado", null);
            } else {
                errorResponse(ctx, 404, "No se pudo eliminar o no existe.");
            }
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error: " + e.getMessage());
        }
    }

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