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
        // Rutas CRUD estándar para Citas
        app.get("/api/citas", citaController::getAll);
        app.post("/api/citas", citaController::create);
        app.get("/api/citas/{id}", citaController::getById);
        app.put("/api/citas/{id}", citaController::update);
        app.delete("/api/citas/{id}", citaController::delete);

        // Ruta para actualizar solo el estado
        app.put("/api/citas/{id}/estado", citaController::updateEstado);

        // Rutas de consulta específicas
        app.get("/api/citas/cliente/{idCliente}", citaController::getByCliente);
        app.get("/api/citas/estilista/{idEstilista}", citaController::getByEstilista);
        app.get("/api/citas/mes/{mes}/{year}", citaController::getByMonth);
    }
}
