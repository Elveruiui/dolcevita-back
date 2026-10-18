package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;

@Service
public class UsuarioService {
	@Autowired
	private UsuarioRepository usuarioR;
	
	public List<Usuario> listarAll() {
        return usuarioR.findAll();
    }

    public List<Usuario> listarporNombre(String nombre) {
        return usuarioR.findByNombre(nombre);
    }

    public Usuario listarID(Long id) {
        return usuarioR.findById(id).orElse(null);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioR.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario detalles) {
        Optional<Usuario> optional = usuarioR.findById(id);
        if (optional.isPresent()) {
            Usuario existente = optional.get();
            existente.setRol(detalles.getRol());
            existente.setNombre(detalles.getNombre());
            existente.setApellido(detalles.getApellido());
            existente.setCorreo(detalles.getCorreo());
            existente.setTelefono(detalles.getTelefono());
            existente.setContrasena(detalles.getContrasena());
            existente.setEstado(detalles.getEstado());
            return usuarioR.save(existente);
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (usuarioR.existsById(id)) {
        	usuarioR.deleteById(id);
            return true;
        }
        return false;
    }

}
