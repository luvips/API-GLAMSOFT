package org.pi.Services;

import org.pi.Models.Usuario;
import org.pi.Repositories.UsuarioRepository;

import java.sql.SQLException;

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario findUserById(int id) throws SQLException {
        return usuarioRepository.findUserById(id);
    }

    public Usuario findUserByEmail(String email) throws SQLException {
        return usuarioRepository.findUserByEmail(email);
    }

    public Usuario findUserByTelefono(String telefono) throws SQLException {
        return usuarioRepository.findUserByTelefono(telefono);
    }

    public Usuario saveUser(Usuario usuario) throws SQLException {
        return usuarioRepository.saveUser(usuario);
    }

    public boolean updateUser(Usuario usuario) throws SQLException {
        return usuarioRepository.updateUser(usuario);
    }

    public boolean deleteUser(int id) throws SQLException {
        return usuarioRepository.softDeleteUser(id);
    }
}
