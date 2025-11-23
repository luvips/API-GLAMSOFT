package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.NotificacionController;

public class NotificacionRouter implements IRouter {
    private final NotificacionController notificacionController;

    public NotificacionRouter(NotificacionController controller) {
        this.notificacionController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/usuarios/{idUsuario}/notificaciones", notificacionController::getNotificacionesByUsuario);
        app.put("/api/notificaciones/{id}/marcar-leida", notificacionController::marcarComoLeida);
        app.get("/api/usuarios/{idUsuario}/notificaciones/no-leidas", notificacionController::contarNoLeidas);
    }
}
