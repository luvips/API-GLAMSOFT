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
        app.post("/api/estilistas", estilistaController::create);
        app.get("/api/estilistas/{id}", estilistaController::getById);
        app.put("/api/estilistas/{id}", estilistaController::update);
        app.delete("/api/estilistas/{id}", estilistaController::delete);
        
        // Ruta para encontrar estilistas disponibles para un servicio específico
        app.post("/api/estilistas/servicio/{idServicio}", estilistaController::findEstilistaServicio);
    }
}
