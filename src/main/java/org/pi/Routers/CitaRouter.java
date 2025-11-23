package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.CitaController;

public class CitaRouter implements IRouter {
    private final CitaController citaController;

    public CitaRouter(CitaController citaController) {
        this.citaController = citaController;
    }

    @Override
    public void register(Javalin app) {
        // --- Flujo de aprobación y gestión de estado ---
        app.get("/api/citas/pendientes", citaController::getCitasPendientes);
        app.put("/api/citas/{id}/aprobar", citaController::aprobarCita);
        app.put("/api/citas/{id}/rechazar", citaController::rechazarCita);
        app.put("/api/citas/{id}/completar", citaController::completarCita);
        app.put("/api/citas/{id}/cancelar", citaController::cancelarCita);

        // --- Rutas CRUD básicas ---
        app.get("/api/citas", citaController::getAll);
        app.post("/api/citas", citaController::create);
        app.get("/api/citas/{id}", citaController::getById);
    }
}
