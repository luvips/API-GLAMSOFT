package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.ValoracionController;

public class ValoracionRouter implements IRouter {
    private final ValoracionController valoracionController;

    public ValoracionRouter(ValoracionController controller) {
        this.valoracionController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/valoraciones", valoracionController::getAll);
        app.post("/api/valoraciones", valoracionController::create);
        app.get("/api/valoraciones/{id}", valoracionController::getById);
        app.put("/api/valoraciones/{id}", valoracionController::update);
        app.delete("/api/valoraciones/{id}", valoracionController::delete);
    }
}
