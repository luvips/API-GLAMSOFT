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
        
        // Ruta GET para encontrar estilistas por servicio (estándar)
        app.get("/api/estilistas/servicios/{idServicio}", estilistaController::getEstilistasByServicio);
        
        // Ruta POST para encontrar estilistas por servicio (legado/alternativa)
        app.post("/api/estilistas/servicio/{idServicio}", estilistaController::getEstilistasByServicio);
        app.post("/api/estilistas/servicios", estilistaController::createServicio);
        app.post("/api/estilistas/horarios", estilistaController::createHorario);

    }
}
