package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Categoria;
import com.example.repository.CategoriaRepository;



@Service
public class CategoriaService {
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public List<Categoria> listarAll(){
		return categoriaRepository.findAll();
	}
	
	public List<Categoria> listarporNombre(String nombre){
		return categoriaRepository.findByNombre(nombre);
	}
	
	public Categoria listarID(Long id){
		return categoriaRepository.findById(id).orElseGet(null);
	}
	
	public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
	
	public Categoria actualizar(Long id, Categoria categoriaDetalles) {
        Optional<Categoria> optional = categoriaRepository.findById(id);
        if (optional.isPresent()) {
            Categoria categoriaExistente = optional.get();
            categoriaExistente.setNombre(categoriaDetalles.getNombre());
            categoriaExistente.setDescripcion(categoriaDetalles.getDescripcion());
            categoriaExistente.setEstado(categoriaDetalles.getEstado()); // 1 o 0
            return categoriaRepository.save(categoriaExistente);
        }
        return null;
    }
	
	public boolean eliminar(Long id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
