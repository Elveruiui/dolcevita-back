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

import com.example.model.DetallePedido;
import com.example.service.DetallePedidoService;

@RestController
@RequestMapping("/detallepedido")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detalleService;

    

    

    @GetMapping("/listarPorPedido/{idPedido}")
    public ResponseEntity<?> listarPorPedido(@PathVariable("idPedido") Long idPedido) {
        try {
            List<DetallePedido> detalles = detalleService.listarPorPedido(idPedido);
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar detalles del pedido");
        }
    }

    @GetMapping("/listarId/{id}")
    public ResponseEntity<?> listarDetId(@PathVariable("id") Long id) {
        try {
            DetallePedido detalle = detalleService.listarID(id);
            if (detalle != null) {
                return ResponseEntity.ok(detalle);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar detalle por ID");
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> guardar(@RequestBody DetallePedido detalle) {
        try {
            DetallePedido nuevo = detalleService.guardar(detalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el detalle de pedido");
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody DetallePedido detalle) {
        try {
            DetallePedido actualizado = detalleService.actualizar(id, detalle);
            if (actualizado != null) {
                return ResponseEntity.ok(actualizado);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el detalle de pedido");
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = detalleService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok("Detalle eliminado con éxito");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Detalle no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el detalle de pedido");
        }
    }
}
