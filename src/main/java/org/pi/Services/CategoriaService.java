package org.pi.Services;

import org.pi.Models.Categoria;
import org.pi.Models.Servicio;
import org.pi.Repositories.CategoriaRepository;
import org.pi.dto.CategoriaDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    private CategoriaDTO toDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setNombre(categoria.getNombreCategoria());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setActivo(categoria.isActivo());
        return dto;
    }

    public List<CategoriaDTO> findAll() throws SQLException {
        return categoriaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO findById(int id) throws SQLException {
        Categoria categoria = categoriaRepository.findById(id);
        if (categoria == null) {
            return null;
        }
        CategoriaDTO dto = toDTO(categoria);
        List<Servicio> servicios = categoriaRepository.findServiciosByCategoriaId(id);
        List<CategoriaDTO.ServicioSimpleDTO> servicioDTOs = servicios.stream()
                .map(s -> new CategoriaDTO.ServicioSimpleDTO(s.getIdServicio(), s.getNombreServicio(), s.getPrecio()))
                .collect(Collectors.toList());
        dto.setServicios(servicioDTOs);
        return dto;
    }

    public Categoria create(Categoria categoria) throws SQLException {
        return categoriaRepository.save(categoria);
    }

    public boolean update(int id, Categoria categoria) throws SQLException {
        categoria.setIdCategoria(id);
        return categoriaRepository.update(categoria);
    }

    public boolean delete(int id) throws SQLException {
        return categoriaRepository.softDelete(id);
    }
}
