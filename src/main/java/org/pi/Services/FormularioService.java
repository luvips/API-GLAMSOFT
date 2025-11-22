package org.pi.Services;

import org.pi.Models.Formulario;
import org.pi.Repositories.FormularioRepository;

import java.sql.SQLException;
import java.util.List;

public class FormularioService {
    private final FormularioRepository formularioRepository;

    public FormularioService(FormularioRepository formularioRepository) {
        this.formularioRepository = formularioRepository;
    }

    public List<Formulario> findAll() throws SQLException {
        return formularioRepository.findAll();
    }

    public Formulario findById(int id) throws SQLException {
        return formularioRepository.findById(id);
    }

    public Formulario create(Formulario formulario) throws SQLException {
        return formularioRepository.save(formulario);
    }

    public boolean update(int id, Formulario formulario) throws SQLException {
        formulario.setIdFormulario(id);
        return formularioRepository.update(formulario);
    }

    public boolean delete(int id) throws SQLException {
        return formularioRepository.softDelete(id);
    }
}
