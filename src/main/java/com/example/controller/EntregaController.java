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

import com.example.model.Entrega;
import com.example.service.EntregaService;

@RestController
@RequestMapping("/entrega")
public class EntregaController {

    @Autowired
    private EntregaService entregaService;

    @GetMapping("/listarEntregas")
    public ResponseEntity<?> listarEntregas() {
        try {
            List<Entrega> entregas = entregaService.listarAll();
            return ResponseEntity.ok(entregas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar entregas");
        }
    }

    @GetMapping("/listarNombre/{nombre}")
    public ResponseEntity<?> listarEntNombreMotorizado(@PathVariable("nombre") String nombre) {
        try {
            List<Entrega> entregas = entregaService.listarporNombre(nombre);
            return ResponseEntity.ok(entregas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar entregas por motorizado");
        }
    }

    @GetMapping("/listarPorMotorizado/{idMotorizado}")
    public ResponseEntity<?> listarPorMotorizado(@PathVariable("idMotorizado") Long idMotorizado) {
        try {
            List<Entrega> entregas = entregaService.listarPorMotorizado(idMotorizado);
            return ResponseEntity.ok(entregas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar entregas del motorizado");
        }
    }

    @GetMapping("/listarId/{id}")
    public ResponseEntity<?> listarEntId(@PathVariable("id") Long id) {
        try {
            Entrega entrega = entregaService.listarID(id);
            if (entrega != null) {
                return ResponseEntity.ok(entrega);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entrega no encontrada con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar entrega por ID");
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> guardar(@RequestBody Entrega entrega) {
        try {
            Entrega nueva = entregaService.guardar(entrega);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar la entrega");
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Entrega entrega) {
        try {
            Entrega actualizada = entregaService.actualizar(id, entrega);
            if (actualizada != null) {
                return ResponseEntity.ok(actualizada);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entrega no encontrada con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la entrega");
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = entregaService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok("Entrega eliminada con éxito");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entrega no encontrada con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la entrega");
        }
    }
}