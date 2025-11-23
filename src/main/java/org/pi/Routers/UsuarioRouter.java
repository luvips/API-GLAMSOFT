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

        // Rutas para la gestión de usuarios (CRUD)
        app.get("/api/usuarios/{id}", usuarioController::getById);
        app.put("/api/usuarios/{id}", usuarioController::updateUser);
        app.delete("/api/usuarios/{id}", usuarioController::deleteUser);
        app.get("/api/usuarios/telefono/{telefono}", usuarioController::getByTelefono);

    }
}