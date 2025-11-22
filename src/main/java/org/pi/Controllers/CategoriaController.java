package org.pi.Controllers;

import io.javalin.http.Context;
import org.pi.Models.Categoria;
import org.pi.Services.CategoriaService;
import org.pi.dto.CategoriaDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    public void getAll(Context ctx) {
        try {
            List<CategoriaDTO> categorias = categoriaService.findAll();
            successResponse(ctx, 200, "Categorías recuperadas", categorias);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            CategoriaDTO categoria = categoriaService.findById(id);
            if (categoria == null) {
                errorResponse(ctx, 404, "Categoría no encontrada.");
                return;
            }
            successResponse(ctx, 200, "Categoría encontrada", categoria);
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de categoría inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Categoria categoria = ctx.bodyAsClass(Categoria.class);
            if (categoria.getNombreCategoria() == null || categoria.getNombreCategoria().trim().isEmpty()) {
                errorResponse(ctx, 400, "El nombre de la categoría es obligatorio.");
                return;
            }
            
            Categoria categoriaCreada = categoriaService.create(categoria);
            
            Map<String, Object> data = new HashMap<>();
            data.put("idCategoria", categoriaCreada.getIdCategoria());
            data.put("nombre", categoriaCreada.getNombreCategoria());

            successResponse(ctx, 201, "Categoría creada exitosamente", data);
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Categoria categoria = ctx.bodyAsClass(Categoria.class);
            
            if (categoriaService.findById(id) == null) {
                errorResponse(ctx, 404, "Categoría no encontrada para actualizar.");
                return;
            }

            if (categoriaService.update(id, categoria)) {
                successResponse(ctx, 200, "Categoría actualizada exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo actualizar la categoría.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de categoría inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            errorResponse(ctx, 400, "Datos de solicitud inválidos: " + e.getMessage());
        }
    }

    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (categoriaService.findById(id) == null) {
                errorResponse(ctx, 404, "Categoría no encontrada para eliminar.");
                return;
            }
            if (categoriaService.delete(id)) {
                successResponse(ctx, 200, "Categoría eliminada exitosamente", null);
            } else {
                errorResponse(ctx, 500, "No se pudo eliminar la categoría.");
            }
        } catch (NumberFormatException e) {
            errorResponse(ctx, 400, "ID de categoría inválido.");
        } catch (SQLException e) {
            errorResponse(ctx, 500, "Error de base de datos: " + e.getMessage());
        }
    }

    // --- Métodos de ayuda ---

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
