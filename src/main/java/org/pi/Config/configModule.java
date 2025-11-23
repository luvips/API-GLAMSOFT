package org.pi.Config;

import org.pi.Controllers.*;
import org.pi.Repositories.*;
import org.pi.Routers.*;
import org.pi.Services.*;
import org.pi.Controllers.TokenManager;

public class configModule {
    
    public static CategoriaRouter initCategoriaRouter() {
        return new CategoriaRouter(new CategoriaController(new CategoriaService(new CategoriaRepository())));
    }

    public static CitaRouter initCitaRouter() {
        NotificacionRepository notificacionRepository = new NotificacionRepository();
        NotificacionService notificacionService = new NotificacionService(notificacionRepository);
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        CitaRepository citaRepository = new CitaRepository();
        CitaService citaService = new CitaService(citaRepository, notificacionService, usuarioRepository);
        return new CitaRouter(new CitaController(citaService));
    }

    public static ComentarioRouter initComentarioRouter() {
        return new ComentarioRouter(new ComentarioController(new ComentarioService(new ComentarioRepository())));
    }

    public static EmpleadoRouter initEmpleadoRouter() {
        return new EmpleadoRouter(new EmpleadoController(new EmpleadoService(new EmpleadoRepository())));
    }

    public static EstilistaRouter initEstilistaRouter() {
        return new EstilistaRouter(new EstilistaController(new EstilistaService(new EstilistaRepository())));
    }

    public static FormularioRouter initFormularioRouter() {
        return new FormularioRouter(new FormularioController(new FormularioService(new FormularioRepository())));
    }

    public static HorarioRouter initHorarioRouter() {
        return new HorarioRouter(new HorarioController(new HorarioService(new HorarioRepository())));
    }

    public static PortafolioRouter initPortafolioRouter() {
        return new PortafolioRouter(new PortafolioController(new PortafolioService(new PortafolioRepository())));
    }

    public static PreguntaRouter initPreguntaRouter() {
        return new PreguntaRouter(new PreguntaController(new PreguntaService(new PreguntaRepository())));
    }

    public static PromocionRouter initPromocionRouter() {
        return new PromocionRouter(new PromocionController(new PromocionService(new PromocionRepository())));
    }

    public static RolRouter initRolRouter() {
        return new RolRouter(new RolController(new RolService(new RolRepository())));
    }

    public static ServicioRouter initServicioRouter() {
        return new ServicioRouter(new ServicioController(new ServicioService(new ServicioRepository())));
    }

    public static UsuarioRouter initUsuarioRouter() {
        return new UsuarioRouter(new UsuarioController(new UsuarioService(new UsuarioRepository()), new TokenManager()));
    }

    public static ValoracionRouter initValoracionRouter() {
        return new ValoracionRouter(new ValoracionController(new ValoracionService(new ValoracionRepository())));
    }
    
    public static NotificacionRouter initNotificacionRouter() {
        return new NotificacionRouter(new NotificacionController(new NotificacionService(new NotificacionRepository())));
    }
}
