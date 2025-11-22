package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.PortafolioController;

public class PortafolioRouter implements IRouter {
    private final PortafolioController portafolioController;

    public PortafolioRouter(PortafolioController controller) {
        this.portafolioController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/portafolio", portafolioController::getAll);
        app.post("/api/portafolio", portafolioController::create);
        app.get("/api/portafolio/{id}", portafolioController::getById);
        app.put("/api/portafolio/{id}", portafolioController::update);
        app.delete("/api/portafolio/{id}", portafolioController::delete);
    }
}
