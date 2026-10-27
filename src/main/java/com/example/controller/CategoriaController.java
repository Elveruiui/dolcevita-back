package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Categoria;
import com.example.service.CategoriaService;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {
	
	@Autowired
	private CategoriaService categoriaService;
	
	@GetMapping("/listarCategorias")
	public ResponseEntity<?> listarCategorias(){
		try {
			List<Categoria> categorias =categoriaService.listarAll();
			return ResponseEntity.ok(categorias);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar categorias");
		}
	}
	
	@GetMapping("/listarNombre/{nombre}")
	public ResponseEntity<?> listarCatNombre(@PathVariable("nombre") String nombre){
		try {
			List<Categoria> categoria =categoriaService.listarporNombre(nombre);
			return ResponseEntity.ok(categoria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar categorias");
		}
	}
	
	@GetMapping("/listarId/{id}")
	public ResponseEntity<?> listarCatId(@PathVariable("id" )Long id){
		try {
			Categoria categoria =categoriaService.listarID(id);
			return ResponseEntity.ok(categoria);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar categorias");
		}
	}
	
	@PostMapping("/crear") 
    public ResponseEntity<?> guardar(@RequestBody Categoria categoria) {
        try {
            Categoria nuevaCategoria = categoriaService.guardar(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar la categoría");
        }
    }

    
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
        try {
            Categoria categoriaActualizada = categoriaService.actualizar(id, categoria);
            if (categoriaActualizada != null) {
                return ResponseEntity.ok(categoriaActualizada);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la categoría");
        }
    }

    
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = categoriaService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok("Categoría eliminada con éxito"); 
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la categoría");
        }
    }
	
	
	
	
	
	
	

}
