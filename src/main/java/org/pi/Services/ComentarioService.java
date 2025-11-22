package org.pi.Services;

import org.pi.Models.Comentario;
import org.pi.Repositories.ComentarioRepository;
import org.pi.dto.ComentarioDTO;

import java.sql.SQLException;
import java.util.List;

public class ComentarioService {
    private final ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    public List<ComentarioDTO> findAll() throws SQLException {
        return comentarioRepository.findAll();
    }

    public ComentarioDTO findById(int id) throws SQLException {
        return comentarioRepository.findById(id);
    }


    public List<ComentarioDTO> findByCliente(int idCliente) throws SQLException {
        return comentarioRepository.findByCliente(idCliente);
    }

    public Comentario create(Comentario comentario) throws SQLException {
        return comentarioRepository.save(comentario);
    }

    public boolean update(int id, Comentario comentario) throws SQLException {
        comentario.setIdComentario(id);
        return comentarioRepository.update(comentario);
    }

    public boolean delete(int id) throws SQLException {
        return comentarioRepository.softDelete(id);
    }
}
