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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Pedido;
import com.example.service.PedidoService;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    
    @GetMapping("/listarPedidos")
    public ResponseEntity<?> listarPedidos() {
        try {
            List<Pedido> pedidos = pedidoService.listarAll();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar pedidos");
        }
    }

    @GetMapping("/listarPorUsuario/{idUsuario}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable("idUsuario") Long idUsuario) {
        try {
            List<Pedido> pedidos = pedidoService.listarPorUsuario(idUsuario);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al listar pedidos del usuario");
        }
    } 
    
    @GetMapping("/listarEstado/{estado}")
    public ResponseEntity<?> listarPedidoPorEstado(@PathVariable("estado") String estado) {
        try {
            List<Pedido> pedidos = pedidoService.listarPorEstado(estado);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar pedidos por estado");
        }
    }

    
    @GetMapping("/listarId/{id}")
    public ResponseEntity<?> listarPedId(@PathVariable("id") Long id) {
        try {
            Pedido pedido = pedidoService.listarID(id);
            if (pedido != null) {
                return ResponseEntity.ok(pedido);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar pedido por ID");
        }
    }

    
    @PostMapping("/crear")
    public ResponseEntity<?> guardar(@RequestBody Pedido pedido) {
        try {
            Pedido nuevoPedido = pedidoService.guardar(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar el pedido: " + e.getMessage());
        }
    }

    
    @PutMapping("/actualizarEstado/{id}")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        try {
            Pedido pedidoActualizado = pedidoService.actualizarEstado(id, estado);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar estado: " + e.getMessage());
        }
    }

    
    @PutMapping("/actualizarDetalles/{id}")
    public ResponseEntity<?> actualizarDetallesYFecha(@PathVariable Long id, @RequestBody Pedido pedido) {
        try {
            Pedido pedidoActualizado = pedidoService.actualizarDetallesYFecha(id, pedido);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar el pedido: " + e.getMessage());
        }
    }

   
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = pedidoService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok("Pedido eliminado con éxito");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado con el ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el pedido: " + e.getMessage());
        }
    }
}
