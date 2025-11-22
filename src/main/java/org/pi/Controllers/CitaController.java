package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Cita;
import org.pi.Services.CitaService;
import org.pi.dto.CitaDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    // --- TRANSFORMADORES DE DATOS PARA RESPUESTAS JSON ---

    private Map<String, Object> citaToDetailedJson(CitaDTO cita) {
        Map<String, Object> json = new HashMap<>();
        json.put("idCita", cita.getIdCita());
        json.put("fecha", cita.getFechaHoraCita().toLocalDate().toString());
        json.put("hora", cita.getFechaHoraCita().toLocalTime().toString());
        json.put("estado", cita.getEstadoCita().toLowerCase());
        json.put("notas", cita.getNotas());
        
        Map<String, Object> clienteJson = new HashMap<>();
        clienteJson.put("idCliente", cita.getIdCliente());
        clienteJson.put("nombre", cita.getNombreCliente());
        clienteJson.put("telefono", cita.getTelefonoCliente());
        json.put("cliente", clienteJson);

        Map<String, Object> estilistaJson = new HashMap<>();
        estilistaJson.put("idEstilista", cita.getIdEstilista());
        estilistaJson.put("nombre", cita.getNombreEstilista());
        json.put("estilista", estilistaJson);

        List<Map<String, Object>> serviciosJson = cita.getServicios().stream().map(s -> {
            Map<String, Object> servicioMap = new HashMap<>();
            servicioMap.put("idServicio", s.getIdServicio());
            servicioMap.put("nombre", s.getNombre());
            servicioMap.put("precio", s.getPrecio());
            return servicioMap;
        }).collect(Collectors.toList());
        json.put("servicios", serviciosJson);
        
        json.put("precioTotal", cita.getPrecioTotal());
        return json;
    }

    // --- ENDPOINTS ---

    public void getAll(Context ctx) {
        try {
            String estado = ctx.queryParam("estado");
            String fecha = ctx.queryParam("fecha");
            List<CitaDTO> citas = citaService.findAll(estado, fecha);
            
            List<Map<String, Object>> jsonResponse = citas.stream()
                .map(this::citaToDetailedJson)
                .collect(Collectors.toList());

            successResponse(ctx, 200, "Citas recuperadas", jsonResponse);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            CitaDTO cita = citaService.findById(id);
            if (cita == null) {
                errorResponse(ctx, 404, "Cita no encontrada.");
                return;
            }
            successResponse(ctx, 200, "Cita encontrada", citaToDetailedJson(cita));
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de cita inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            // Leer el cuerpo como un mapa genérico para manejar fecha y hora por separado
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            
            Cita nuevaCita = new Cita();
            nuevaCita.setIdCliente((Integer) body.get("idCliente"));
            nuevaCita.setIdEstilista((Integer) body.get("idEstilista"));
            // El campo 'notas' no existe en el modelo Cita.java, se omite.
            
            LocalDate fecha = LocalDate.parse((String) body.get("fecha"));
            LocalTime hora = LocalTime.parse((String) body.get("hora"));
            nuevaCita.setFechaHoraCita(LocalDateTime.of(fecha, hora)); // CORREGIDO

            List<Integer> servicios = (List<Integer>) body.get("servicios");

            Cita citaCreada = citaService.create(nuevaCita, servicios);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("idCita", citaCreada.getIdCita());
            responseData.put("fecha", citaCreada.getFechaHoraCita().toLocalDate().toString()); // CORREGIDO
            responseData.put("hora", citaCreada.getFechaHoraCita().toLocalTime().toString());   // CORREGIDO
            responseData.put("estado", citaCreada.getEstadoCita().toLowerCase());
            
            successResponse(ctx, 201, "Cita creada exitosamente", responseData);

        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Formato de JSON inválido: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            Cita citaActualizada = new Cita();
            citaActualizada.setIdEstilista((Integer) body.get("idEstilista"));
            // El campo 'notas' no existe en el modelo Cita.java, se omite.
            
            LocalDate fecha = LocalDate.parse((String) body.get("fecha"));
            LocalTime hora = LocalTime.parse((String) body.get("hora"));
            citaActualizada.setFechaHoraCita(LocalDateTime.of(fecha, hora)); // CORREGIDO

            if (citaService.update(id, citaActualizada)) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("idCita", id);
                responseData.put("fecha", fecha.toString());
                responseData.put("hora", hora.toString());
                responseData.put("estado", "pendiente");
                successResponse(ctx, 200, "Cita actualizada exitosamente", responseData);
            } else {
                errorResponse(ctx, 404, "Cita no encontrada.");
            }
        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Formato de JSON inválido: " + e.getMessage());
        }
    }

    public void updateEstado(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String nuevoEstado = body.get("estado");

            if (citaService.updateEstado(id, nuevoEstado)) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("idCita", id);
                responseData.put("estado", nuevoEstado);
                responseData.put("fechaActualizacion", LocalDateTime.now().toString());
                successResponse(ctx, 200, "Estado de cita actualizado exitosamente", responseData);
            } else {
                errorResponse(ctx, 404, "Cita no encontrada.");
            }
        } catch (IllegalArgumentException e) {
            errorResponse(ctx, 400, e.getMessage());
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (citaService.delete(id)) {
                successResponse(ctx, 200, "Cita eliminada exitosamente", null);
            } else {
                errorResponse(ctx, 404, "Cita no encontrada.");
            }
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }
    
    public void getByCliente(Context ctx) {
        try {
            int idCliente = Integer.parseInt(ctx.pathParam("idCliente"));
            List<CitaDTO> citas = citaService.findByCliente(idCliente);
            List<Map<String, Object>> response = citas.stream().map(c -> {
                Map<String, Object> simpleJson = new HashMap<>();
                simpleJson.put("idCita", c.getIdCita());
                simpleJson.put("fecha", c.getFechaHoraCita().toLocalDate().toString());
                simpleJson.put("hora", c.getFechaHoraCita().toLocalTime().toString());
                simpleJson.put("estado", c.getEstadoCita().toLowerCase());
                simpleJson.put("estilista", c.getNombreEstilista());
                simpleJson.put("servicios", c.getServicios().stream().map(CitaDTO.ServicioDTO::getNombre).collect(Collectors.toList()));
                return simpleJson;
            }).collect(Collectors.toList());
            successResponse(ctx, 200, "Citas del cliente recuperadas", response);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de cliente inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getByEstilista(Context ctx) {
        try {
            int idEstilista = Integer.parseInt(ctx.pathParam("idEstilista"));
            List<CitaDTO> citas = citaService.findByEstilista(idEstilista);
            List<Map<String, Object>> response = citas.stream().map(c -> {
                Map<String, Object> simpleJson = new HashMap<>();
                simpleJson.put("idCita", c.getIdCita());
                simpleJson.put("fecha", c.getFechaHoraCita().toLocalDate().toString());
                simpleJson.put("hora", c.getFechaHoraCita().toLocalTime().toString());
                simpleJson.put("estado", c.getEstadoCita().toLowerCase());
                simpleJson.put("cliente", c.getNombreCliente());
                simpleJson.put("servicios", c.getServicios().stream().map(CitaDTO.ServicioDTO::getNombre).collect(Collectors.toList()));
                simpleJson.put("duracionTotal", c.getServicios().stream().mapToInt(CitaDTO.ServicioDTO::getDuracion).sum());
                return simpleJson;
            }).collect(Collectors.toList());
            successResponse(ctx, 200, "Citas del estilista recuperadas", response);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de estilista inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getByMonth(Context ctx) {
        try {
            int mes = Integer.parseInt(ctx.pathParam("mes"));
            int year = Integer.parseInt(ctx.pathParam("year"));
            List<CitaDTO> citas = citaService.findByMonth(mes, year);
            
            List<Map<String, Object>> citasJson = citas.stream().map(c -> {
                Map<String, Object> simpleJson = new HashMap<>();
                simpleJson.put("idCita", c.getIdCita());
                simpleJson.put("fecha", c.getFechaHoraCita().toLocalDate().toString());
                simpleJson.put("hora", c.getFechaHoraCita().toLocalTime().toString());
                simpleJson.put("cliente", c.getNombreCliente());
                simpleJson.put("estilista", c.getNombreEstilista());
                simpleJson.put("estado", c.getEstadoCita().toLowerCase());
                return simpleJson;
            }).collect(Collectors.toList());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("mes", mes);
            responseData.put("year", year);
            responseData.put("totalCitas", citas.size());
            responseData.put("citas", citasJson);

            successResponse(ctx, 200, "Citas del mes recuperadas", responseData);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "Mes o año inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    private void successResponse(Context ctx, int statusCode, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        ctx.status(statusCode).json(response);
    }

    private void errorResponse(Context ctx, int statusCode, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        ctx.status(statusCode).json(response);
    }
}
