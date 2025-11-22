package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.ComentarioController;

public class ComentarioRouter implements IRouter {
    private final ComentarioController comentarioController;

    public ComentarioRouter(ComentarioController controller) {
        this.comentarioController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/comentarios", comentarioController::getAll);
        app.post("/api/comentarios", comentarioController::create);
        app.get("/api/comentarios/{id}", comentarioController::getById);
        app.put("/api/comentarios/{id}", comentarioController::update);
        app.delete("/api/comentarios/{id}", comentarioController::delete);
        app.get("/api/comentarios/cliente/{idCliente}", comentarioController::getByCliente);
    }
}
