package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Horario;
import org.pi.Services.HorarioService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    public void getAll(Context ctx) {
        try {
            List<Horario> horarios = horarioService.findAll();
            successResponse(ctx, 200, "Horarios recuperados correctamente", horarios);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al obtener horarios: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Horario horario = horarioService.findById(id);

            if (horario == null) {
                errorResponse(ctx, 404, "Horario no encontrado");
                return;
            }
            successResponse(ctx, 200, "Horario encontrado", horario);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del horario debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al buscar el horario: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Horario horario = ctx.bodyAsClass(Horario.class);

            // Validaciones
            if (horario.getHoraInicio() == null || horario.getHoraFin() == null) {
                errorResponse(ctx, 400, "La hora de inicio y fin son obligatorias");
                return;
            }
            if (horario.getHoraFin().isBefore(horario.getHoraInicio())) {
                errorResponse(ctx, 400, "La hora de fin no puede ser anterior a la de inicio.");
                return;
            }
            if (horario.getDiaSemana() == null || horario.getDiaSemana().trim().isEmpty()) {
                errorResponse(ctx, 400, "El día de la semana es obligatorio");
                return;
            }

            int idGenerado = horarioService.save(horario);
            Map<String, Integer> data = new HashMap<>();
            data.put("id", idGenerado);
            successResponse(ctx, 201, "Horario creado exitosamente", data);

        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al crear el horario: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Horario horario = ctx.bodyAsClass(Horario.class);

            // Verificar existencia
            Horario existente = horarioService.findById(id);
            if (existente == null) {
                errorResponse(ctx, 404, "Horario no encontrado para actualizar");
                return;
            }

            // Validaciones
            if (horario.getHoraInicio() == null || horario.getHoraFin() == null) {
                errorResponse(ctx, 400, "La hora de inicio y fin son obligatorias");
                return;
            }
            if (horario.getHoraFin().isBefore(horario.getHoraInicio())) {
                errorResponse(ctx, 400, "La hora de fin no puede ser anterior a la de inicio.");
                return;
            }
            if (horario.getDiaSemana() == null || horario.getDiaSemana().trim().isEmpty()) {
                errorResponse(ctx, 400, "El día de la semana es obligatorio");
                return;
            }
            
            horario.setIdHorario(id);
            boolean actualizado = horarioService.update(horario);

            if (actualizado) {
                successResponse(ctx, 200, "Horario actualizado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar el horario.");
            }

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del horario debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al actualizar el horario: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));

            // Verificar existencia
            Horario existente = horarioService.findById(id);
            if (existente == null) {
                errorResponse(ctx, 404, "Horario no encontrado para eliminar");
                return;
            }

            boolean eliminado = horarioService.delete(id);
            if (eliminado) {
                successResponse(ctx, 200, "Horario eliminado correctamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar el horario.");
            }

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del horario debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos al eliminar el horario: " + e.getMessage());
        }
    }

    // --- Métodos de ayuda para respuestas ---

    private void successResponse(Context ctx, int statusCode, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        ctx.status(statusCode).json(response);
    }

    private void errorResponse(Context ctx, int statusCode, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        ctx.status(statusCode).json(response);
    }
}
