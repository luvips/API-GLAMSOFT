package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.EstilistaController;

public class EstilistaRouter implements IRouter {
    private final EstilistaController estilistaController;

    public EstilistaRouter(EstilistaController controller) {
        this.estilistaController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/estilistas", estilistaController::getAll);
        app.get("/api/estilistas/{id}", estilistaController::getById);
        app.post("/api/estilistas/servicio/{idServicio}", estilistaController::findEstilistaServicio);
    }
}
