package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Cita;
import org.pi.Services.CitaService;
import org.pi.dto.CitaDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    public void getAll(Context ctx) {
        try {
            String estado = ctx.queryParam("estado");
            List<CitaDTO> citas;

            if (estado != null && !estado.isEmpty()) {
                citas = citaService.findByEstado(estado);
            } else {
                citas = citaService.findAll();
            }

            successResponse(ctx, 200, "Citas recuperadas correctamente", citas);

        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error en la base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            CitaDTO cita = citaService.findById(id);

            if (cita == null) {
                errorResponse(ctx, 404, "Cita no encontrada");
                return;
            }

            successResponse(ctx, 200, "Cita encontrada", cita);

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID de la cita debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error en la base de datos: " + e.getMessage());
        }
    }

    public void getByCliente(Context ctx) {
        try {
            int idCliente = Integer.parseInt(ctx.pathParam("idCliente"));
            List<CitaDTO> citas = citaService.findByCliente(idCliente);

            successResponse(ctx, 200, "Citas del cliente recuperadas correctamente", citas);

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del cliente debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error en la base de datos: " + e.getMessage());
        }
    }

    public void getByEstilista(Context ctx) {
        try {
            int idEstilista = Integer.parseInt(ctx.pathParam("idEstilista"));
            List<CitaDTO> citas = citaService.findByEstilista(idEstilista);

            successResponse(ctx, 200, "Citas del estilista recuperadas correctamente", citas);

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID del estilista debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error en la base de datos: " + e.getMessage());
        }
    }

    public void getByMonth(Context ctx) {
        try {
            int mes = Integer.parseInt(ctx.pathParam("mes"));
            int year = Integer.parseInt(ctx.pathParam("year"));
            List<CitaDTO> citas = citaService.findCitasMes(mes, year);

            successResponse(ctx, 200, "Citas del mes recuperadas correctamente", citas);

        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El mes y el año deben ser números válidos");
        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error en la base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Cita nuevaCita = ctx.bodyAsClass(Cita.class);
            int idGenerado = citaService.create(nuevaCita);

            successResponse(ctx, 201, "Cita creada exitosamente", Map.of("id", idGenerado));

        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (Exception e) { // Captura más amplia para problemas de deserialización
            errorResponse(ctx, 500, "Error al procesar la solicitud: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Cita citaActualizada = ctx.bodyAsClass(Cita.class);

            boolean actualizado = citaService.update(id, citaActualizada);

            if (!actualizado) {
                errorResponse(ctx, 404, "Cita no encontrada para actualizar");
                return;
            }

            successResponse(ctx, 200, "Cita actualizada correctamente", null);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID de la cita debe ser un número válido");
        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error al procesar la solicitud: " + e.getMessage());
        }
    }

    public void updateStatus(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Map<String, String> requestBody = ctx.bodyAsClass(Map.class);
            String nuevoEstado = requestBody.get("estado");

            if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                errorResponse(ctx, 400, "El campo 'estado' es obligatorio en el cuerpo de la solicitud");
                return;
            }

            boolean actualizado = citaService.updateEstado(id, nuevoEstado);

            if (!actualizado) {
                errorResponse(ctx, 404, "Cita no encontrada para actualizar estado");
                return;
            }

            successResponse(ctx, 200, "Estado de la cita actualizado correctamente", null);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID de la cita debe ser un número válido");
        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 500, "Error al procesar la solicitud: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean eliminado = citaService.delete(id);

            if (!eliminado) {
                errorResponse(ctx, 404, "Cita no encontrada para eliminar");
                return;
            }

            successResponse(ctx, 200, "Cita eliminada correctamente", null);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "El ID de la cita debe ser un número válido");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error en la base de datos: " + e.getMessage());
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
