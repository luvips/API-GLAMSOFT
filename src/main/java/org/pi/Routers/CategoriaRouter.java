package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.CategoriaController;

public class CategoriaRouter implements IRouter {
    private final CategoriaController categoriaController;

    public CategoriaRouter(CategoriaController controller) {
        this.categoriaController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/categorias", categoriaController::getAll);
        app.post("/api/categorias", categoriaController::create);
        app.get("/api/categorias/{id}", categoriaController::getById);
        app.put("/api/categorias/{id}", categoriaController::update);
        app.delete("/api/categorias/{id}", categoriaController::delete);
    }
}
