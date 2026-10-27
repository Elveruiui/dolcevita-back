package com.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Entrega;
import com.example.repository.EntregaRepository;

@Service
public class EntregaService {

    @Autowired
    private EntregaRepository entregaRepository;

    public List<Entrega> listarAll() {
        return entregaRepository.findAll();
    }

    public List<Entrega> listarporNombre(String nombreMotorizado) {
        return entregaRepository.buscarPorNombreMotorizado(nombreMotorizado);
    }

    public List<Entrega> listarPorMotorizado(Long idMotorizado) {
        return entregaRepository.listarPorMotorizado(idMotorizado);
    }

    public Entrega listarID(Long id) {
        return entregaRepository.findById(id).orElse(null);
    }

    public Entrega guardar(Entrega entrega) {
        if (entrega.getFecha_asignacion() == null) {
            entrega.setFecha_asignacion(LocalDateTime.now());
        }
        return entregaRepository.save(entrega);
    }

    public Entrega actualizar(Long id, Entrega detalles) {
        Optional<Entrega> optional = entregaRepository.findById(id);
        if (optional.isPresent()) {
            Entrega existente = optional.get();
            existente.setPedido(detalles.getPedido());
            existente.setMotorizado(detalles.getMotorizado());
            existente.setFecha_entrega(detalles.getFecha_entrega());
            existente.setEstado(detalles.getEstado());
            existente.setCosto_delivery(detalles.getCosto_delivery());
            existente.setObservacion(detalles.getObservacion());
            return entregaRepository.save(existente);
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (entregaRepository.existsById(id)) {
            entregaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
