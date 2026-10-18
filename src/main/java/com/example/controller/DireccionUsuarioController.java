package com.example.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.model.DireccionUsuario;
import com.example.service.DireccionUsuarioService;

@RestController
@RequestMapping("/direccion")
public class DireccionUsuarioController {
	
	@Autowired
    private DireccionUsuarioService direccionService;

   

    @GetMapping("/listarPorUsuario/{idUsuario}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable("idUsuario") Long idUsuario) {
        try {
            List<DireccionUsuario> direcciones = direccionService.listarPorUsuario(idUsuario);
            return ResponseEntity.ok(direcciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar direcciones del usuario");
        }
    }

    

    @PostMapping("/crear")
    public ResponseEntity<?> guardar(@RequestBody DireccionUsuario direccion) {
        try {
            DireccionUsuario nueva = direccionService.guardar(direccion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar la dirección");
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody DireccionUsuario direccion) {
        try {
            DireccionUsuario actualizada = direccionService.actualizar(id, direccion);
            if (actualizada != null) {
                return ResponseEntity.ok(actualizada);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dirección no encontrada con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la dirección");
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = direccionService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok("Dirección eliminada con éxito");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dirección no encontrada con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la dirección");
        }
    }

}
