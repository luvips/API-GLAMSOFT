package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.FormularioController;

public class FormularioRouter implements IRouter {
    private final FormularioController formularioController;

    public FormularioRouter(FormularioController controller) {
        this.formularioController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/formularios", formularioController::getAll);
        app.post("/api/formularios", formularioController::create);
        app.get("/api/formularios/{id}", formularioController::getById);
        app.put("/api/formularios/{id}", formularioController::update);
        app.delete("/api/formularios/{id}", formularioController::delete);
    }
}
