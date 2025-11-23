package org.pi;

import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.json.JavalinJackson;
import org.pi.Config.DBconfig;
import org.pi.Config.configModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.json.JavalinJackson;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            // Inicializa la conexión a la base de datos
            DBconfig.getDataSource();

            Javalin app = Javalin.create(config -> {
                config.bundledPlugins.enableCors(cors -> cors.addRule(it -> {
                    config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                        mapper.registerModule(new JavaTimeModule());
                    }));
                    it.reflectClientOrigin = true;
                    it.allowCredentials = true;
                }));
            });

            // Registrar todas las rutas de los módulos
            configModule.initCategoriaRouter().register(app);
            configModule.initCitaRouter().register(app);
            configModule.initComentarioRouter().register(app);
            configModule.initUsuarioRouter().register(app);
            configModule.initEmpleadoRouter().register(app);
            configModule.initEstilistaRouter().register(app);
            configModule.initFormularioRouter().register(app);
            configModule.initHorarioRouter().register(app);
            configModule.initPortafolioRouter().register(app);
            configModule.initPreguntaRouter().register(app);
            configModule.initPromocionRouter().register(app);
            configModule.initRolRouter().register(app);
            configModule.initServicioRouter().register(app);
            configModule.initValoracionRouter().register(app);
            configModule.initNotificacionRouter().register(app); // <-- LÍNEA AÑADIDA

            // Endpoint de documentación
            app.get("/api/docs", ctx -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "API de GlamSoft funcionando. Visita la documentación para más detalles.");
                ctx.status(200).json(response);
            });

            // Manejo de errores globales
            app.exception(NotFoundResponse.class, (e, ctx) -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Recurso no encontrado: " + ctx.path());
                ctx.status(404).json(response);
            });

            app.exception(Exception.class, (e, ctx) -> {
                System.err.println("Error no capturado: " + e.getMessage()); // Log para depuración
                e.printStackTrace(); // Log para depuración
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Error interno del servidor: " + e.getClass().getSimpleName());
                ctx.status(500).json(response);
            });

            // Iniciar el servidor
            app.start(7000);
            System.out.println("Servidor iniciado en http://localhost:7000");

        } catch (Exception e) {
            System.err.println("Ocurrió un error crítico durante el inicio de la aplicación:");
            e.printStackTrace();
        }
    }
}
