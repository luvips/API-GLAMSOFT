package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.CitaController;

public class CitaRouter implements IRouter {
    private final CitaController citaController;

    public CitaRouter(CitaController controller) {
        this.citaController = controller;
    }

    @Override
    public void register(Javalin app) {
        // CORRECCIÓN: Volviendo al método original de registro de rutas directas.
        // Esto es consistente con la arquitectura del proyecto y evita los errores.
        app.get("/api/citas", citaController::getAll);
        app.post("/api/citas", citaController::create);
        app.get("/api/citas/{id}", citaController::getById);
        app.put("/api/citas/{id}", citaController::update);
        app.delete("/api/citas/{id}", citaController::delete);
        app.put("/api/citas/{id}/estado", citaController::updateStatus);
        app.get("/api/citas/cliente/{idCliente}", citaController::getByCliente);
        app.get("/api/citas/estilista/{idEstilista}", citaController::getByEstilista);
        app.get("/api/citas/mes/{mes}/{year}", citaController::getByMonth);
    }
}
