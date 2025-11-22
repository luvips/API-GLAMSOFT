package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.RolController;

public class RolRouter implements IRouter {
    private final RolController rolController;

    public RolRouter(RolController controller) {
        this.rolController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/roles", rolController::getAll);
        app.post("/api/roles", rolController::create);
        app.get("/api/roles/{id}", rolController::getById);
        app.put("/api/roles/{id}", rolController::update);
        app.delete("/api/roles/{id}", rolController::delete);
    }
}
