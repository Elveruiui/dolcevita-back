package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.DireccionUsuario;
import com.example.repository.DireccionUsuarioRepository;

@Service
public class DireccionUsuarioService {
	@Autowired
	private DireccionUsuarioRepository direccionRepository;

    

    

    public List<DireccionUsuario> listarPorUsuario(Long idUsuario) {
        return direccionRepository.listarPorUsuario(idUsuario);
    }

   

    public DireccionUsuario guardar(DireccionUsuario direccion) {
        return direccionRepository.save(direccion);
    }

    public DireccionUsuario actualizar(Long id, DireccionUsuario detalles) {
        Optional<DireccionUsuario> optional = direccionRepository.findById(id);
        if (optional.isPresent()) {
            DireccionUsuario existente = optional.get();
            existente.setUsuario(detalles.getUsuario());
            existente.setDireccion(detalles.getDireccion());
            return direccionRepository.save(existente);
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (direccionRepository.existsById(id)) {
            direccionRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
