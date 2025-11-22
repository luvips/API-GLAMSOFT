package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.PromocionController;

public class PromocionRouter implements IRouter {
    private final PromocionController promocionController;

    public PromocionRouter(PromocionController controller) {
        this.promocionController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/promociones", promocionController::getAll);
        app.post("/api/promociones", promocionController::create);
        app.get("/api/promociones/{id}", promocionController::getById);
        app.put("/api/promociones/{id}", promocionController::update);
        app.delete("/api/promociones/{id}", promocionController::delete);
        
        app.get("/api/promociones/{id}/servicios", promocionController::getServiciosFromPromocion);
        app.post("/api/promociones/{id}/servicios", promocionController::addServicioToPromocion);
    }
}
