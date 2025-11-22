package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.HorarioController;

public class HorarioRouter implements IRouter {
    private final HorarioController horarioController;

    public HorarioRouter(HorarioController controller) {
        this.horarioController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/horarios", horarioController::getAll);
        app.post("/api/horarios", horarioController::create);
        app.get("/api/horarios/{id}", horarioController::getById);
        app.put("/api/horarios/{id}", horarioController::update);
        app.delete("/api/horarios/{id}", horarioController::delete);
    }
}
