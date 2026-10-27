package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.DetallePedido;
import com.example.repository.DetallePedidoRepository;

@Service
public class DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detalleRepository;

    

    

    public List<DetallePedido> listarPorPedido(Long idPedido) {
        return detalleRepository.listarPorPedido(idPedido);
    }

    public DetallePedido listarID(Long id) {
        return detalleRepository.findById(id).orElse(null);
    }

    public DetallePedido guardar(DetallePedido detalle) {
        return detalleRepository.save(detalle);
    }

    public DetallePedido actualizar(Long id, DetallePedido detalles) {
        Optional<DetallePedido> optional = detalleRepository.findById(id);
        if (optional.isPresent()) {
            DetallePedido existente = optional.get();
            existente.setPedido(detalles.getPedido());
            existente.setProducto(detalles.getProducto());
            existente.setCantidad(detalles.getCantidad());
            existente.setPrecio_unitario(detalles.getPrecio_unitario());
            existente.setSubtotal(detalles.getSubtotal());
            return detalleRepository.save(existente);
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (detalleRepository.existsById(id)) {
            detalleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}