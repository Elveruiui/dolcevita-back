package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long>{
	@Query("SELECT d FROM DetallePedido d WHERE d.pedido.id_pedido = :idPedido")
    List<DetallePedido> listarPorPedido(@Param("idPedido") Long idPedido);
}
