package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.PreguntaController;

public class PreguntaRouter implements IRouter {
    private final PreguntaController preguntaController;

    public PreguntaRouter(PreguntaController controller) {
        this.preguntaController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/preguntas", preguntaController::getAll);
        app.post("/api/preguntas", preguntaController::create);
        app.get("/api/preguntas/{id}", preguntaController::getById);
        app.put("/api/preguntas/{id}", preguntaController::update);
        app.delete("/api/preguntas/{id}", preguntaController::delete);
    }
}
