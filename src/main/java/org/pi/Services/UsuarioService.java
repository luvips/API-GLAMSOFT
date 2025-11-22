package org.pi.Services;

import org.pi.Models.Empleado;
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

    public int saveUser(Usuario usuario) throws SQLException {
        return usuarioRepository.saveUser(usuario);
    }

    public boolean deleteUser(int id) throws SQLException {
        return usuarioRepository.deleteUser(id);
    }

    public boolean updateUser(Usuario usuario) throws SQLException {
        if (usuario.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a cero.");
        }
        return usuarioRepository.updateUser(usuario);
    }
    
    // --- Métodos complejos ---

    public int saveEmpleadoCompleto(Empleado empleado) throws SQLException {
        return usuarioRepository.saveEmpleadoCompleto(empleado);
    }

    public void updateEmpleadoCompleto(Usuario usuario, Empleado empleado) throws SQLException {
        if (usuario.getIdUsuario() <= 0 || empleado.getIdEmpleado() <= 0) {
            throw new IllegalArgumentException("Los IDs de usuario y empleado son obligatorios.");
        }
        usuarioRepository.updateEmpleado(usuario, empleado);
    }
}
