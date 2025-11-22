package org.pi;
import io.javalin.Javalin;
import org.pi.Config.DBconfig;
import org.pi.Config.configModule;
public class Main {
   
    public static void main(String[] args) {
        try {
            // Initialize the database connection
            DBconfig.getDataSource();

            Javalin app = Javalin.create(config -> {

                config.bundledPlugins.enableCors(cors -> {
                    cors.addRule(rule -> {
                        rule.reflectClientOrigin = true;
                        rule.allowCredentials = true;
                    });
                });

            });

            // Registrar rutas ANTES de iniciar el servidor
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

            // Ahora sí iniciar
            app.start(7000);

            app.events(event -> {
                event.serverStartFailed(() -> {});
                event.serverStarted(() -> {
                    app.jettyServer().server().getConnectors()[0]
                            .getConnectionFactories()
                            .removeIf(cf -> cf.getProtocol().startsWith("h2"));
                });
            });
        } catch (Exception e) {
            System.err.println("An error occurred during application startup:");
            e.printStackTrace();
        }
    }
}
