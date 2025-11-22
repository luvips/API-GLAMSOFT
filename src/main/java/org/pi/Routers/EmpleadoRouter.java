package org.pi.Routers;

import io.javalin.Javalin;
import org.pi.Config.IRouter;
import org.pi.Controllers.EmpleadoController;

public class EmpleadoRouter implements IRouter {
    private final EmpleadoController empleadoController;

    public EmpleadoRouter(EmpleadoController controller) {
        this.empleadoController = controller;
    }

    @Override
    public void register(Javalin app) {
        app.get("/api/empleados", empleadoController::getAll);
        app.post("/api/empleados", empleadoController::create);
        app.get("/api/empleados/{id}", empleadoController::getById);
        app.put("/api/empleados/{id}", empleadoController::update);
        app.delete("/api/empleados/{id}", empleadoController::delete);
    }
}
