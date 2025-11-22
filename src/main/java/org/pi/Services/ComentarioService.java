package org.pi.Services;

import org.pi.Models.Comentario;
import org.pi.Repositories.ComentarioRepository;

import java.sql.SQLException;
import java.util.List;

public class ComentarioService {
    private final ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    public List<Comentario> findAll() throws SQLException {
        return comentarioRepository.findAll();
    }

    public Comentario findById(int id) throws SQLException {
        return comentarioRepository.findById(id);
    }

    public List<Comentario> findByCliente(int idCliente) throws SQLException {
        return comentarioRepository.findComentariosByCliente(idCliente);
    }

    public List<Comentario> findLatest(int limit) throws SQLException {
        return comentarioRepository.findLatest(limit);
    }

    public int create(Comentario comentario) throws SQLException {
        // Las validaciones de datos se manejan en el controlador.
        return comentarioRepository.save(comentario);
    }

    public boolean update(int id, Comentario comentario) throws SQLException {
        comentario.setIdComentario(id);
        return comentarioRepository.update(comentario);
    }

    public boolean delete(int id) throws SQLException {
        return comentarioRepository.delete(id);
    }
}
