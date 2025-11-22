package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.ServicioController;

public class ServicioRouter implements IRouter {
    private final ServicioController servicioController;

    public ServicioRouter(ServicioController controller) {
        this.servicioController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/servicios", servicioController::getAll);
        app.post("/api/servicios", servicioController::create);
        app.get("/api/servicios/{id}", servicioController::getById);
        app.put("/api/servicios/{id}", servicioController::update);
        app.delete("/api/servicios/{id}", servicioController::delete);
    }
}
