package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.UsuarioController;

public class UsuarioRouter implements IRouter {
    private final UsuarioController usuarioController;

    public UsuarioRouter(UsuarioController controller) {
        this.usuarioController = controller;
    }

    @Override
    public void register(Javalin app) {
        // Rutas de autenticación
        app.post("/api/register", usuarioController::register);
        app.post("/api/login", usuarioController::login);

        // Rutas para la gestión de usuarios
        app.get("/api/usuarios/{id}", usuarioController::getById);
        app.put("/api/usuarios/{id}", usuarioController::updateUser);
        app.delete("/api/usuarios/{id}", usuarioController::deleteUser);

        // Rutas para la gestión completa de empleados (que incluye un usuario)
        // Estas rutas son más específicas y es mejor mantenerlas separadas.
        app.post("/api/empleados/completo", usuarioController::registrarEmpleadoCompleto);
        app.put("/api/empleados/completo", usuarioController::updateEmpleadoCompleto);
    }
}
